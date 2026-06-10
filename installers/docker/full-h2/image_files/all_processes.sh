#!/bin/bash

cd /netcentral/src

echo Configuring environment variables for services
/netcentral/src/config_env.sh
# start Net Central server
echo Starting Net Central server
rc-service netcentral-server start
sleep 10
# start APRS-IS
echo Starting Net Central APRS-IS transceiver
rc-service netcentral-transceiver-aprsis start
sleep 10
# start UI
echo starting UI
cd ui
npm run dev  </dev/null &>/dev/null &
cd /netcentral

