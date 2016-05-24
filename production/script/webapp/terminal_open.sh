#!/usr/bin/env bash
#
# 이미지의 터미널을 오픈한다.
# @author : Seungpil, Park
#

if [[ "$OSTYPE" == "darwin"* ]]; then
	# Mac Os X
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    source "$DIR/../osx/mac-osx-docker-booting.sh"
fi

echo Command : $0 "$@"

container_name="$1"
image_name="$2"
bind_port="$3"
container_port="$4"

echo docker run -d -p $bind_port:$container_port --name $container_name $image_name
docker rm --force `docker ps -qa`
docker run -d -p $bind_port:$container_port --name $container_name $image_name npm start