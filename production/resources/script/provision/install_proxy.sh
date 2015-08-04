#!/usr/bin/env bash
#
# HAProxy 설치
# @author : Sang Wook, Song
#

sudo apt-get -y update

sudo apt-get install haproxy

#일반 시작 : sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid
#빠른 재시작 : sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)