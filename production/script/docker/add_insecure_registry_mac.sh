#!/usr/bin/env bash
#
# Mac 에서 접근할 레지스트리 주소를 도커 설정에 추가한다.
# Mac 도커머신을 통해 버츄얼 머신에 설정을 주입 한 후에, 머신을 재부팅한다.
# mac-osx-docker-booting.sh 을 실행 후에 실행하도록 한다.
# @author : Sang Wook, Song
#

if [ $# -ne 1 ] ; then
    echo "Please put registry server address."
    exit 1
fi

echo "Excute: sudo sed -i \"s/--label provider=virtualbox.*/--label provider=virtualbox --insecure-registry $1:5000/g\" /var/lib/boot2docker/profile"

docker-machine ssh default "sudo sed -i \"s/--label provider=virtualbox.*/--label provider=virtualbox --insecure-registry $1:5000/g\" /var/lib/boot2docker/profile"
docker-machine restart default

