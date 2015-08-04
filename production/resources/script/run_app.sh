#!/usr/bin/env bash
#
# Docker Registry 에서 이미지를 가져와서 Marathon 으로 실행한다.
# @author : Sang Wook, Song
#

if [ $# -ne 6 ] ; then
    echo "Usage: $0 <user_name> <image_id> <port> <cpus> <memory> <instance_size>"
    echo "Sample: $0 oce java-calendar 8080 0.3 256 2"
    exit 1
fi

user_name="$1"
image_id="$2"
port="$3"
cpus="$4"
memory="$5"
instance_size="$6"


. config.sh

app_name="$user_name"-"$image_id"

cat <<EndOfMessage > request.json
{
    "constraints": [
        [
            "hostname",
            "UNIQUE"
        ]
    ],
    "container": {
        "docker": {
                "image": "$registry_address/$image_id",
                "network": "BRIDGE",
                "portMappings": [
                    {
                        "containerPort": $port,
                        "hostPort": 0,
                        "protocol": "tcp"
                    }
                ],
                "privileged": true,
                "parameters": []
            },
        "type": "DOCKER"
    },
    "cpus": $cpus,
    "type": "$app_name",
    "instances": $instance_size,
    "mem": $memory,
    "ports": [
        0
    ],
    "upgradeStrategy": {
        "minimumHealthCapacity": 0.5,
        "maximumOverCapacity": 0.5
    }
}
EndOfMessage

#echo curl -X POST -H "\"Content-Type: application/json\"" http://$marathon_address/v2/apps -d@request.json
curl -X POST -H "\"Content-Type: application/json\"" http://$marathon_address/v2/apps -d@request.json