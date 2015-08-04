#!/usr/bin/env bash
#
# 워커노드의 패키지를 설치한다.
# @author : Sang Wook, Song
#
# @param 1 : zookeeper address
# @param 2 : mesos master public ip
# @param 3 : mesos master private ip
# @param 4 : mesos container. "docker" for docker, "mesos" for mesos
# @param 5 : docker registry address
#

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




