#!/bin/sh

CATALINA_OPTS=" -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9005  -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

JAVA_OPTS="-Djvm=$JVM_ID -Xms2G -Xmx4G -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dhttp.maxConnections=100"
