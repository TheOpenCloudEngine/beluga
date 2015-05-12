source ./config.sh
image_name=$1
docker build -t $registry_address/$image_name -f ../dockerfile/$image_name ../dockerfile/ && docker push $registry_address/$image_name