all:
	javac view/*.java
	javac control/*.java
	javac model/*java
	java  control/Main

run:
	java control/Main

clean:
	echo "Removing .class files"
	rm control/*.class
	rm model/*.class
	rm view/*.class	
