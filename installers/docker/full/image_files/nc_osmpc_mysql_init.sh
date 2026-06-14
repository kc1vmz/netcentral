#!/bin/bash
NC_SQL_INIT="SET GLOBAL max_connections = 128;create schema netcentral_osmproxycache;create user netcentral_osmproxycache@'localhost' IDENTIFIED BY 'netcentral_osmproxycache';grant all privileges on netcentral_osmproxycache.* to 'netcentral_osmproxycache'@'localhost';flush privileges;"
echo $NC_SQL_INIT | sudo mariadb -u root

mv /netcentral/src/nc_osmpc_mysql_init_done.sh /netcentral/src/nc_osmpc_mysql_init.sh
