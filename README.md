# RequestHandler

This project was created for a purpose to
redirect requests between several handlers,
that will behavior in different ways, manipulates data and send response
to client in standard format.

## Tools

Before we start running and interacting with project, you need to have
these tools (instruments, sdk, etc.) installed in your environment.

- Java SDK 11 (OpenJDK will be the best choice)
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

## Build

To build project, you need to do next maven goal:
`mvn package`.
Then, you will have `.war` package in `target` directory.

## Deploy

To deploy an application, you just need to put this `.war` file inside
`%CATALINA_HOME%/webapps` directory.

Then, startup tomcat and write name of `.war` file in query line of your browser.