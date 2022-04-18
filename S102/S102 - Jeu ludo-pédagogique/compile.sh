#!/bin/bash
export SOURCES=src
export CLASSES=classes
export LIB=lib
export RESSOURCES=ressources
export CLASSPATH=`find ${LIB} -name "*.jar" | tr '\n' ':'`

javac -cp ${CLASSPATH} -sourcepath ${SOURCES} -d ${CLASSES} $@ `find ${SOURCES} -name "*.java"`
cp -r ${RESSOURCES}/* ${CLASSES}/