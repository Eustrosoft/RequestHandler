cd request-handler-service/
mvn clean package
scp -r target/classes/com/ yadzuka@fudo.eustrosoft.org:/home/yadzuka/workspace/
