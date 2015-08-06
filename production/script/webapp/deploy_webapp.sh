#!/usr/bin/env bash
#
# Docker Registry 에서 이미지를 가져와서 Marathon 으로 실행한다.
# @author : Sang Wook, Song
#

if [ $# -ne 7 ] ; then
    echo "Usage: $0 <marathon_address> <user_name> <image_id> <port> <cpus> <memory> <instance_size>"
    echo "Sample: $0 192.168.0.10 oce java-calendar 8080 0.3 256 2"
    exit 1
fi

marathon_address="$1"
user_name="$2"
image_id="$3"
port="$4"
cpus="$5"
memory="$6"
instance_size="$7"

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

curl -X POST -H "\"Content-Type: application/json\"" http://$marathon_address:8080/v2/apps -d@request.json