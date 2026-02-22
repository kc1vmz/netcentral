#!/bin/bash
# start Net Central server
echo Configuring environment variables for services
/netcentral/bin/config_env.sh
echo Starting Net Central server
rc-service netcentral-server start
