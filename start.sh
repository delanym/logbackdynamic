#!/usr/bin/env sh

set -eu

APP_JAR="target/logbackdynamic-1.0.0.jar"

JAVA_OPTS="
  -Dlog.dir=logs
  -Dapp.name=installationstarter
  -Dlogback.configurationFile=installationstarter/logback.xml
  -Dlogback.debug=true
"

JAVA_OPTS="
  -Dlogback.configurationFile=installationstarter/logback.xml
  -Dlogback.debug=true
"

# shellcheck disable=SC2086
exec java ${JAVA_OPTS} -jar "${APP_JAR}"
