# RequestHandler

This project was created for a purpose to
redirect requests between several handlers,
that will behavior in different ways, manipulates data and send response
to client in a standard format.

## Tools

Before we start running and interacting with project, you need to have
these tools (instruments, sdk, etc.) installed in your environment.

- Java SDK 8 (OpenJDK will be the best choice)
- Apache maven
- Apache Tomcat (9.x.x version)

## Configuration

First of all, there are properties files in resources directory.
These resources contains all configuration data, that is not necessary for filling.

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

## Build

The preparation step includes downloading two jars from the url: 
http://ftp.eustrosoft.org/pub/eustrosoft.org/pkg/ConcepTIS/
- qDBPool
- qSessionCookie

After this, you need to install them in your .m2 directory for maven to work with them.
The script is in project ROOT directory/scripts/installMvn. Complete the brackets and run the program. 
The versions and naming must be like in mvn file of project.

To build project, you need to do next maven goal:
`mvn package`.
Then, you will have `.war` package in `target` directory.

**If you want a jar file - just recompile in JAR mode in mvn. Parameter name is `packaging`**

## Deploy

To deploy an application, you just need to put this `.war`/`.jar with properties` file inside
`%CATALINA_HOME%/webapps` directory.

Then, startup tomcat.