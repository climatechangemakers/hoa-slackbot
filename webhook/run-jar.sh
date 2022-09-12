#!/usr/bin/env bash
set -e
set -u
set -o pipefail

java -jar /usr/local/bin/webhook-0.0.1-all.jar
