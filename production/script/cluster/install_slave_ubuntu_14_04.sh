#!/usr/bin/env bash
#
# 워커노드의 패키지를 설치한다.
# @author : Sang Wook, Song


# Add the Mesosphere repository
sudo apt-get update -y
sudo DEBIAN_FRONTEND=noninteractive apt-get install -y \
  python-software-properties software-properties-common
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF
DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')
CODENAME=$(lsb_release -cs)
echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" |
  \sudo tee /etc/apt/sources.list.d/mesosphere.list

# Install Java 8 from Oracle's PPA
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update -y
sudo apt-get install -y oracle-java8-installer oracle-java8-set-default

sudo apt-get -y install mesos

sudo wget -qO- https://get.docker.com/ | sh

sudo usermod -aG docker ubuntu

# cadvisor를 시작한다.
sudo docker run --volume=/:/rootfs:ro --volume=/var/run:/var/run:rw --volume=/sys:/sys:ro --volume=/var/lib/docker/:/var/lib/docker:ro --publish=8080:8080 --detach=true --name=cadvisor   google/cadvisor:latest

# cadvisor 자동시작을 등록한다.
cat << CAdvisorConfig | sudo tee /etc/init/cadvisor.conf
start on runlevel [2345]
respawn
kill timeout 20
exec docker start cadvisor
CAdvisorConfig

sudo mv /etc/init/mesos-master.conf /etc/init/mesos-master.conf.bak
sudo mv /etc/init/zookeeper.conf /etc/init/zookeeper.conf.bak
