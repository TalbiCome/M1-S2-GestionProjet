#!/bin/sh

dockerd &

docker build --pull --rm -f "./Dockerfile" -t gestiondeprojet-loginapp:latest "."

docker run gestiondeprojet-loginapp:latest