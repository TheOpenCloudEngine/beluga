#!/bin/sh

work_dir="$1"
zip_file="$2"
new_image="$3"

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