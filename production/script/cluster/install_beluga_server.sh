#!/usr/bin/env bash
#
# 벨루가 서버 노드에 Docker,Mysql, Java, Tomcat 패키지를 설치한다.
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
echo Install MySQL5.6
echo ============
echo "mysql-server-5.6 mysql-server/root_password password beluga123:)" | sudo debconf-set-selections
echo "mysql-server-5.6 mysql-server/root_password_again password beluga123:)" | sudo debconf-set-selections

sudo apt-get install mysql-server-5.6 -y

sudo sed -i 's/127\.0\.0\.1/0\.0\.0\.0/g' /etc/mysql/my.cnf
sudo sed -i 's/\[mysqld\]/\[mysqld\]\nlower_case_table_names=1/g' /etc/mysql/my.cnf

mysql -uroot -p'beluga123:)' -e 'USE mysql; UPDATE `user` SET `Host`="%" WHERE `User`="root" AND `Host`="localhost"; DELETE FROM `user` WHERE `Host` != "%" AND `User`="root"; FLUSH PRIVILEGES;'

sudo service mysql restart

echo ============
echo Install JDK8
echo ============
sudo add-apt-repository ppa:webupd8team/java && sudo apt-get update -y && sudo apt-get install oracle-java8-installer -y

echo ============
echo Install Tomcat7
echo ============
sudo apt-get install tomcat7 -y
