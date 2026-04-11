#!/bin/bash
# Determine OS name
NC_OS=$(uname)
if [ "$NC_OS" = "Linux" ]; then
  echo "Welcome to the Net Central UNinstaller for Linux"

  # default environment variables
  NC_CONFIRM_UNINSTALL=Y

  read -e -i $NC_CONFIRM_UNINSTALL -p "Do you want to uninstall Net Central (Y/n)?: " NC_CONFIRM_UNINSTALL

  if [[ "$NC_CONFIRM_UNINSTALL" =~ ^[Yy]$ ]]; then
    # fall through
    echo Starting uninstall of Net Central
  else
    #any other answer - exit
    exit 1
  fi

  echo Backing up /etc/environment
  sudo cp /etc/environment /etc/environment.pre_net_central_uninstall

  echo Removing environment variables from /etc/environment
  sudo sed -i '/NETCENTRAL_APRS_CALLSIGN/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_TEMP_DIR/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_USERNAME/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_PASSWORD/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_MYSQL_USERNAME/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_MYSQL_PASSWORD/d' /etc/environment
  sudo sed -i '/NETCENTRAL_SERVER_MYSQL_DBNAME/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_APRSIS_PASSCODE/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_APRSIS_CALLSIGN/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_APRSIS_QUERY/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KENWOOD_TNC_BAUD/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KISS_TNC_HOST/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KISS_TNC_PORT/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KISS_TNC_BAUD/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KISS_TNC_INIT1/d' /etc/environment
  sudo sed -i '/NETCENTRAL_TRANS_KISS_TNC_INIT2/d' /etc/environment
  sudo sed -i '/NETCENTRAL_INSTALL_DIR/d' /etc/environment

  echo Removing services
  sudo systemctl stop netcentral-ui
  sudo systemctl stop netcentral-transceiver-aprsis
  sudo systemctl stop netcentral-transceiver-kenwood
  sudo systemctl stop netcentral-transceiver-kiss
  sudo systemctl stop netcentral-server

  sudo systemctl disable netcentral-server
  sudo systemctl disable netcentral-transceiver-aprsis
  sudo systemctl disable netcentral-transceiver-kenwood
  sudo systemctl disable netcentral-transceiver-kiss
  sudo systemctl disable netcentral-ui

  sudo rm /etc/systemd/system/netcentral-server.service
  sudo rm /etc/systemd/system/netcentral-transceiver-aprsis.service
  sudo rm /etc/systemd/system/netcentral-transceiver-kenwood.service
  sudo rm /etc/systemd/system/netcentral-transceiver-kiss.service
  sudo rm /etc/systemd/system/netcentral-ui.service

  sudo systemctl daemon-reload

  if [ -n "${NETCENTRAL_INSTALL_DIR+x}" ]; then
    echo Deleting Net Central server and transceiver files
    sudo rm transceiver*.jar
    sudo rm netcentral-server*.jar
    echo Not deleting ${NETCENTRAL_INSTALL_DIR} - delete independently.
  else
    echo Net Central installation directory unknown- delete independently.
  fi

  if [ -n "${NETCENTRAL_SERVER_TEMP_DIR+x}" ]; then
    echo Deleting Net Central temporary sub-directories
    sudo rm ${NETCENTRAL_SERVER_TEMP_DIR}/logs
    sudo rm ${NETCENTRAL_SERVER_TEMP_DIR}/reports
    sudo rm ${NETCENTRAL_SERVER_TEMP_DIR}/src
    echo Not deleting ${NETCENTRAL_SERVER_TEMP_DIR} - delete independently.
  else
    echo Net Central temporary directory unknown - delete independently.
  fi

  echo Not uninstalling MariaDB - uninstall independently.
  echo Not uninstalling Java - uninstall independently.
  echo Not uninstalling Node.JS - uninstall independently.
  echo Not uninstalling Git client - uninstall independently.
  echo Not uninstalling Maven client - uninstall independently.

  NC_CONFIRM_UNINSTALL=

elif [ "$NC_OS" = "Darwin" ]; then

  echo "This is a Mac Machine - not yet supported"
  exit 1
else
  echo "Unsupported OS -" $NC_OS
  exit 1
fi

echo "Net Central uninstallation complete."