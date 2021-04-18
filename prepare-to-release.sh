#!/bin/bash
set -e
./mvnw clean package
mv target/demo-api-runner.jar target/demo-api.jar
java -jar target/demo-api.jar