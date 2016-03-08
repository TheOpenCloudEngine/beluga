#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

if [[ "$OSTYPE" == "darwin"* ]]; then
	# Mac Os X
    DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
    source "$DIR/../osx/mac-osx-docker-booting.sh"
fi

if [ $# -ne 3 ] ; then
    echo "Usage: $0 <image_name> <zip_file>"
    echo "Sample: $0 192.168.0.10/php-calendar Calendar.zip"
    exit 1
fi

base_image=fastcat/php5_apache2
work_dir="/tmp"

image_name="$1"
zip_file="$2"

cd "$work_dir"

temp_dir=$(uuidgen)
mkdir $temp_dir
cd $temp_dir

cp $zip_file ./app.zip

echo FROM "$registry_address"/"$base_image" > Dockerfile
echo COPY app.zip /var/www/html/ >> Dockerfile
echo RUN ["\"unzip\"", "\"-oq\"", "\"/var/www/html/app.zip\"", "\"-d\"", "\"/var/www/html/\""] >> Dockerfile
echo RUN ["\"rm\"", "\"-f\"", "\"/var/www/html/app.zip\""] >> Dockerfile

docker build -t "$image_name" .

push_result=$?

cd ..
rm -rf $temp_dir

if [ $push_result -ne 0 ]; then
    exit 1
fi