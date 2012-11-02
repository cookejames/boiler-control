boiler-control
==============
Java code to control relays via a raspberry pi.
Features GPIO code, output to a 16x2 LCD and mysql access.


Instructions
==============

If you download the latest from https://github.com/james-jaynne/boiler-control. You might want to extract it on your own computer if you haven't got a GUI on your Pi as you need to edit a couple of files to setup the GPIO pins you are using.

From the scripts directory that you've downloaded copy the boilercontrol folder to /opt and the the file init.d/boilercontrol to /etc/init.d/ You need to make all these files executable by running sudo chmod +x filename.

From the src directory you need to edit uk/co/uk/jaynne/ControlBroker.java There are 6 lines starting from line 13 that define what pins you are using for your relays. You also need to say whether your relay is on when the pin is high (true) or low (false). You can see the header and pin names at this link [url]https://projects.drogon.net/raspberry-pi/wiringpi/pins/[/url].

You also need to edit uk/co/uk/jaynne/datasource/DbConfig.java and edit all 3 lines. The last bit of the URL line /pi defines the database name.

Copy both the src and libs directory to /opt/boilercontrol/java since you've edited the source files you need to compile the java the first time you run it - you can do that executing the following commands
[code]
cd /opt/boilercontrol/java
sudo ./compileandrun.sh
[/code]

When you want to run the program normally you can run the following command which starts the program in the background so that when you logout it keeps running.

[code]
sudo /etc/init.d/boilercontrol start
[/code]
