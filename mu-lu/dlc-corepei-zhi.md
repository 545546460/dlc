## dlc-core配置

---

### 引入dlc-core构件

dlc-core与应用集成，需在应用系统的pom文件中引入dlc-core构件：

```
<dependency>
	<groupId>com.happygo.dlc</groupId>
	<artifactId>dlc-core</artifactId>
	<version>1.1.1</version>
</dependency>
```

### 配置dlc-ignite文件

在应用系统中src/main/resources下建立一个目录，命名为config，在config下新建一个dlc-default.xml以及dlc-ignite.xml文件，下面两个xml文件的配置，拿来即用，只需将&lt;value&gt;127.0.0.1:47500..47509&lt;/value&gt;替换成真实IP，模板如下：

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

    ### 2.3. 发布dlc服务（Spring）
    dlc服务的发布分为两种，clusterSingleton和nodeSingleton，clusterSingleton（集群单例）指部署一个集群范围的单例服务，Ignite会保证集群内会一直有一个该服务的实例。当部署该服务的节点故障或者停止时，Ignite会自动在另一个节点上重新部署该服务；nodeSingleton（节点单例）指部署一个节点范围的单例服务，Ignite会保证每个节点都会有一个服务的实例在运行。当在集群组中启动了新的节点时，Ignite会自动地在每个新节点上部署一个新的服务实例。
    #### 2.3.1. 在Spring的application.xml中配置
    ```
    <bean id="dlcServiceExporter" class="com.happygo.dlc.ignite.DlcIgniteServicesExporter" init-method="export">
        <property name="mode" value="nodeSingleton" />
        <property name="service">
            <bean id="dlcService" class="com.happygo.dlc.ignite.service.DlcIgniteServiceImpl" />
        </property>
    </bean>



