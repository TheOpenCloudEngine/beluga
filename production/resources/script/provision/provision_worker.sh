#!/usr/bin/env bash
#
# 워커노드의 패키지를 설치한다.
# @author : Soo Hwan, Min
# @author : Sang Wook, Song
#

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

sudo apt-get -y update

sudo apt-get -y install mesos

sudo wget -qO- https://get.docker.com/ | sh

sudo mv /etc/init/mesos-master.conf /etc/init/mesos-master.conf.bak

#zk
echo $1 | sudo tee /etc/mesos/zk

#public IP
echo $2 | sudo tee /etc/mesos-slave/hostname

#private IP
echo $3 | sudo tee /etc/mesos-slave/ip

#containerizers
echo $4 | sudo tee /etc/mesos-slave/containerizers

#docker configuration
echo DOCKER_OPTS=\"\$DOCKER_OPTS --insecure-registry $5\" | sudo tee -a /etc/default/docker

sudo service docker restart

sudo reboot



