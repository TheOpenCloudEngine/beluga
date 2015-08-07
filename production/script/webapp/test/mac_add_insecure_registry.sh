#!/bin/bash

if [ $# -ne 1 ] ; then
    echo "Please put registry server address."
    exit 1
fi

boot2docker ssh "echo $'EXTRA_ARGS=\"--insecure-registry $1:5000\"' | sudo tee -a /var/lib/boot2docker/profile && sudo /etc/init.d/docker restart"
