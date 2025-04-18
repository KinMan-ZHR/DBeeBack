package kinman.dbee.rest;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 引导服务入口。
 *
 */
@ServletComponentScan
@EnableScheduling
@PropertySource(value = {"classpath:dbee.yml",
		"classpath:application-private.yml"})
@SpringBootApplication(scanBasePackages = {
		"kinman.dbee.infrastructure",
		"kinman.dbee.application",
		"kinman.dbee.rest"})
public class DBeeBootstrap {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(DBeeBootstrap.class);
		springApplication.setDefaultProperties(Collections.singletonMap("server.port", "8100"));
		//这里也可以通过SpringBoot写入pid文件，目前通过脚本写入
		//springApplication.addListeners(new ApplicationPidFileWriter("pid_file_path"));
		springApplication.run(args);
	}
}