#!/bin/sh

mkdir -p lib class tmp

COMMONS_DAEMON_VER=commons-daemon-1.2.2

javac -cp lib/${COMMONS_DAEMON_VER}.jar -d class/ src/*
jar cfe lib/pjbridge.jar Server -C class .
cp lib/pjbridge.jar ../pjbridge.jar