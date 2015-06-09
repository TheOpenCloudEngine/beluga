#!/usr/bin/env bash
sudo apt-get -y install docker.io

sudo docker pull registry:latest

sudo docker run --name personal-registry -d -p 5000:5000 registry