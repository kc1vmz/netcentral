@echo off
echo Fetching version %1 as specified on command line
echo Need curl to proceed.
pause
echo Creating temporary directory structure
mkdir tmp
mkdir tmp\netcentral-server\target
mkdir tmp\transceivers\transceiver-aprsis\target
mkdir tmp\transceivers\transceiver-agw\target
mkdir tmp\transceivers\transceiver-kenwood\target
mkdir tmp\transceivers\transceiver-kiss\target
mkdir tmp\ui\net-central-ui\target
mkdir tmp\docker\full\image_files

echo  Fetching files from github

set NC_SRC_URL_ROOT=https://github.com/kc1vmz/netcentral/releases/download/v%1

curl -L -o tmp\netcentral-server\target\netcentral-server-%1.jar %NC_SRC_URL_ROOT%/netcentral-server-%1.jar
curl -L -o tmp\ui\net-central-ui\target\netcentral-ui-%1.zip %NC_SRC_URL_ROOT%/netcentral-ui-%1.zip
curl -L -o tmp\transceivers\transceiver-agw\target\transceiver-agw-%1.jar %NC_SRC_URL_ROOT%/transceiver-agw-%1.jar
curl -L -o tmp\transceivers\transceiver-aprsis\target\transceiver-aprsis-%1.jar %NC_SRC_URL_ROOT%/transceiver-aprsis-%1.jar
curl -L -o tmp\transceivers\transceiver-kenwood\target\transceiver-kenwood-%1.jar %NC_SRC_URL_ROOT%/transceiver-kenwood-%1.jar
curl -L -o tmp\transceivers\transceiver-kiss\target\transceiver-kiss-%1.jar %NC_SRC_URL_ROOT%/transceiver-kiss-%1.jar
set NC_SRC_URL_ROOT=

copy image_files\*.* tmp\docker\full\image_files

docker build --no-cache -t netcentral-server:1.0.6 -f Dockerfile-server ../..
docker build --no-cache -t netcentral-ui:1.0.6 -f Dockerfile-ui ../..
docker build --no-cache -t netcentral-transceiver-aprsis:1.0.6 -f Dockerfile-aprsis ../..
docker build --no-cache -t netcentral-transceiver-kenwood:1.0.6 -f Dockerfile-kenwood ../..
docker build --no-cache -t netcentral-transceiver-kiss:1.0.6 -f Dockerfile-kiss ../..
