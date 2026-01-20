#!/bin/bash
NC_SQL_INIT="SET GLOBAL max_connections = 128;create schema netcentral;create user netcentral@'localhost' IDENTIFIED BY 'netcentral';grant all privileges on netcentral.* to 'netcentral'@'localhost';flush privileges;"
echo $NC_SQL_INIT | sudo mariadb -u root

mv /netcentral/src/nc_mysql_init_done.sh /netcentral/src/nc_mysql_init.sh
