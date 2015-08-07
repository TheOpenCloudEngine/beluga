#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <registry_address> <zip_file> <new_image>"
    echo "Sample: $0 192.168.0.10 Calendar.zip php-calendar"
    exit 1
fi

registry_address="$1:5000"
zip_file="$2"
new_image="$3"

work_dir="/tmp"

cd "$work_dir"

#registry_address=
base_image=php5_apache2
filename=$(uuidgen -t)

echo FROM "$registry_address"/"$base_image" >> $filename
echo COPY "$zip_file" /var/www/html/ >> $filename
echo RUN ["\"unzip\"", "\"-oq\"", "\"/var/www/html/$zip_file\"", "\"-d\"", "\"/var/www/html/\""] >> $filename
echo RUN ["\"rm\"", "\"-f\"", "\"/var/www/html/$zip_file\""] >> $filename
docker build -t "$registry_address"/"$new_image" -f $filename .

docker push "$registry_address"/"$new_image"

rm $filename