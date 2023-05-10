cd request-handler-service/
mvn clean package
scp -R target/classes/com/ yadzuka@fudo.eustrosoft.org:/home/yadzuka/workspace/
ssh yadzuka@fudo.eustrosoft.org 'scp -r /home/yadzuka/workspace/eustrosoft /usr/local/apache-tomcat-9.0/webapps/eustrosofthandler_war/WEB-INF/classes/'
ssh yadzuka@fudo.eustrosoft.org 'rm -rf /usr/local/apache-tomcat-9.0/webapps/eustrosofthandler_war/WEB-INF/classes/com/'
ssh yadzuka@fudo.eustrosoft.org 'mkdir /usr/local/apache-tomcat-9.0/webapps/eustrosofthandler_war/WEB-INF/classes/com'
ssh yadzuka@fudo.eustrosoft.org 'mv /usr/local/apache-tomcat-9.0/webapps/eustrosofthandler_war/WEB-INF/classes/eustrosoft /usr/local/apache-tomcat-9.0/webapps/eustrosofthandler_war/WEB-INF/classes/com'
