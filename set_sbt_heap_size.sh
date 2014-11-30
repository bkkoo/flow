#!/bin/bash
#set 2G max heap
export SBT_OPTS="-Xmx4G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=4G -Xss10M  -Duser.timezone=ICT"
export JAVA_OPTS="-Xmx4G -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=4G -Xss10M  -Duser.timezone=ICT"
