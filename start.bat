@echo off
java -jar .\build\libs\arya-simulation-0.0.1.jar --simulation login --url https://www.google.com --username johndoe01 --password password --username abc --auto-screenshots --auto-wait 2
REM gradle run --args="--simulation login --url https://www.google.com --username johndoe01 --password password --username abc --auto-screenshots --auto-wait 2"