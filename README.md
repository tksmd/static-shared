# What's this

This is a Java Servlet implementation of [Plack::Middleware::StaticShared](https://metacpan.org/module/Plack::Middleware::StaticShared), except the Google Closure compiling feature on javascript files concatination.
In addition, Plack::Middleware::StaticShared uses memcached for results caching on the other hand this uses a simple file as cache. So it is strongly recommended to be used with reverse proxy which can cache response of this servelet.

# How To Use

## Add dependency to staticshared for your project

## Change web.xml and add servlet configuration.

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
		<init-param>
			<param-name>nocache</param-name>
			<param-value>true</param-value>
		</init-param>
	</servlet>

	<servlet-mapping>
		<servlet-name>staticshared</servlet-name>
		<url-pattern>/.shared.js/*</url-pattern>
		<url-pattern>/.shared.css/*</url-pattern>
	</servlet-mapping>
```

## Load scripts/css through SharedServlet on your HTML

If you want to load these 3 javascript files ( relative to context path ) with version "v1",

* js/jquery.js
* js/underscore.js
* js/bootstrap.js

then you add a script tag into your jsp file just like this.

```
<script src="${pageContext.request.contextPath}/.shared.js/v1:js/jquery.js,js/underscore.js,js/bootstrap.js"></script>
```

# Sample

You can easily know how this works by following steps.

```
git clone https://github.com/tksmd/static-shared
cd static-shared
./gradlew -info tomcatRun
```

and then access to the URL ( http://localhost:8080/static-shared ) on your browser.

# See Also

* [Plack::Middleware::StaticShared source](https://github.com/cho45/Plack-Middleware-StaticShared)
