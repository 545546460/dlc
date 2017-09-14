# Distributed Log Collected用户指南
***
## 目录
> ### 1. dlc入门
* 1.1. dlc结构介绍
* 1.2. dlc依赖的其他构件
> ### 2. dlc-core配置
* 2.1. 引入dlc-core构件
* 2.2. 配置dlc-ignite文件
* 2.3. 日志组件文件引入Lucene Appender
* 2.4. 发布dlc服务
> ### 3.dlc-web部署
* 3.1. dlc-web配置
* 3.2. dlc-web一键式部署
> ### 4. dlc查询语法
> ### 5. 日志源设置
* 5.1. 新增日志源
* 5.2. 日志源列表
***
## 1. dlc入门
### 1.1. dlc结构介绍
dlc分布式日志搜集系统结构分为两部分:dlc-web（客户端）和dlc-core（日志搜集组件，与应用集成），dlc-web的作用是从日志搜集组件获取到相关日志并在前端展示，dlc-web可以配置从哪个系统获取设置等等，dcl-web采用应用闭环部署，一键部署，部署简单；dlc-core日志搜集核心组件，与应用系统集成，随着应用系统的启动，会在相同应用系统自动建立集群，写日志时，建立lucene索引，便于后面日志搜集，提供配置哪些字段需要建立索引，配置灵活，方便。

### 1.2. dlc依赖的其他构件
dlc-core主要依赖的其他构件有apache ignite、log4j2以及lucene，dlc-core主要依赖的其他构件有Spring boot、alibaba的durid、mybatis等

## 2. dlc-core配置
### 2.1. 引入dlc-core构件
dlc-core与应用集成，需在应用系统的pom文件中引入dlc-core构件：
```
<dependency>
	<groupId>com.happygo.dlc</groupId>
	<artifactId>dlc-core</artifactId>
	<version>最新版本</version>
</dependency>
```
### 2.2. 配置dlc-ignite文件
在应用系统中src/main/resources下建立一个目录，命名为config，在config下新建一个dlc-default.xml以及dlc-ignite.xml文件，下面两个xml文件的配置，拿来即用，需将```<value>127.0.0.1:47500..47509</value>```替换成真实IP以及如下中的value替换成对应应用名称，key无需替换
```
<map>
    <entry key="ROLE" value="dlc-example"/>
</map>
```
模板如下：
dlc-default.xml
```
<?xml version="1.0" encoding="UTF-8"?>

<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!--
    Ignite configuration with all defaults and enabled p2p deployment and enabled events.
-->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/util
        http://www.springframework.org/schema/util/spring-util.xsd">

    <!-- Datasource for sample in-memory H2 database. -->
    <bean id="h2-dlc-db" class="org.h2.jdbcx.JdbcDataSource">
        <property name="URL" value="jdbc:h2:tcp://localhost/mem:DlcDb" />
        <property name="user" value="sa" />
    </bean>

    <bean abstract="true" id="ignite.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
        <!-- Set to true to enable distributed class loading for examples, default is false. -->
        <property name="peerClassLoadingEnabled" value="true"/>

        <!-- set cluster node attributes-->
        <property name="userAttributes">
            <map>
                <entry key="ROLE" value="dlc-example"/>
            </map>
        </property>

        <!-- Enable task execution events for examples. -->
        <property name="includeEventTypes">
            <list>
                <!--Task execution events-->
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_STARTED"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_FINISHED"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_FAILED"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_TIMEDOUT"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_SESSION_ATTR_SET"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_TASK_REDUCED"/>

                <!--Cache events-->
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_PUT"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_READ"/>
                <util:constant static-field="org.apache.ignite.events.EventType.EVT_CACHE_OBJECT_REMOVED"/>
            </list>
        </property>

        <!-- Explicitly configure TCP discovery SPI to provide list of initial nodes. -->
        <property name="discoverySpi">
            <bean class="org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi">
                <property name="ipFinder">
                    <!--
                        Ignite provides several options for automatic discovery that can be used
                        instead os static IP based discovery. For information on all options refer
                        to our documentation: http://apacheignite.readme.io/docs/cluster-config
                    -->
                    <!-- Uncomment static IP finder to enable static-based discovery of initial nodes. -->
                    <!--<bean class="org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder">-->
                    <bean class="org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder">
                        <property name="addresses">
                            <list>
                                <!-- 替换成部署机器的真实IP -->
                                <value>127.0.0.1:47500..47509</value>
                            </list>
                        </property>
                    </bean>
                </property>
            </bean>
        </property>
    </bean>
</beans>
```
dlc-ignite.xml:
```
<?xml version="1.0" encoding="UTF-8"?>

<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<!--
    Ignite configuration with all defaults and enabled p2p deployment and enabled events.
-->
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">
    <!-- Imports default Ignite configuration -->
    <import resource="dlc-default.xml"/>

    <bean parent="ignite.cfg"/>
</beans>
```
### 2.3. 日志组件文件引入Lucene Appender
> _[日志文件配置内容](https://github.com/xiapshen/dlc/wiki/log-config-file)_

> ps:_目前只支持log4j系列的日志记录_

### 2.4. 发布dlc服务（Spring）
dlc服务的发布分为两种，clusterSingleton和nodeSingleton，clusterSingleton（集群单例）指部署一个集群范围的单例服务，Ignite会保证集群内会一直有一个该服务的实例。当部署该服务的节点故障或者停止时，Ignite会自动在另一个节点上重新部署该服务；nodeSingleton（节点单例）指部署一个节点范围的单例服务，Ignite会保证每个节点都会有一个服务的实例在运行。当在集群组中启动了新的节点时，Ignite会自动地在每个新节点上部署一个新的服务实例。
#### 2.4.1. 在Spring的application.xml中配置
```
<bean id="dlcServiceExporter" class="com.happygo.dlc.ignite.DlcIgniteServicesExporter" init-method="export">
    <property name="mode" value="nodeSingleton" />
    <property name="service">
        <bean id="dlcService" class="com.happygo.dlc.ignite.service.DlcIgniteServiceImpl" />
    </property>
</bean>
```
#### 2.4.2. Spring java配置
```
@Bean
public DlcIgniteServicesExporter expoter() {
	DlcIgniteServicesExporter expoter = new DlcIgniteServicesExporter();
	expoter.setMode(DlcConstants.DEPLOY_NODE_SINGLETON);
	DlcIgniteService service = new DlcIgniteServiceImpl();
	expoter.setService(service);
	expoter.export();
	return expoter;
}
```
### 3.1. dlc-web配置
#### 3.1.1. 下载dlc-web-xxx.zip
下载dlc源码，通过maven打包，会在target下生成dlc-web-xxx.zip
#### 3.1.2. 配置
将zip文件解压到指定部署机器，将config/dlc-default.xml中<value>127.0.0.1:47500..47509</value>替换成真实部署机器IP地址，如下配置中加了注释“**用户可按需设置**”均可修改
```
<!-- Config ignite client cache -->
<property name="cacheConfiguration">
    <list>
        <!-- Partitioned cache example configuration (Atomic mode). -->
        <bean class="org.apache.ignite.configuration.CacheConfiguration">
            <property name="name" value="dlcLogCache"/>
            <property name="atomicityMode" value="ATOMIC"/>
            <property name="backups" value="1"/>
            <property name="evictionPolicy">
                <bean class="org.apache.ignite.cache.eviction.lru.LruEvictionPolicy">
                    <!-- 设置缓存中需要储存的元素个数，用户可按需设置 -->
                    <constructor-arg value="1000"/>
                </bean>
            </property>
            <property name="expiryPolicyFactory">
                <bean class="javax.cache.configuration.FactoryBuilder$SingletonFactory">
                    <constructor-arg>
                        <bean class="javax.cache.expiry.CreatedExpiryPolicy">
                            <constructor-arg>
                                <bean class="javax.cache.expiry.Duration">
                                    <constructor-arg index="0">
                                        <bean
                                         class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean">
                                            <property name="staticField"
                                                      value="java.util.concurrent.TimeUnit.MINUTES"/>
                                        </bean>
                                    </constructor-arg>
                                    <!-- 设置缓存失效时间， 单位：分钟，用户可按需设置 -->
                                    <constructor-arg index="1" value="2"/>
                                </bean>
                            </constructor-arg>
                        </bean>
                    </constructor-arg>
                </bean>
            </property>
        </bean>
    </list>
</property>
```
### 3.2. dlc-web一键式部署
进入解压后的文件中，找到bin目录，启动start.sh脚本，启动信息如下：
```
     _ _                       _     
  __| | | ___    __      _____| |__  
 / _` | |/ __|___\ \ /\ / / _ \ '_ \ 
| (_| | | (_|_____\ V  V /  __/ |_) |
 \__,_|_|\___|     \_/\_/ \___|_.__/                                    
:: dlc-web ::        (v1.1.1.RELEASE)
2017-08-06 16:11:21.516  INFO 7844 --- [           main] com.happygo.dlc.api.DlcWebApplication    : Starting DlcWebApplication on jjk with PID 7844 (F:\dlc-workspace\dlc\dlc-web\dlc-web-api\target\classes started by ACER in F:\dlc-workspace\dlc\dlc-web\dlc-web-api)
2017-08-06 16:11:21.572  INFO 7844 --- [           main] com.happygo.dlc.api.DlcWebApplication    : No active profile set, falling back to default profiles: default
2017-08-06 16:11:21.991  INFO 7844 --- [           main] ationConfigEmbeddedWebApplicationContext : Refreshing org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext@71f47: startup date [Sun Aug 06 16:11:21 CST 2017]; root of context hierarchy
2017-08-06 16:11:32.144  INFO 7844 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration' of type [org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration$$EnhancerBySpringCGLIB$$400f04b8] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2017-08-06 16:11:34.661  INFO 7844 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat initialized with port(s): 9090 (http)
2017-08-06 16:11:34.745  INFO 7844 --- [           main] o.apache.catalina.core.StandardService   : Starting service Tomcat
2017-08-06 16:11:34.748  INFO 7844 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/8.5.11
2017-08-06 16:11:35.418  INFO 7844 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2017-08-06 16:11:35.419  INFO 7844 --- [ost-startStop-1] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 13445 ms
2017-08-06 16:11:36.311  INFO 7844 --- [ost-startStop-1] o.s.b.w.servlet.ServletRegistrationBean  : Mapping servlet: 'statViewServlet' to [/druid/*]
2017-08-06 16:11:36.328  INFO 7844 --- [ost-startStop-1] o.s.b.w.servlet.ServletRegistrationBean  : Mapping servlet: 'dispatcherServlet' to [/]
```

## 4. dlc查询语法
> _[查询语法](https://github.com/xiapshen/dlc/wiki/dlc-query-grammar)_

## 5. 日志源设置
### 5.1. 新增日志源
初次使用dlc-web客户端需新增默认日志源，否则系统会报未设置日志源
![新增日志源](https://github.com/xiapshen/dlc/blob/master/docs/images/add_logsource.png)

### 5.2. 日志源列表
日志源列表可以修改默认日志源以及删除无用日志源
![日志源列表](https://github.com/xiapshen/dlc/blob/master/docs/images/logsource_list.png)