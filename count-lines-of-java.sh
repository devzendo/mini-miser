#!/bin/sh
java=`find src/main/java -name "*.java" -exec cat {} \; | wc -l | sed 's/ //g'`
test=`find src/test/java -name "*.java" -exec cat {} \; | wc -l | sed 's/ //g'`
all=`expr ${java} + ${test}`
echo "Lines of Java: $all (main: ${java}; test: ${test})"
