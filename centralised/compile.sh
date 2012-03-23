#! /bin/bash


if [ ! -d bin ]; then
    mkdir bin
fi
javac -d bin -sourcepath src/client/ src/client/*.java
