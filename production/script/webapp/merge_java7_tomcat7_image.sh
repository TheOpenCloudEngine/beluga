#!/usr/bin/env bash
#
# 어플리케이션 스택 도커이미지에 webapp을 적용하여 최종 이미지를 생성하고, Docker Registry 에 등록한다.
# @author : Sang Wook, Song
#

echo Command : $0 "$@"

if [ $# -lt 4 ] ; then
    echo "Usage: $0 <image_name> <memory_size_MB> <war_file1> <context1> [<war_file2> <context2>]"
    echo "Sample: $0 192.168.0.10/java-calendar 300 Calendar.war /"
    exit 1
fi

base_image=fastcat/java7_tomcat7
work_dir="/tmp"

image_name="$1"
memory_size="$2"
shift
shift

arg_size=$#

if (( arg_size % 2 == 1 )); then
	echo "wrong arguments."
	exit 1;
fi

jvm_xmx=$(($memory_size/10*8))
jvm_perm_size=$(($memory_size/10 + 50))

cd "$work_dir"

temp_dir=$(uuidgen)
mkdir $temp_dir
cd $temp_dir

#cp $war_file ./app.war

echo FROM "$base_image" > Dockerfile
echo RUN rm -rf /usr/local/tomcat/webapps/* >> Dockerfile
echo ENV CATALINA_OPTS -server -Xms64m -Xmx${jvm_xmx}m -XX:MaxPermSize=${jvm_perm_size}m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom >> Dockerfile

for (( i = 0; i < arg_size; i+=2 ))
do
	filename="$1"
	shift
	echo cp $filename ./app${i}.war
	cp $filename ./app${i}.war
	path="$1"
	shift
	# path 를 보고 ROOT 로 할지 context로 할지 결정.
	if [[ $path == / ]]; then
		context=ROOT
	elif [[ $path == /* ]]; then
		context=${path:1}
    else
		echo "context path must starts with '/' : $path"
		exit 1;
	fi
    # write to Dockerfile
	echo COPY app${i}.war /usr/local/tomcat/webapps/${context}.war \>\> Dockerfile
	echo COPY app${i}.war /usr/local/tomcat/webapps/${context}.war >> Dockerfile
done

echo docker build -t "$image_name" .
docker build -t "$image_name" .

push_result=$?

cd ..
rm -rf $temp_dir

if [ $push_result -ne 0 ]; then
    exit 1
fi

echo SUCCESS $0