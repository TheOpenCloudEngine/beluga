#!/usr/bin/env bash
#
# 도커데몬을 설치한다.
# @author : Sang Wook, Song
#

sudo apt-get -y update

sudo wget -qO- https://get.docker.com/ | sh

sudo usermod -aG docker ubuntu
