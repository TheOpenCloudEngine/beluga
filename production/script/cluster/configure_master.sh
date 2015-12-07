#!/usr/bin/env bash
#
# 마스터노드의 패키지를 설정한다.
# @author : Sang Wook, Song
#
# @param 1 : zookeeper address		zk://192.168.2.44:2181,192.168.2.45:2181,192.168.2.46:2181/mesos
# @param 2 : mesos cluster name
# @param 3 : mesos master public ip
# @param 4 : mesos master private ip
# @param 5 : quorum		            2
# @param 6 : zookeeper id           1
# @param 7 : zookeeper address 1   server.1=192.168.2.44:2888:3888
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

#zookeeper id
echo $6 | sudo tee /etc/zookeeper/conf/myid

#zookeeper servers
until [ -z "$7" ]; do
  echo $7 | sudo tee -a /etc/zookeeper/conf/zoo.cfg
  shift
done

echo "mesos-master" | sudo tee /etc/hostname

echo "127.0.0.1 mesos-master" | sudo tee -a /etc/hosts

sudo service zookeeper restart

sudo service mesos-master restart

sudo service marathon restart

sudo service docker restart

echo -e "1\n1" | sudo passwd

