#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <work_dir> <war_file> <new_image>"
    echo "Sample: $0 oce Calendar.war java-calendar"
    exit 1
fi

work_dir="$1"
war_file="$2"
new_image="$3"

. config.sh

cd "$work_dir"

base_image=java7_wildfly8.2
filename=$(uuidgen -t)

echo FROM "$registry_address"/"$base_image" >> $filename
echo COPY "$war_file" /root/wildfly/standalone/deployments/ROOT.war >> $filename
docker build -t "$registry_address"/"$new_image" -f $filename .

docker push "$registry_address"/"$new_image"

rm $filename