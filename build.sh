#!/usr/bin/env bash

#builds and starts the application
mvn package
java -jar distribution/target/quarkus-app/quarkus-run.jar
