#!/bin/bash
exec -a sentinel java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=localhost:3000 \
    -Dserver.port=9900 \
    -Dcsp.sentinel.dashboard.server=localhost:9900 \
    -Dproject.name=sentinel-dashboard \
    -Dcsp.sentinel.app.type=1 \
    -jar ./target/sentinel-dashboard.jar
