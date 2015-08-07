#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <registry_address> <war_file> <image_name>"
    echo "Sample: $0 192.168.0.10 Calendar.war java-calendar"
    exit 1
fi

base_image=fastcat/java7_wildfly8.2
work_dir="/tmp"

registry_address="$1:5000"
war_file="$2"
image_name="$3"

cd "$work_dir"

temp_dir=$(uuidgen)
mkdir $temp_dir
cd $temp_dir

cp $war_file ./app.war

echo FROM "$base_image" > Dockerfile
echo COPY app.war /root/wildfly/standalone/deployments/ROOT.war >> Dockerfile

echo docker build -t "$registry_address"/"$image_name" .
docker build -t "$registry_address"/"$image_name" .

echo docker push "$registry_address"/"$image_name"
docker push "$registry_address"/"$image_name"

cd ..
rm -rf $temp_dir