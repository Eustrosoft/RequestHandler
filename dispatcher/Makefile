JAR_LOCATION = "target/eustrosofthandler.jar"

usage:
	@echo "Usage: make (all|clean|compile|package)"

clean:
	mvn clean

compile: clean
	mvn compile

package: clean
	mvn package
	@echo "Jar located in ${JAR_LOCATION}"

all: package
	@echo "Processed successfully"
