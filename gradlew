#!/bin/sh
GRADLE_HOME="$(dirname "$0")"
exec gradle -p "$GRADLE_HOME" "$@"
