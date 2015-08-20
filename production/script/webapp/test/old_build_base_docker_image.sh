#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 1 ] ; then
    echo "Please put registry server address."
    exit 1
fi

registry_address="$1:5000"

for image_name in java7_tomcat7
do
    docker build -t $registry_address/$image_name -f dockerfile/$image_name dockerfile/ && docker push $registry_address/$image_name
done
