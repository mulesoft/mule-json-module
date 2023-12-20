#!/bin/bash
RUNTIME_VERSION=4.6.0-SNAPSHOT
MUNIT_JVM=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java
mvn clean
mkdir pepe
mvn verify \
   -DruntimeProduct=MULE_EE \
   -DruntimeVersion=$RUNTIME_VERSION \
   -Dmunit.jvm=$MUNIT_JVM \
   -Dmule.module.tweaking.validation.skip=true \
   -Dmule.jvm.version.extension.enforcement=LOOSE > ./pepe/test.log