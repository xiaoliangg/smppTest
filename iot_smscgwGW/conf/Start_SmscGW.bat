title SMSCGW Server

set main_home=D:\\SmscGW
set java_home=D:\\java

set bin=%main_home%/bin
set lib=%main_home%/lib
set smpp=%lib%/smpp
set http=%lib%/http
set jms=%lib%/jms
set log=%lib%/log4j
set db=%lib%/db
set xml=%lib%/xml
set wd=%lib%/wd
set json=%lib%/json
set alarm=%lib%/alarm


cd..
echo %cd%

set main_classpath=%lib%/smscgw-1.0.jar;
set db_classpath=%db%/satota-dao-1.0.jar;
set xml_classpath=%xml%/dom4j-1.6.1.jar;
set wd_classpath=%wd%/DESTool.jar;%wd%/wdcoder-jdk15-1.3.1.jar
set smpp_classpath=%smpp%/smpp.jar;%smpp%/xbean.jar;%smpp%/SMPPServerConfigXML.jar;%smpp%/jsr173_1.0_api.jar
set http_classpath=%http%/httpcore-4.0.jar;%http%/httpcore-nio-4.0.jar;%http%/httpclient-4.0-beta2.jar;%http%/httpmime-4.0-beta2.jar;%http%/commons-codec-1.3.jar;%http%/commons-httpclient-3.1.jar
set jms_classpath=%jms%/activemq-core-5.2.0.jar;%jms%/geronimo-j2ee-management_1.0_spec-1.0.jar;%jms%/geronimo-jms_1.1_spec-1.1.1.jar;%jms%/mqBean.jar
set log_classpath=%log%/log4j-1.2.14.jar;%log%/commons-logging-1.1.jar
set json_classpath=%json%/commons-beanutils-1.8.3.jar;%json%/commons-collections-3.2.jar;%json%/commons-lang.jar;%json%/ezmorph-1.0.6.jar;%json%/json-lib-2.4-jdk15.jar
set alarm_classpath=%alarm%/alarm.jar

set CLASSPATH=%main_classpath%;%db_classpath%;%xml_classpath%;%wd_classpath%;%smpp_classpath%;%http_classpath%;%jms_classpath%;%log_classpath%;%json_classpath%;%alarm_classpath%;

"%JAVA_HOME%\bin\java" com.roamingServer.smscgw.server.SmscGW