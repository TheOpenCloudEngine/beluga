#!/usr/bin/env bash
#
# 워커노드의 패키지를 설치한다.
# @author : Sang Wook, Song


sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

sudo apt-get -y update

sudo apt-get -y install mesos

sudo wget -qO- https://get.docker.com/ | sh

sudo usermod -aG docker ubuntu

sudo mv /etc/init/mesos-master.conf /etc/init/mesos-master.conf.bak


