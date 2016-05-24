#!/usr/bin/env bash
#
# Mac os x 에서의 도커 데몬 호스트를 알아낸다.
# @author : Seungpil, Park
#

if [[ "$OSTYPE" == "darwin"* ]]; then
	# Mac Os X
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    source "$DIR/../osx/mac-osx-docker-booting.sh"
fi

echo Command : $0 "$@"

docker-machine ip default