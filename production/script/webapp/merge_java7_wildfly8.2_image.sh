#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 2 ] ; then
    echo "Usage: $0 <image_name> <war_file>"
    echo "Sample: $0 192.168.0.10/java-calendar Calendar.war"
    exit 1
fi

base_image=fastcat/java7_wildfly8.2
work_dir="/tmp"

image_name="$1"
war_file="$2"

cd "$work_dir"

temp_dir=$(uuidgen)
mkdir $temp_dir
cd $temp_dir

cp $war_file ./app.war

echo FROM "$base_image" > Dockerfile
echo COPY app.war /root/wildfly/standalone/deployments/ROOT.war >> Dockerfile

docker build -t "$image_name" .

push_result=$?

cd ..
rm -rf $temp_dir

if [ $push_result -ne 0 ]; then
    exit 1
fi