#!/usr/bin/env bash
#
# 도커 데몬에게 레지스트리 주소를 설정해준다.
# @author : Sang Wook, Song
#

DOCKER_REGISTRY=$1

#docker configuration
echo DOCKER_OPTS=\"\$DOCKER_OPTS --insecure-registry $DOCKER_REGISTRY\" | sudo tee -a /etc/default/docker

sudo service docker restart
