#!/bin/bash

cd /netcentral/src

# start MariaDB and wait
echo Starting database
rc-service mariadb start
sleep 10
# configure database and environment variables
echo Configuring database
/netcentral/src/nc_mysql_init.sh
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

