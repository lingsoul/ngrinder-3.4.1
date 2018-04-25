【工具简介】
    nGrinder是韩国NAVER公司(韩国最大互联网公司NHN旗下搜索引擎网站)基于Grinder二次开发的一个性能测试工具。
nGrinder 在 Grinder 基础上：
* 实现多测试并行
* 基于web的管理
* 实现cluster（集群）
* 方便的脚本编辑、管理
* 支持Groovy的脚本，相对于Jython，可以启用更多的虚拟用户
* 目标服务器的监控
* 插件系统扩展


【修改者】 凌键（lingj1992@163.com）
* 此版本主要源于nGrinder 3.4.1的基础上进行修改，主要修改点如下：
* 【bug修复】:
    * 1、网络资源统计方法（received、sent）的bug；
    * 2、ngrinder控制台页面访问availableAgentCount返回404的bug；
    * 3、csv文件默认分隔符","的问题；
* 【新增】：
    * 4、新增收集服务器的资源使用率指标（cpu使用率（100%-idle）、内存使用率、IO util%、Load、CPU等待率、磁盘读写速率）；
    * 5、新增测试报告页面交易成功率指标；
    * 6、新增测试配置页面的配置项描述，新增采样间隔180秒选项；
    * 7、新增最近一次执行的日志收集，代理服务器日志路径：/${NGRINDER_HOME}/.ngrinder_agent/log/recent_log；
    * 8、新增GrinderUtils工具类方法，如并发造数、csv文件参数化、动态数据关联等方法，为后续全链路测试做准备；
* 【修改】：
    * 9、修改快速测试脚本模板的断言方式，新增日志级别设置；
    * 10、修改nGrinder默认配置文件system.conf参数：主要涉及并发数、执行时长等配置；

【注】：在版本更新后，需重新下载代理包、监控包进行部署。或可以通过以下方式进行更新：
 * 代理更新：
    * 1、打开右上角“系统配置”，打开"#"注释，设置controller.agent_force_update=true
    * 2、进入“代理管理”页面，勾选代理，点击“更新代理”按钮并确定。
 
 * 监控更新：
     * 1、停止监控服务，进入监控包部署路径：ngrinder-monitor/lib目录，用最新的ngrinder-core-3.4.1.jar文件进行替换；
     * 2、重启监控服务。


版本源码地址： https://github.com/lingsoul/ngrinder

版本下载地址： https://github.com/lingsoul/ngrinder/releases

nGrinder源码地址： https://github.com/naver/ngrinder

nGrinder 
========

[![Join the chat at https://gitter.im/naver/ngrinder](https://badges.gitter.im/naver/ngrinder.svg)](https://gitter.im/naver/ngrinder?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


nGrinder is a platform for stress tests that enables you to execute script creation, test execution, monitoring, and result report generator simultaneously. The open-source nGrinder offers easy ways to conduct stress tests by eliminating inconveniences and providing integrated environments.


Want to know what's changed from the original grinder platform?
 * Checkout https://github.com/naver/ngrinder/wiki/Architecture !

To get to know what's different from previous ngrinder 2.0?
 * Checkout http://www.slideshare.net/junhoyoon3994/ngrinder-30-load-test-even-kids-can-do !

To get started,
 * Checkout https://github.com/naver/ngrinder/wiki/User-Guide !

You can find out what nGrinder looks like with screen-shot.
 * https://github.com/naver/ngrinder/wiki/Screen-Shot

nGrinder consists of two major components. 

nGrinder controller
 * a web application that enables the performance tester to create a test script and configure a test run

nGrinder agent
* a virtual user generator that creates loads.

Features
--------

* Use Jython script to create test scenario and generate stress in JVM using multiple agents.
* Extend tests with custom libraries(jar, py). It's unlimited practically.
* Provide web-based interface for project management, monitoring, result management and report management.
* Run multiple tests concurrently. Assign the pre-installed multiple agents to maximize each agent's utilization.
* Deploy agents on multiple network regions. Execute tests on various network locations
* Embed Subversion to manage scripts.
* Allow to monitor the state of agents generating stress and target machines receiving stress
* Proven solution which is used to test huge systems having more than 100 million users in NHN.


Download
--------

You can download the latest nGrinder in the following link. 
* https://github.com/naver/ngrinder/releases

Documentation
-------------
You can find the installation guide at the following link.
* https://github.com/naver/ngrinder/wiki/Installation-Guide

You can find the user guide at the following location link.
* https://github.com/naver/ngrinder/wiki/User-Guide



Contribution?
-------------
nGrinder welcomes any contributions from users. Please make all pull requests against master branches.
* Clone the REPO : 'git clone git://github.com/naver/ngrinder.git'

You can find general developer documents at the following link.
 * https://github.com/naver/ngrinder/wiki/Dev-Document

Versioning
----------

For transparency and insight into our release cycle, and to strive to maintain backward compatibility, Bootstrap will be maintained under the Semantic Versioning guidelines to the greatest extent possible.

Releases will be numbered in the following format:

      `<major>.<minor>.<patch>`

Release will be constructed based on the following guidelines:

* Breaking backward compatibility bumps the major (and resets the minor and patch)
* New additions without breaking backward compatibility bump the minor (and reset the patch)
* Bug fixes and misc. changes bump the patch


Q/A and Bug tracker
-------------------
Found the apparent bug? Got a brilliant idea for an enhancement? Please create an issue here on GitHub so you can notify us!
* https://github.com/naver/ngrinder/issues

You can join our forum as well
* Dev : http://ngrinder.642.n7.nabble.com/ngrinder-dev-f1.html 
* User Forum : http://ngrinder.642.n7.nabble.com/ngrinder-user-f50.html
* 中文论坛 (Chinese) http://ngrinder.642.n7.nabble.com/ngrinder-user-cn-f114.html
* 한국어 유저 포럼 (Korean) http://ngrinder.642.n7.nabble.com/ngrinder-user-kr-f113.html
* [![Developer chat at https://gitter.im/naver/ngrinder-kr](https://badges.gitter.im/naver/ngrinder-kr.svg)](https://gitter.im/naver/ngrinder-kr?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

---------------------

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License. 
      
   
nGrinder includes the following software and libraries as follows. See the LICENSE folder for the license and copyright details for each.
* https://github.com/naver/ngrinder/tree/master/license
