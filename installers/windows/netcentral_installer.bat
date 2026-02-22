@echo off
echo Welcome to the Net Central installer for Microsoft Windows

SET NC_INSTALL_DIR=%USERPROFILE%\netcentral
SET NC_DB_NAME=netcentral
SET NC_DB_USER=netcentral
SET NC_DB_PASS=netcentral
SET NC_SVC_USER=serviceAccount
SET NC_SVC_PASS=serviceAccountPassword
SET NC_DEVMODE=N
SET NC_BUILD_SRC=N
SET NC_TEMP_DIR=%TEMP%\netcentral
SET NC_TR_APRSIS=Y
SET NC_TR_APRSIS_QUERY=r/42.222/-71.57/500
SET NC_VERSION=1.0.11
SET NC_DB_CREATE=Y
SET NC_JAVA_INSTALL=Y
SET NC_INSTALL_SERVICES=Y

REM Prompt the user for input
SET /P "NC_VERSION=What version of Net Central? (Default: 1.0.11): "
SET /P "NC_INSTALL_DIR=Where should Net Central be installed? (Default: %NC_INSTALL_DIR%): "
SET /P "NC_TEMP_DIR=Where should Net Central temp space be located? (Default: %NC_TEMP_DIR%): "
SET /P "NC_DB_NAME=Net Central database name? (Default: %NC_DB_NAME%): "
SET /P "NC_DB_USER=Net Central database username? (Default: %NC_DB_USER%): "
SET /P "NC_DB_PASS=Net Central database password?: "
SET /P "NC_SVC_USER=Net Central service account username? (Default: %NC_SVC_USER%): "
SET /P "NC_SVC_PASS=Net Central service account password?: "
SET /P "NC_CALLSIGN=What is your callsign?: "

SET NC_SRC_URL=https://github.com/kc1vmz/netcentral/archive/refs/tags/v%NC_VERSION%.zip

SET NETCENTRAL_APRS_CALLSIGN=%NC_CALLSIGN%
SET NETCENTRAL_SERVER_TEMP_DIR=%NC_TEMP_DIR%
SET NETCENTRAL_SERVER_USERNAME=%NC_SVC_USER%
SET NETCENTRAL_SERVER_PASSWORD=%NC_SVC_PASS%
SET NETCENTRAL_SERVER_MYSQL_USERNAME=%NC_DB_USER%
SET NETCENTRAL_SERVER_MYSQL_PASSWORD=%NC_DB_PASS%
SET NETCENTRAL_SERVER_MYSQL_DBNAME=%NC_DB_NAME%

rem  echo "NETCENTRAL_APRS_CALLSIGN=$NETCENTRAL_APRS_CALLSIGN" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_TEMP_DIR=$NETCENTRAL_SERVER_TEMP_DIR" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_USERNAME=$NETCENTRAL_SERVER_USERNAME" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_PASSWORD=$NETCENTRAL_SERVER_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_MYSQL_USERNAME=$NETCENTRAL_SERVER_MYSQL_USERNAME" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_MYSQL_PASSWORD=$NETCENTRAL_SERVER_MYSQL_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
rem  echo "NETCENTRAL_SERVER_MYSQL_DBNAME=$NETCENTRAL_SERVER_MYSQL_DBNAME" | sudo tee -a /etc/environment >  /dev/null

pushd .

SET /P "NC_TR_APRSIS=Connect to APRS-IS (Y/n)? (Default: %NC_TR_APRSIS%): "
if %NC_TR_APRSIS% == "y" (
    SET NC_TR_APRSIS=Y
)
if %NC_TR_APRSIS% == "Y" (
    echo For more information about APRS-IS queries see https://www.aprs-is.net/javAPRSFilter.aspx .
    SET /P "NC_TR_APRSIS_QUERY=APRS-IS query to use? (Default: %NC_TR_APRSIS_QUERY%): "
    SET /P "NC_TR_APRSIS_PASSCODE=APRS-IS passcode?: "
    SET NETCENTRAL_TRANS_APRSIS_PASSCODE=$NC_TR_APRSIS_PASSCODE
    SET NETCENTRAL_TRANS_APRSIS_CALLSIGN=$NC_CALLSIGN
    SET NETCENTRAL_TRANS_APRSIS_QUERY=$NC_TR_APRSIS_QUERY
)


mkdir %NC_INSTALL_DIR%
mkdir %NC_INSTALL_DIR%\ui

echo Creating database and initial database user
SET BC_SQL_INIT="create schema %NC_DB_NAME%;create user '%NC_DB_USER%'@'localhost' IDENTIFIED BY '%NC_DB_PASS%'; grant all privileges on %NC_DB_NAME%.* to '%NC_DB_USER%'@'localhost';flush privileges;"
rem echo %NC_SQL_INIT% | sudo mysql -u root

mkdir %NC_TEMP_DIR%
mkdir %NC_TEMP_DIR%\logs
mkdir %NC_TEMP_DIR%\reports
mkdir %NC_TEMP_DIR%\src

SET NC_SRC_URL_ROOT=https://github.com/kc1vmz/netcentral/releases/download/v%NC_VERSION%
wget -q -O netcentral-server-%NC_VERSION%.jar %NC_SRC_URL_ROOT%\netcentral-server-%NC_VERSION%.jar
wget -q -O netcentral-ui.zip %NC_SRC_URL_ROOT%\netcentral-ui-%NC_VERSION%.zip
wget -q -O transceiver-aprsis-%NC_VERSION%.jar %NC_SRC_URL_ROOT%\transceiver-aprsis-%NC_VERSION%.jar
wget -q -O transceiver-kenwood-%NC_VERSION%.jar %NC_SRC_URL_ROOT%\transceiver-kenwood-%NC_VERSION%.jar
wget -q -O transceiver-kiss-%NC_VERSION%.jar %NC_SRC_URL_ROOT%\transceiver-kiss-%NC_VERSION%.jar
SET NC_SRC_URL_ROOT=

cd ui
tar -xf ..\netcentral-ui.zip
cd ..
del netcentral-ui.zip


cd ui
rem npm install

popd

SET NC_INSTALL_DIR=
SET NC_DB_NAME=
SET NC_DB_USER=
SET NC_DB_PASS=
SET NC_SVC_USER=
SET NC_SVC_PASS=
SET NC_DEVMODE=
SET NC_BUILD_SRC=
SET NC_TEMP_DIR=
SET NC_TR_APRSIS=
SET NC_TR_APRSIS_QUERY=
SET NC_VERSION=
SET NC_DB_CREATE=
SET NC_JAVA_INSTALL=
SET NC_INSTALL_SERVICES=
