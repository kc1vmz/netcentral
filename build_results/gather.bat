@echo off
rd /s/q target
mkdir target
echo Gathering files for upload to Github

copy ..\installers\docker\target\netcentral-docker-image-build-scripts*.zip target
copy ..\netcentral-server\target\netcentral-server*.jar target
copy ..\transceivers\transceiver-agw\target\transceiver-agw*.jar target
copy ..\transceivers\transceiver-aprsis\target\transceiver-aprsis*.jar target
copy ..\transceivers\transceiver-kenwood\target\transceiver-kenwood*.jar target
copy ..\transceivers\transceiver-kiss\target\transceiver-kiss*.jar target
copy ..\transceivers\transceiver-test\target\transceiver-test*.jar target
copy ..\ui\net-central-ui\target\netcentral-ui*.zip target
copy ..\documents\target\netcentral-documents*.zip target
copy ..\installers\linux\netcentral_installer.sh target
