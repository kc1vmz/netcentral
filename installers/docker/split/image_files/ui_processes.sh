#!/bin/bash
# start UI
echo Configuring environment variables for services
/netcentral/bin/config_env.sh
echo starting UI
cd /netcentral/bin/ui
npm run dev  </dev/null &>/dev/null &
cd /netcentral

