#!/bin/bash
# start Net Central OSM Proxy Cache
echo Configuring environment variables for services
/netcentral/bin/config_env.sh
echo Starting Net Central OSM Proxy Cache
rc-service osm-proxy-cache start
