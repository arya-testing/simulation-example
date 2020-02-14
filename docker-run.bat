@echo off

docker build --tag arya-test .
docker rm -f arya-test
docker run --name arya-test arya-test