sudo apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF

DISTRO=$(lsb_release -is | tr '[:upper:]' '[:lower:]')

CODENAME=$(lsb_release -cs)

echo "deb http://repos.mesosphere.io/${DISTRO} ${CODENAME} main" | \
  sudo tee /etc/apt/sources.list.d/mesosphere.list

sudo apt-get -y update

sudo apt-get -y install mesos

sudo wget -qO- https://get.docker.com/ | sh

mv /etc/init/mesos-master.conf /etc/init/mesos-master.conf.bak

#zk
echo $1 > /etc/mesos/zk

#public IP
echo $2 > /etc/mesos-slave/hostname

#private IP
echo $3 > /etc/mesos-slave/ip

#containerizers
echo $4 > /etc/mesos-slave/containerizers
