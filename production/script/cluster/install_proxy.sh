#!/usr/bin/env bash
#
# HAProxy 설치
# @author : Sang Wook, Song
#
sudo apt-add-repository -y ppa:vbernat/haproxy-1.5
sudo apt-get -y update
sudo apt-get install haproxy

cat <<EndOfStatConfig | sudo tee -a /etc/haproxy/haproxy.cfg

listen stats :1900
    mode http
    stats enable
    stats hide-version
    stats realm Haproxy\ Statistics
    stats uri /
    stats auth garuda:garuda123:)
EndOfStatConfig
