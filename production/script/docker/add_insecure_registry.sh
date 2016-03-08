#!/usr/bin/env bash
#
# 접근할 레지스트리 주소를 도커 설정에 추가한다.
# @author : Sang Wook, Song
#

if [ $# -ne 1 ] ; then
    echo "Please put registry server address."
    exit 1
fi

echo DOCKER_OPTS=\"\$DOCKER_OPTS --insecure-registry $1:5000\" | sudo tee -a /etc/default/docker && sudo service docker restart
