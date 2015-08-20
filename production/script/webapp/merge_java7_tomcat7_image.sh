#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <image_name> <war_file> <memory_size_MB>"
    echo "Sample: $0 192.168.0.10/java-calendar Calendar.war 300"
    exit 1
fi

base_image=fastcat/java7_tomcat7
work_dir="/tmp"

image_name="$1"
war_file="$2"
memory_size="$3"

jvm_xmx=$(($memory_size/10*8))
jvm_perm_size=$(($memory_size/10 + 50))

cd "$work_dir"

temp_dir=$(uuidgen)
mkdir $temp_dir
cd $temp_dir

cp $war_file ./app.war

echo FROM "$base_image" > Dockerfile
echo COPY app.war /root/wildfly/standalone/deployments/ROOT.war >> Dockerfile
echo ENV JAVA_OPTS -Xms64m -Xmx${jvm_xmx}m -XX:MaxPermSize=${jvm_perm_size}m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true >> Dockerfile
docker build -t "$image_name" .

push_result=$?

cd ..
rm -rf $temp_dir

if [ $push_result -ne 0 ]; then
    exit 1
fi