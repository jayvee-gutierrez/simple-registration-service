#!/bin/bash

port=${1:-8887}

mvn clean package
docker build --tag=registration-service:latest .
docker run -p"$port":8080 registration-service:latest
