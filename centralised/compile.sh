#! /bin/bash


if [ ! -d bin ]; then
    mkdir bin
fi
javac -d bin -sourcepath src src/client/*.java
