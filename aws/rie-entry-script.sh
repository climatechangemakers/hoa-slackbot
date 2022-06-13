#!/bin/sh
if [ -z "${AWS_LAMBDA_RUNTIME_API}" ]; then
  exec /usr/local/bin/aws-lambda-rie /usr/local/bin/slackbot.kexe $@
else
  exec /usr/local/bin/slackbot.kexe $@
fi