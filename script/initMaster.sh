sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

sudo apt-get -y update

sudo apt-get -y install mesos

sudo apt-get -y install marathon

mv /etc/init/mesos-slave.conf /etc/init/mesos-slave.conf.bak

#zk
echo $1 > /etc/mesos/zk

#cluster
echo $2 > /etc/mesos-master/cluster

#public IP
echo $3 > /etc/mesos-master/hostname

#private IP
echo $4 > /etc/mesos-master/ip

#quorum
echo $5 > /etc/mesos-master/quorum

#zookeeper id
echo $6 > /etc/zookeeper/conf/myid

#zookeeper servers
echo $7 >> /etc/zookeeper/conf/zoo.cfg
echo $8 >> /etc/zookeeper/conf/zoo.cfg
echo $9 >> /etc/zookeeper/conf/zoo.cfg

sudo reboot
