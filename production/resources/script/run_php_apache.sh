#!/bin/sh
source ./config.sh

new_image="$1"
instance_size="$2"
user_name="$3"

app_name="$user_name"-"$new_image"

base_image=php5_apache2

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
                "image": "$registry_address/$new_image",
                "network": "BRIDGE",
                "portMappings": [
                    {
                        "containerPort": 80,
                        "hostPort": 0,
                        "protocol": "tcp"
                    }
                ],
                "privileged": true,
                "parameters": []
            },
        "type": "DOCKER"
    },
    "cpus": 0.5,
    "id": "$app_name",
    "instances": $instance_size,
    "mem": 50,
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