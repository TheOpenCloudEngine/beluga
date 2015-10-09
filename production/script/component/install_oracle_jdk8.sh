#!/bin/bash
#
# Oracle JDK 8 설치
# Author : Sang Song
# Update : 2015.10.08
#
echo ============
echo Install JDK8
echo ============
sudo add-apt-repository ppa:webupd8team/java && sudo apt-get update -y && sudo apt-get install oracle-java8-installer -y

