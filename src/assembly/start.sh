#!/bin/bash
cd /home/pi/deploy/ProcessMe
DISPLAY=:0 XAUTHORITY=/home/pi/.Xauthority /home/pi/.sdkman/candidates/java/current/bin/java -XX:+UseZGC -Xmx1G  -jar ProcessMe.jar
exit 0
