source ./config.sh

zip_file="$1"
new_image="$2"
instance_size="$3"
user_name="$4"
app_name="$user_name"_"$new_image"

base_image=php5_apache2

data={ << EOF

    "constraints": [
        [
            "hostname",
            "UNIQUE"
        ]
    ],
    "container": {
        "docker": {
	        "image": "$registry_address/php-sample-img",
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
    "healthChecks": [
        {
            "gracePeriodSeconds": 3,
            "intervalSeconds": 10,
            "maxConsecutiveFailures": 3,
            "path": "/",
            "portIndex": 0,
            "protocol": "HTTP",
            "timeoutSeconds": 5
        }
    ],
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
EOF

curl -X POST -H "Content-Type: application/json" http://$marathon_address/v2/apps -d $data