# **入门**

> ## dlc结构介绍

dlc分布式日志搜集系统结构分为两部分:dlc-web（客户端）和dlc-core（日志搜集组件，与应用集成），dlc-web的作用是从日志搜集组件获取到相关日志并在前端展示，dlc-web可以配置从哪个系统获取设置等等，dcl-web采用应用闭环部署，一键部署，部署简单；dlc-core日志搜集核心组件，与应用系统集成，随着应用系统的启动，会在相同应用系统自动建立集群，写日志时，建立lucene索引，便于后面日志搜集，提供配置哪些字段需要建立索引，配置灵活，方便。

## 

> ## dlc依赖的其他构件

dlc-core主要依赖的其他构件有apache ignite、log4j2以及lucene，dlc-core主要依赖的其他构件有Spring boot、alibaba的durid、mybatis等

