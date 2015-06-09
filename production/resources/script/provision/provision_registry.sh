#!/bin/bash
#
# Docker registry의 패키지를 설치한다.
# @author : Sang Wook, Song
#

# 패키지 업데이트
sudo apt-get update -y

# Docker 설치
sudo apt-get install docker.io -y

# /etc/default/docker.io 에 "$DOCKER_OPTS --insecure-registry <IP>:5000" 추가.
echo 'DOCKER_OPTS="--insecure-registry 52.69.75.168:5000"' | sudo tee -a /etc/default/docker.io

sudo docker run -p 5000:5000 -v /home/ubuntu/registry-conf:/registry-conf -e DOCKER_REGISTRY_CONFIG=/registry-conf/config.yml registry