#디렉토리 생성
mkdir -p /data/repo/mesos && mkdir /data/programs && mkdir /data/zookeeper && mkdir -p /data/conf/zoo && mkdir /data/logs && cd /data/programs

#wget 설치
yum -y install wget

#java7 설치
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/7u75-b13/jdk-7u75-linux-x64.tar.gz"
tar xfvz jdk-7u75-linux-x64.tar.gz && rm -f jdk-7u75-linux-x64.tar.gz
alternatives --install /usr/bin/java java /data/programs/jdk1.7.0_75/bin/java 2

#메이븐 설치
wget http://apache.mirror.cdnetworks.com/maven/maven-3/3.3.1/binaries/apache-maven-3.3.1-bin.tar.gz
tar xfvz apache-maven-3.3.1-bin.tar.gz && rm -f apache-maven-3.3.1-bin.tar.gz

#환경 변수 설정
echo export JAVA_HOME=/data/programs/jdk1.7.0_75 >> /etc/profile
echo export M2_HOME=/data/programs/apache-maven-3.3.1 >> /etc/profile
echo 'export PATH=$M2_HOME/bin:$PATH' >> /etc/profile
 . /etc/profile

#필수 패키지 설치
wget http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-5.noarch.rpm
rpm -Uvh epel-release*rpm && rm -f epel-release*rpm
yum -y install libserf-devel
yum groupinstall -y "Development Tools"
yum install -y python-devel java-1.7.0-openjdk-devel zlib-devel libcurl-devel openssl-devel cyrus-sasl-devel cyrus-sasl-md5 apr-devel subversion-devel apr-utils-devel git apr-util-devel perf

#mesos
git clone https://git-wip-us.apache.org/repos/asf/mesos.git /data/programs/mesos

cd /data/programs/mesos

./bootstrap

mkdir build && cd build

../configure

make

make install