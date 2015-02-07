WebSphere and Spring
====================


Spring offers a rich platform on top of which to build lightweight, powerful web applications and services, handle batch processing,
 integration and big-data chores, and connect to the wider open-web. It is very portable and can run on and exploit a wide assortment of platforms, inclduing
 IBM WebSphere. 

Notes:
------

Spring provides the `JtaTransactionManager` hierarchy. It handles TX coordination with JTA implementations. The JTA spec provides the `UserTransaction` and optionally the `TransactionManager` interfaces. WebSphere offers oneof the most advanced,powerful TX managers in the business, and it would be a disservice if Spring didn't expose as much of it as possible. The [`WebSphereUowTransactionManager` implementation](https://github.com/spring-projects/spring-framework/blob/master/spring-tx/src/main/java/org/springframework/transaction/jta/WebSphereUowTransactionManager.java)

Spring provides a [`WebSphereMBeanServerFactoryBean`](https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/jmx/support/WebSphereMBeanServerFactoryBean.java) that obtains a `javax.management.MBeanServer` reference through WebSphere's proprietary `AdminServiceFactory`. This is very convenient for - There are some issues to know about when working with WSL. One is that [the error filter in Boot responds that it can't write to the output because the buffer's been closed](https://github.com/spring-projects/spring-boot/issues/1575). [There's interesting advice on how to fix it here](http://www-01.ibm.com/support/knowledgecenter/SSZH4A_6.2.0/com.ibm.worklight.deploy.doc/admin/t_configuring_websphere_application_server_manually.html). The error looks like this:

```sh
2015-02-06 15:47:35.358 ERROR 13897 --- [ecutor-thread-1] o.s.boot.context.web.ErrorPageFilter     : Cannot forward to error page for request [/] as the response has already been committed. As a result, the response may have the wrong status code. If your application is running on WebSphere Application Server you may be able to resolve this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false
```

Create a custom server to isolate your application: `> bin ./server create boot --template="defaultServer"`. This will create a directory structure, like this: `$WEBSHERE_ROOT/wlp/usr/servers/boot`. There, you can find a configuration file, [`server.xml`](http://www-01.ibm.com/support/knowledgecenter/SSEQTP_8.5.5/com.ibm.websphere.wlp.doc/autodita/rwlp_metatype_4ic.html?cp=SSEQTP_8.5.5%2F1-0-2-1-0).

My `server.xml` looks like this:

```xml

<?xml version="1.0" encoding="UTF-8"?>
<server description="new server">

	<!-- Enable features -->
	<featureManager>
		<feature>jsp-2.2</feature>
		<feature>localConnector-1.0</feature>
		<feature>jndi-1.0</feature>
		<feature>localConnector-1.0</feature>
		<feature>servlet-3.0</feature>
		<feature>jdbc-4.0</feature>
	</featureManager>

	<!-- To access this server from a remote client add a host attribute to the following element, e.g. host="*" -->
	<webContainer invokeFlushAfterService="false" />
	<httpEndpoint id="defaultHttpEndpoint" httpPort="9080" httpsPort="9443" />
	<applicationMonitor updateTrigger="mbean" />
	<application id="demo_war" location="/Users/jlong/Desktop/ws/target/demo-0.0.1-SNAPSHOT.war" name="demo_war" type="war" />
	<!-- setting up DS as per http://jaceklaskowski.pl/wiki/Connecting_Java_EE_application_to_MySQL_in_WebSphere_Application_Server_V8.5_Liberty_Profile -->
	<dataSource jndiName="jdbc/mysql">
		<jdbcDriver id="mysqlDriver" libraryRef="mysql-connector" />
		<properties URL="jdbc:mysql://ahost/adb" password="apw" user="auser" />
	</dataSource>
	<library description="MySQL JDBC Driver" id="mysql-connector" name="MySQL Connector">
		<fileset dir="/Users/jlong/.m2/repository/mysql/mysql-connector-java/5.1.34/" id="mysql-connector-jar" includes="mysql-connector-java-*.jar" />
	</library>

</server>


```

[Configuring and consuming a JDBC `DataSource`](http://jaceklaskowski.pl/wiki/Connecting_Java_EE_application_to_MySQL_in_WebSphere_Application_Server_V8.5_Liberty_Profile) from a Spring Boot application is easy with Boot's JNDI-based access.

References
----------

-	[Deploying Spring Boot applications in IBM WebSphere Liberty Application Server](http://naruraghavan.github.io/deploying-spring-boot-applications-in-ibm-websphere-application-server/) -
