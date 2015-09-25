#!/usr/bin/env bash

user_home="$(eval echo ~$USER)"

docker run --rm \
           -t \
           -v $user_home/.gradle:/root/.gradle \
           -v $(pwd):/project \
           -w /project \
           java:7 \
           ./gradlew clean build
