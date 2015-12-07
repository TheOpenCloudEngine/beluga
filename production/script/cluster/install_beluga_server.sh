#!/usr/bin/env bash
#
# 벨루가 서버 노드에 Docker, Java, Tomcat 패키지를 설치한다.
# mysql의 root계정 비번은 beluga123:) 이다.
# @author : Sang Wook, Song
#

sudo apt-get -y update


echo ============
echo Install Docker
echo ============
sudo wget -qO- https://get.docker.com/ | sh

sudo usermod -aG docker ubuntu

echo ============
echo Install JDK8
echo ============
sudo add-apt-repository ppa:webupd8team/java && sudo apt-get update -y && sudo apt-get install oracle-java8-installer -y

echo ============
echo Install Tomcat7
echo ============
sudo apt-get install tomcat7 -y
