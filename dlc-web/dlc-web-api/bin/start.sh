#!/bin/sh
echo -------------------------------------------
echo start dlc-web server
echo -------------------------------------------

# 设置项目代码路径
export CODE_HOME=".."

# 设置依赖路径
export CLASSPATH="$CODE_HOME/WEB-INF/classes:$CODE_HOME/lib/*"

# java可执行文件位置
export _EXECJAVA="$JAVA_HOME/bin/java"

# JVM启动参数
export JAVA_OPTS="-server -Xms128m -Xmx256m -Xss256k -XX:MaxDirectMemorySize=128m"

# 启动类
export MAIN_CLASS=com.happygo.dlc.api.DlcWebApplication

$_EXECJAVA $JAVA_OPTS -classpath $CLASSPATH $MAIN_CLASS &