package kinman.dbee.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * JVM指标收集代理
 * 用于收集Java应用的JVM运行时指标并上报到DBee服务器
 * 启动方式：java -javaagent:dbee-agent.jar
 * 
 * @author KinMan
 */
public class JvmMetricsAgent {

	private static final Logger logger = Logger.getLogger("JvmMetricsAgent");
	
	private static final String DBEE_SERVER_URL_FILE_PATH = "/usr/local/data/dbee-server-url";

	// 服务器URL的线程安全列表
	private static final List<String> DBEE_URL = new CopyOnWriteArrayList<>();
	
    private static long lastModifiedTime;

     /**
     * Java Agent入口方法，在目标应用的main方法执行前调用
     * 初始化监控任务，定期收集JVM指标并上报
     * 
     * @param args Agent启动参数
     * @param inst 提供检测Java编程语言代码的服务
     */
	public static void premain(String args, Instrumentation inst) {
		
		loadDBeeIp();
		
		String hostName = null;
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.warning(String.format("Failed to get localhost name, message: %s", e));
		}
		
		final String replicaName = hostName;
		ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
		scheduled.scheduleAtFixedRate(() -> {
			if(DBEE_URL.size() == 0) {
				//如果没有dbee服务器的url，则重新读取DBee的Url一次
				loadDBeeIp();
			}
			
			//如果仍然没有dbee服务器的url，则不上报
			if(DBEE_URL.size() == 0) {
				logger.warning(String.format("Failed to push metrics data, none dbee server exists"));
				return;
			}
			
			List<Metrics> metricsList = new ArrayList<>(16);
			memoryHeap(replicaName, metricsList);
			memoryPool(replicaName, metricsList);
			gc(replicaName, metricsList);
			thread(replicaName, metricsList);
			
			if(pushMetrics(metricsList)) {
				//如果没有上报成功，重新读取DBee的Url并重新上报一次
				loadDBeeIp();
				pushMetrics(metricsList);
			}
		}, 0, 10, TimeUnit.SECONDS);
		
		//监控文件变化
		watchFile();
	}
	/**
     * 从配置文件加载DBee服务器IP地址
     * 读取文件内容，解析IP地址，并构建完整的URL
     */
	private static void loadDBeeIp() {
		try(InputStream in = new FileInputStream(DBEE_SERVER_URL_FILE_PATH)){
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			String ipStr = new String(buffer, "UTF-8");
			DBEE_URL.clear();
			if(ipStr != null && !"".equals(ipStr)) {
				String[] ips = ipStr.split(",");
				for(String ip : ips) {
					DBEE_URL.add("http://" + ip + "/app/env/replica/metrics/add");
				}
			}
		} catch (Exception e) {
			logger.warning(String.format("Failed to load dbee url, message: %s", e));
		}
	}
	
	 /**
     * 收集堆内存使用情况
     * 包括已使用内存和最大可用内存
     * 
     * @param replicaName 实例名称
     * @param metricsList 指标列表，用于存储收集的指标
     */
	private static void memoryHeap(String replicaName, List<Metrics> metricsList) {
		MemoryUsage m = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		item(MetricsTypeEnum.HEAP_MEMORY_USED, replicaName, metricsList, m.getUsed());
		item(MetricsTypeEnum.HEAP_MEMORY_MAX, replicaName, metricsList, m.getMax());
	}

	 /**
     * 收集内存池使用情况
     * 包括年轻代和元空间的使用情况
     * 
     * @param replicaName 实例名称
     * @param metricsList 指标列表，用于存储收集的指标
	 */
	private static void memoryPool(String replicaName, List<Metrics> metricsList) {
		List<MemoryPoolMXBean> mpxbs = ManagementFactory.getMemoryPoolMXBeans();
		long young = 0L;
		for (MemoryPoolMXBean mp : mpxbs) {
			String name = mp.getName().toLowerCase();
			if (name.contains("metaspace")) {
				item(MetricsTypeEnum.META_MEMORY_MAX, replicaName, metricsList, mp.getUsage().getMax());
				item(MetricsTypeEnum.META_MEMORY_USED, replicaName, metricsList, mp.getUsage().getUsed());
				continue;
			}
			if (name.contains("eden") || name.contains("SURVIVOR")) {
				young += mp.getUsage().getUsed();
				continue;
			}
		}
		
		//年轻代
		item(MetricsTypeEnum.YOUNG, replicaName, metricsList, young);
	}
	
	/**
     * 收集垃圾回收器使用情况
     * 包括垃圾回收次数和总时间
     * 
     * @param replicaName 实例名称
     * @param metricsList 指标列表，用于存储收集的指标
	 */
	private static void gc(String replicaName, List<Metrics> metricsList) {
		List<GarbageCollectorMXBean> gcbs = ManagementFactory.getGarbageCollectorMXBeans();
		long size = 0L;
		long duration = 0L;
		for (GarbageCollectorMXBean gcb : gcbs) {
			size += gcb.getCollectionCount();
			duration += gcb.getCollectionTime();
		}
		
		item(MetricsTypeEnum.GC_SIZE, replicaName, metricsList, size);
		item(MetricsTypeEnum.GC_DURATION, replicaName, metricsList, duration);
	}

	/**
     * 收集线程使用情况
     * 包括线程总数、守护线程数、阻塞线程数和死锁线程数
     * 
     * @param replicaName 实例名称
     * @param metricsList 指标列表，用于存储收集的指标
	 */
	private static void thread(String replicaName, List<Metrics> metricsList) {
		ThreadMXBean txb = ManagementFactory.getThreadMXBean();

		item(MetricsTypeEnum.THREAD, replicaName, metricsList, (long) txb.getThreadCount());
		item(MetricsTypeEnum.THREAD_DAEMON, replicaName, metricsList, (long) txb.getDaemonThreadCount());

		ThreadInfo[] threads = txb.getThreadInfo(txb.getAllThreadIds());
		long bcValue = 0;
		if (threads != null) {
			for (ThreadInfo t : threads) {
				if (t != null && Thread.State.BLOCKED.equals(t.getThreadState())) {
					bcValue++;
				}
			}
			item(MetricsTypeEnum.THREAD_BLOCKED, replicaName, metricsList, bcValue);
		}

		long[] dlThreads = txb.findDeadlockedThreads();
		long dlSize = (long) (dlThreads == null ? 0 : dlThreads.length);
		item(MetricsTypeEnum.THREAD_DEADLOCKED, replicaName, metricsList, dlSize);
	}
	
	/**
     * 添加指标项
     * 
     * @param metricsType 指标类型
     * @param replicaName 实例名称
     * @param metricsList 指标列表，用于存储收集的指标
	 */
	private static void item(MetricsTypeEnum metricsType, String replicaName,
			List<Metrics> metricsList, long metricsValue) {
		Metrics used = new Metrics();
		used.setReplicaName(replicaName);
		used.setMetricsType(metricsType.getCode());
		used.setMetricsValue(metricsValue);
		metricsList.add(used);
	}
	
	/**
     * 推送指标数据到DBee服务器
     * 
     * @param metricsList 指标列表，用于存储收集的指标
	 * @return 推送成功与否
	 */
	public static boolean pushMetrics(List<Metrics> metricsList) {
		String url = DBEE_URL.get(new Random().nextInt(DBEE_URL.size()));
		return HttpUtils.post(url, metricsList.toString());
	}
	
	 /**
     * 监控文件变化
     * 当文件内容发生变化时，重新读取DBee的Url
     */
    public static void watchFile() {
        File file = new File(DBEE_SERVER_URL_FILE_PATH);
        lastModifiedTime = file.lastModified();
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        scheduled.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (file.lastModified() > lastModifiedTime || DBEE_URL.size() == 0) {
                	logger.info(String.format("The file %s has changed", DBEE_SERVER_URL_FILE_PATH));
					loadDBeeIp();
                    lastModifiedTime = file.lastModified();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}