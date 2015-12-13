#!/bin/sh

if [ ! -d libs ];
then
    mkdir libs
fi

if [ ! -f libs/square-otto-1.3.2.jar ];
then
    curl -o libs/square-otto-1.3.2.jar https://cloud.github.com/downloads/square/otto/square-otto-1.3.2.jar
fi