#!/bin/bash
export JAVA_HOME=${JAVA_HOME_1_6}
export PATH=${JAVA_HOME_1_6}/bin:${PATH}

if [ "x$MAVEN_HOME" != "x" ]
then
    MVN_BIN=$MAVEN_HOME/bin/mvn
else
	MVN_BIN=mvn
fi

cd `dirname $0`
BUILD_DIR=`pwd`

echo $BUILD_DIR

function check_error()
{
	if [ ${?} -ne 0 ]
	then
		echo "Error! Please Check..."
 		exit 1
	fi
}

#--------------------------------------
#mvn install
#--------------------------------------
$MVN_BIN deploy 
check_error
mkdir -p output
cp -r ./target/ ./output
check_error
rm -r ./target/