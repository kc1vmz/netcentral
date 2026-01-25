#!/bin/bash
# start APRS-IS
echo Configuring environment variables for services
/netcentral/bin/config_env.sh
echo Starting Net Central APRS-IS transceiver
rc-service netcentral-transceiver-aprsis start

