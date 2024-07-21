# RequestHandler

This project was created for a purpose to
redirect requests between several handlers,
that will behavior in different ways, manipulates data and send response
to client in a standard format.

## Tools

Before we start running and interacting with project, you need to have
these tools (instruments, sdk, etc.) installed in your environment.

- Java SDK 8 (OpenJDK will be the best choice)
- Apache maven (or use Makefile in source directory)
- Apache Tomcat (9.x.x version)

## Configuration

First of all, there are properties files in resources directory.
These resources contain all configuration data, that is not necessary for filling.

The first one is `logging.properties`.
It contains information about logging. Bingo!
There are few parameters inside it:

- `logPath` - path for file, that will be used as logging file.
  If you don't want to use logging into file - you can leave this field empty.
  
Second configuration file is `cms.properties`
This file is about cms subsystem: file path for file-based cms, upload directory and used datasource.
Datasource may be: DATABASE, FILE_SYSTEM.
Database-based datasource use parameters from web.xml, that starts with QDBPOOL*.
File-based work with paths in same properties file.

Other parameters in web.xml are dbUrl and debug. The first one is for SQL subsystem. 
Debug means will be the application send cookie in httpOnly or no. False - for httpOnly cookies.

## Build with Maven

The preparation step includes downloading two jars from the url: 
http://ftp.eustrosoft.org/pub/eustrosoft.org/pkg/ConcepTIS/
- qDBPool
- qSessionCookie

After this, you need to install them in your .m2 directory for maven to work with them.
The script is in project ROOT ./scripts/installMvn. Complete the brackets and run the program. 
The versions and naming must be like in mvn file of project.

To build project, you need to do next maven goal:
`cd request-handler-servce && mvn package`.
Then, you will have `.war` package in `target` directory.

**If you want a jar file - just recompile in JAR mode in mvn. Parameter name is `packaging`**

## Build with Makefile

If you want to build this project with Makefile, follow the next steps:

1. Place all libraries in `README` in `./request-handler-service/lib/README` to the same directory \
  1.1. Commons-io: https://repo1.maven.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar \
  1.2. PostgreSQL: https://repo1.maven.org/maven2/org/postgresql/postgresql/42.5.1/postgresql-42.5.1.jar \
  1.3. Javax Servlet Api: https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.0/javax.servlet-api-4.0.0.jar \
  1.4. DBPool & SessionCookie look at: http://ftp.eustrosoft.org/pub/eustrosoft.org/pkg/ConcepTIS/
2. In `request-handler-service` directory, you might run `make` to see at prompt
3. `make build` to build the project, `.jar` file, classes, libraries & WEB-INF will be created in `work/` directory
4. Use `make clean` to clean all work and stage files

## Deploy

To deploy an application, you just need to put this `.war`/`.jar with properties` file inside
`%CATALINA_HOME%/webapps` directory.

Then, startup tomcat.