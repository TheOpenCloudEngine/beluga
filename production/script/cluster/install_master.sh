#!/usr/bin/env bash
#
# 마스터노드의 패키지를 설치한다.
# @author : Sang Wook, Song
#

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

# Install Java 8 from Oracle's PPA
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get -y update
sudo apt-get install -y oracle-java8-installer oracle-java8-set-default

sudo apt-get -y install mesos

sudo apt-get -y install marathon

sudo mv /etc/init/mesos-slave.conf /etc/init/mesos-slave.conf.bak
