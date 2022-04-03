#!/usr/bin/env bash

mvn mvn package
java -jar distribution/target/quarkus-app/quarkus-run.jar
