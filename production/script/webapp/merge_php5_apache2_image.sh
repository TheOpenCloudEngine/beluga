#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <registry_address> <zip_file> <image_name>"
    echo "Sample: $0 192.168.0.10 Calendar.zip php-calendar"
    exit 1
fi

base_image=fastcat/php5_apache2
work_dir="/tmp"

registry_address=$1
zip_file="$2"
image_name="$3"

cd "$work_dir"

filename=$(uuidgen)
mkdir $filename
cd $filename

cp $zip_file ./app.zip

echo FROM "$registry_address"/"$base_image" > Dockerfile
echo COPY app.zip /var/www/html/ >> Dockerfile
echo RUN ["\"unzip\"", "\"-oq\"", "\"/var/www/html/app.zip\"", "\"-d\"", "\"/var/www/html/\""] >> Dockerfile
echo RUN ["\"rm\"", "\"-f\"", "\"/var/www/html/app.zip\""] >> Dockerfile

docker build -t "$image_name" .

docker push "$registry_address"/"$image_name"

cd ..
rm -rf $filename