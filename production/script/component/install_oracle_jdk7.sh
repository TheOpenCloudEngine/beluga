#!/bin/bash
#
# Oracle JDK 7 설치
# Author : Sang Song
# Update : 2015.6.27
#
echo ============
echo Install JDK
echo ============
sudo apt-get -y install python-software-properties && sudo add-apt-repository -y ppa:webupd8team/java && sudo apt-get update && sudo apt-get install -y oracle-java7-installer

