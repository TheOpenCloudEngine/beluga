#!/usr/bin/env bash
#
# 이미지를 Docker Registry 에 등록한다.
# @author : Seungpil, Park
#

if [[ "$OSTYPE" == "darwin"* ]]; then
	# Mac Os X
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    source "$DIR/../osx/mac-osx-docker-booting.sh"
fi

echo Command : $0 "$@"

image_name="$1"

echo docker push $image_name
docker push $image_name