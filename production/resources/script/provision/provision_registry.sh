#!/usr/bin/env bash
#
# Docker registry의 패키지를 설치한다.
# @author : Soo Hwan, Min
# @author : Sang Wook, Song
#

sudo apt-get -y install docker.io

sudo docker pull registry:latest

sudo docker run --name garuda-registry -d -p 5000:5000 registry