#!/bin/sh

SWAGGER_BASE=`env | grep OPENSHIFT_APP_DNS | awk -F= '{print "http://" $2}'`

echo "SWAGGER_BASE=$SWAGGER_BASE"
