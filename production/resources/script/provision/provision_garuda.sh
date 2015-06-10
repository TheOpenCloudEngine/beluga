#!/usr/bin/env bash
#
# Garuda의 기본패키지를 설치한다.
# @author : Sang Wook, Song
#

DOCKER_REGISTRY=$1

sudo apt-get -y update

sudo wget -qO- https://get.docker.com/ | sh

#docker configuration
echo DOCKER_OPTS=\"\$DOCKER_OPTS --insecure-registry $DOCKER_REGISTRY\" | sudo tee -a /etc/default/docker

sudo service docker restart

sudo usermod -aG docker ubuntu
