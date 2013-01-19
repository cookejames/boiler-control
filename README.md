boiler-control
==============
Java code to control relays via a raspberry pi.
Features GPIO code, output to a 16x2 LCD and mysql access.
This is tested with the Oracle JDK and as such requires the Debian softfloat install. If you use raspbian and openjdk I can't verify any problems you may have although it could run fine.

changelog
==============
v1.2
-added choice of boost time to frontend
v1.1
-changed communication between php and java to mostly use sockets rather than the database
-boosting boiler now instantly shows it is on/off
-removed many database calls from java and php side - should reduce CPU load
-now shows heating/water state and boost state separately
-boost time remaining now shown
-moved pins configuration to database, create entry in configuration table with key pinsHigh and the value
	true (high) or false (low) when pressed
v1.0
-release

TODO
-make sure all configuration options are in the database

Instructions
==============

Firstly install java in accordance with this guide - you may be able to use openjdk but it's slower and users have had problems getting it running

Download it from http://www.oracle.com/technetwork/java/javase/downloads/jdk7u9-downloads-1859576.html - the file you want is the Linux ARM one - and transfer the file using a program like WinSCP.

Now execute the following commands as a normal user (such as pi)
<pre><code>sudo mkdir /opt/java
tar -xzvf jdk-7u6-linux-arm-sfp.tar.gz 
sudo mv jdk1.7.0_06/ /opt/java/
sudo update-alternatives --install "/usr/bin/java" "java" "/opt/java/jdk1.7.0_06/bin/java" 1
sudo update-alternatives --install "/usr/bin/javac" "javac" "/opt/java/jdk1.7.0_06/bin/javac" 1
sudo update-alternatives --set java /opt/java/jdk1.7.0_06/bin/java
sudo update-alternatives --set javac /opt/java/jdk1.7.0_06/bin/javac
sudo apt-get update
sudo apt-get install screen
wget https://github.com/james-jaynne/boiler-control/archive/master.zip
unzip master.zip
sudo mkdir /opt/boilercontrol
sudo mv boiler-control-master/libs/ /opt/boilercontrol/
sudo mv boiler-control-master/scripts/boilercontrol/java/* /opt/boilercontrol/
sudo mv boiler-control-master/scripts/boilercontrol/* /etc/init.d/
sudo chmod +x /opt/boilercontrol/*.sh
sudo chmod +x /etc/init.d/boilercontrol
</code></pre>

Now you will want to edit a few files that define what GPIO pins you are using and what your database password is.

Starting from the /opt/boilercontrol directory you need to edit uk/co/uk/jaynne/ControlBroker.java There are 6 lines starting from line 13 that define what pins you are using for your relays. You also need to say whether your relay is on when the pin is high (true) or low (false). You can see the header and pin names at this link [url]https://projects.drogon.net/raspberry-pi/wiringpi/pins/[/url].

You also need to edit uk/co/uk/jaynne/datasource/DbConfig.java and edit all 3 lines. The last bit of the URL line /pi defines the database name.

You can now do the first run with 
<pre><code>cd /opt/boilercontrol
sudo ./compileandrun.sh
</code></pre>

For following runs where the source code hasn't changed you can use the following

<pre><code>cd /opt/boilercontrol
sudo ./run.sh
</code></pre>

