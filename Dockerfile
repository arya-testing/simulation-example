FROM arya
FROM arya:jdk8
FROM arya:node12
#FROM openjdk:8
#COPY build/libs/arya-simulation-0.0.1.jar app.jar
#ENTRYPOINT java -jar app.jar --simulation search --url https://www.google.com --query "funny cats" --auto-screenshots --auto-wait 2