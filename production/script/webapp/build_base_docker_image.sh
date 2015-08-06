#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

registry_address=$1:5000
image_name_list=(java7_wildfly8.2 php5_apache2)

for image_name in $image_name_list
do
    docker build -t $registry_address/$image_name -f dockerfile/$image_name dockerfile/ && docker push $registry_address/$image_name
done