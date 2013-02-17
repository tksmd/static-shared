# What's this

# How To Use


## Add Servlet configuration to your web.xml

```
	<servlet>
		<servlet-name>staticshared</servlet-name>
		<servlet-class>staticshared.SharedServlet</servlet-class>
		<init-param>
			<param-name>base</param-name>
			<param-value></param-value>
		</init-param>
		<init-param>
			<param-name>js</param-name>
			<param-value>text/javascript; charset=utf8</param-value>
		</init-param>
		<init-param>
			<param-name>css</param-name>
			<param-value>text/css; charset=utf8</param-value>
		</init-param>
	</servlet>

	<servlet-mapping>
		<servlet-name>staticshared</servlet-name>
		<url-pattern>/.shared.js/*</url-pattern>
		<url-pattern>/.shared.css/*</url-pattern>
	</servlet-mapping>
```

# Sample

git clone https://github.com/tksmd/static-shared
cd static-shared
./gradlew -info tomcatRun

http://localhost:8080/static-shared

# See Also

* [Plack::Middleware::StaticShared](https://github.com/cho45/Plack-Middleware-StaticShared)
