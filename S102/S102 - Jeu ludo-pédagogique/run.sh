#!/bin/bash
export CLASSES=classes
export LIB=lib
export RESSOURCES=ressources

cp -r -i ${RESSOURCES}/* ${CLASSES}/
cd ${CLASSES}
export CLASSPATH=`find ../${LIB} -name "*.jar" | tr '\n' ':'`
java -cp ${CLASSPATH}:. $@
cd ..