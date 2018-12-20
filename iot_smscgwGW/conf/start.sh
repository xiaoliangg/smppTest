#!/bin/sh
#JDK所在路径
JAVA_HOME=/opt/jre
#应用主目录
MAIN_HOME=/opt/webserver/IOT_SmscGW

#执行程序启动所使用的系统用户，考虑到安全，推荐不使用root帐号
RUNNING_USER=root

bin=$MAIN_HOME/bin
#需要引用的第三方jar目录
#拼凑完整的classpath参数，包括指定lib目录下所有的jar
CLASSPATH=$MAIN_HOME/
for i in "$MAIN_HOME"/lib/*/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done
for i in "$MAIN_HOME"/lib/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done
echo $CLASSPATH
#需要启动的Java主程序（main方法类）
#APP_MAINCLASS=com.roamingServer.smscgw.server.SmscGW
APP_MAINCLASS=com.roamingServer.smscgw.smpp.client.SmppStarter
#java虚拟机启动参数
JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m"

cd /opt/webserver/IOT_SmscGW
$JAVA_HOME/bin/java $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS 
