#!/usr/bin/env bash

mvn package
java -jar distribution/target/quarkus-app/quarkus-run.jar
