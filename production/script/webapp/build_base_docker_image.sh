#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지를 생성하고, Docker Hub Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 1 ] ; then
    echo "Please put image name."
    exit 1
fi

account="fastcat"
image_name="$1"

docker build -t $account/$image_name -f dockerfile/$image_name dockerfile/ && docker push $account/$image_name
