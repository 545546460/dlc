## 发布dlc服务（Spring）

---

dlc服务的发布分为两种，clusterSingleton和nodeSingleton，clusterSingleton（集群单例）指部署一个集群范围的单例服务，Ignite会保证集群内会一直有一个该服务的实例。当部署该服务的节点故障或者停止时，Ignite会自动在另一个节点上重新部署该服务；nodeSingleton（节点单例）指部署一个节点范围的单例服务，Ignite会保证每个节点都会有一个服务的实例在运行。当在集群组中启动了新的节点时，Ignite会自动地在每个新节点上部署一个新的服务实例。

### 在Spring的application.xml中配置

```
<bean id="dlcServiceExporter" class="com.happygo.dlc.ignite.DlcIgniteServicesExporter" init-method="export">
    <property name="mode" value="nodeSingleton" />
    <property name="service">
        <bean id="dlcService" class="com.happygo.dlc.ignite.service.DlcIgniteServiceImpl" />
    </property>
</bean>
```

### Spring java配置

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



