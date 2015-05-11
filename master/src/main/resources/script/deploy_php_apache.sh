filename=$(uuidgen -t)
zip_file="$1"
new_image="$2"
registry_address="$3"
base_image="$4"

echo FROM "$registry_address"/"$base_image" >> $filename
echo COPY "$zip_file" /var/www/html/ >> $filename
echo RUN ["\"unzip\"", "\"-oq\"", "\"/var/www/html/$zip_file\"", "\"-d\"", "\"/var/www/html/\""] >> $filename
echo RUN ["\"rm\"", "\"-f\"", "\"/var/www/html/$zip_file\""] >> $filename
docker build -t "$registry_address"/"$new_image" -f $filename .

docker push "$registry_address"/"$new_image"

rm $filename