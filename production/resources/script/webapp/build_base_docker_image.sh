#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

. config.sh
image_name=$1
docker build -t $registry_address/$image_name -f dockerfile/$image_name ../dockerfile/ && docker push $registry_address/$image_name