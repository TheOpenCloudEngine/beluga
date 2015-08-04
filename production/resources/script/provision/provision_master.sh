#!/usr/bin/env bash
#
# 마스터노드의 패키지를 설치한다.
# @author : Soo Hwan, Min
# @author : Sang Wook, Song
#
# @param 1 : zookeeper address
# @param 2 : mesos cluster name
# @param 3 : mesos master public ip
# @param 4 : mesos master private ip
# @param 5 : quorum
# @param 6 : zookeeper address 1
# @param 7 : zookeeper address 2
# @param 8 : zookeeper address 3
#

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

sudo apt-get -y update

sudo apt-get -y install mesos

sudo apt-get -y install marathon

sudo mv /etc/init/mesos-slave.conf /etc/init/mesos-slave.conf.bak

#zk
echo $1 | sudo tee /etc/mesos/zk

#cluster
echo $2 | sudo tee /etc/mesos-master/cluster

#public IP
echo $3 | sudo tee /etc/mesos-master/hostname

#private IP
echo $4 | sudo tee /etc/mesos-master/ip

#quorum
echo $5 | sudo tee /etc/mesos-master/quorum

#zookeeper type
echo $6 | sudo tee /etc/zookeeper/conf/myid

#zookeeper servers
echo $7 | sudo tee -a /etc/zookeeper/conf/zoo.cfg
echo $8 | sudo tee -a /etc/zookeeper/conf/zoo.cfg
echo $9 | sudo tee -a /etc/zookeeper/conf/zoo.cfg

sudo reboot




