@echo off
title LibGdx HTML Builder

ECHO LibGDX HTML BUILDER
ECHO  ___________________________________________
ECHO needs phyton v3 or further
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO Starting gradle.build task:
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO  ___________________________________________
START gradlew html:dist
ECHO ______doing gradle stuff in other screen
ECHO ______KEEP WAITING until ends
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO ___________________________________________
ECHO ___when it ends, close the other window 
ECHO ______and press here any key
ECHO ___________________________________________
ECHO ___________________________________________
ECHO ___________________________________________
ECHO ___________________________________________
PAUSE
CD html/build/dist
START python3 -m http.server 9000
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO Server activated, opening localhost 9000
START http://localhost:9000
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO  ___________________________________________
ECHO             CLOSE ALL WINDOWS TO END SERVER
ECHO  _______________________________________
ECHO  _______________________________________
ECHO  _________________________________
ECHO  ____________________________
ECHO  _______________________
ECHO  __________________
ECHO  _____________
ECHO  __________
ECHO  _______
ECHO  ____
PAUSE
exit