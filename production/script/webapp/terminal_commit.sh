#!/usr/bin/env bash
#
# 터미널이 연결된 컨테이너를 커밋한다.
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
docker_cmd="$3"
expose_port="$4"

docker_cmd=${docker_cmd//,__/ }

echo docker commit -c "CMD $docker_cmd" -c "EXPOSE $expose_port" $container_name $image_name

docker stop $container_name
docker commit -c "CMD $docker_cmd" -c "EXPOSE $expose_port" $container_name $image_name
docker rm --force `docker ps -qa`