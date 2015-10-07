#!/bin/bash

mvn clean

mvn release:prepare || exit

cd target

name=`ls |grep beluga`

tar czvf "$name".tar.gz "$name"/

cd .. 

mvn release:clean