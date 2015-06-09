#!/bin/bash
# -----------------------------------------------------------------------------
# Set java exe and conf file for all scripts
#
# -----------------------------------------------------------------------------

echo '++++++++++ YAJSW SET ENV ++++++++++'

#remember current dir
current1=$(pwd)
# resolve links - $0 may be a softlink
PRGDIR=$(dirname $0)

source ../../bin/environment.sh

cd "$current1"
cd ..

# path to wrapper home
wrapper_home=$(pwd)
export wrapper_home

# return to original folder
cd "$current1"

wrapper_jar="$wrapper_home"/wrapper.jar
export wrapper_jar

wrapper_app_jar="$wrapper_home"/wrapperApp.jar
export wrapper_app_jar

wrapper_java_sys_options=-Djna_tmpdir="$wrapper_home"/tmp
export wrapper_java_sys_options

wrapper_java_options=-Xmx30m
export wrapper_java_options

if [ -n "$java_path" ] ; then
	java_exe=$java_path
	echo Use custom java path="$java_exe"
else
	java_exe=java
fi

export java_exe

# show java version
"$java_exe" -version

conf_file="$wrapper_home"/conf/wrapper.conf
export conf_file

conf_default_file="$wrapper_home"/conf/wrapper.conf.default
export conf_default_file

echo "wrapper home : $wrapper_home"
echo "configuration: $conf_file"

# show java version
#"$java_exe" -version
echo '---------- YAJSW SET ENV ----------'