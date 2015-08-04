#!/usr/bin/env bash
#
# 마스터노드의 패키지를 설정한다.
# @author : Sang Wook, Song
#
# @param 1 : zookeeper address		zk://localhost:2181/mesos
# @param 2 : mesos cluster name
# @param 3 : mesos master public ip
# @param 4 : mesos master private ip
# @param 5 : quorum		3
# @param 6 : zookeeper address 1  server.1=zookeeper1:2888:3888
# @param 7 : zookeeper address 2  server.2=zookeeper1:2888:3888
# @param 8 : zookeeper address 3  server.3=zookeeper1:2888:3888
#

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




