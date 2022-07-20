#!/usr/bin/env bash
set -e
set -u
set -o pipefail

exec /usr/local/bin/aws-lambda-rie /usr/local/bin/event.kexe $@
