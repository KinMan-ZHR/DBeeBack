## 项目介绍
Dbee是一个轻量级、简单易用的云应用管理平台，具有多云和多环境管理、应用管理和部署、服务治理等功能，使用该平台不需要了解容器和k8s的概念。

## 主要特性
* 简洁的操作界面
* 以应用为中心，屏蔽K8S的底层概念
* 支持SpringBoot、Vue、React、Nodejs、Html、Go、Flask、Django、Nuxt、Next、.Net应用部署
* 无需安装Docker即可构建镜像
* 无需安装Maven、Gradle、Node、Go、Python、Nodejs、.Net即可打包
* 支持多集群管理
* 支持多环境部署
* 支持日志收集
* 支持链路追踪

## 快速开始

1.环境要求

| Java | kubernetes | Harbor |
| :----: | :----: | :----: |
| >=8 | [1.18, 1.30] | >=2.0.0 |

3.解压

```bash
$  tar zxvf dbee-*.tar.gz
```

4.查看文件列表

```bash
$  cd dbee-* && ls -l
total 24
drwxr-xr-x 2 root root   115 Oct  6 19:56 bin
drwxr-xr-x 2 root root    48 Oct  6 19:56 conf
drwxr-xr-x 3 root root    46 Oct  6 19:56 lib
-rw-r--r-- 1 root root 11558 Dec 10  2021 LICENSE
-rw-r--r-- 1 root root  5141 Dec 26  2021 NOTICE
-rw-r--r-- 1 root root  1337 Jan 21  2022 README.txt
drwxr-xr-x 8 root root    93 Sep 23 16:09 static
```

5.启动服务

```bash
$  bin/dbee-start.sh
```

6.在浏览器里输入地址：`http://127.0.0.1:8100`，并输入账号/密码：admin/admin，登录之后如下图所示：


7.最后，关闭服务

```bash
$  bin/dbee-stop.sh
```

## 开源许可

本软件遵守Apache开源许可协议2.0，详情《 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)》。