#!/bin/bash  

LIBS=libs/raspberrygpio.jar:/home/pi/java:libs/commonsio24.jar:libs/mysql-connector-java-5.1.21-bin.jar:libs/framboos-0.0.1.jar:libs/pi4j-core-0.0.2-SNAPSHOT.jar
echo "Compiling"
javac -classpath $LIBS $1.java
echo "Done, running"
sudo java -cp $LIBS $1
