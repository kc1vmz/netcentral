#!/bin/bash
# Determine OS name
NC_OS=$(uname)
if [ "$NC_OS" = "Linux" ]; then
  echo "Welcome to the Net Central installer for Linux"

  if [ -n "${NETCENTRAL_SERVER_TEMP_DIR+x}" ]; then
    echo A previous version of Net Central was detected. Please uninstall then reinstall.
    exit 1
  fi

  if [[ -f /etc/redhat-release ]]; then
    pkg_manager=yum
  elif [[ -f /etc/debian_version ]]; then
    pkg_manager=apt
  elif [[ -f /etc/os_version ]]; then
    pkg_manager=apt
  fi

  # default environment variables
  NC_INSTALL_DIR=~/netcentral
  NC_DB_NAME=netcentral
  NC_DB_USER=netcentral
  NC_DB_PASS=netcentral
  NC_SVC_USER=serviceAccount
  NC_SVC_PASS=serviceAccountPassword
  NC_DEVMODE=N
  NC_BUILD_SRC=N
  NC_TEMP_DIR=~/netcentral/tmp
  NC_TR_APRSIS=Y
  NC_TR_KENWOOD=N
  NC_TR_KENWOOD_PORT=/dev/rfcomm0
  NC_TR_KENWOOD_BAUD=9600
  NC_TR_KISS=N
  NC_TR_KISS_HOST=
  NC_TR_KISS_PORT=
  NC_TR_KISS_BAUD=
  NC_TR_KISS_CMD1=
  NC_TR_KISS_CMD2=
  NC_TR_APRSIS_QUERY=r/42.222/-71.57/500
  NC_VERSION=1.0.16
  NC_DB_CREATE=Y
  NC_JAVA_INSTALL=Y
  NC_INSTALL_SERVICES=Y
  NC_START_SERVICES=N
  NC_TR_KISS_DIGI_PATH=WIDE2-1,WIDE1-1

  read -e -i $NC_VERSION -p "What version of Net Central?: " NC_VERSION
  read -e -i $NC_INSTALL_DIR -p "Where should Net Central be installed?: " NC_INSTALL_DIR
  read -e -i $NC_TEMP_DIR -p "Where should Net Central temp space be located?: " NC_TEMP_DIR
  read -e -i $NC_BUILD_SRC -p "Do you want to build from source (y/N)?: " NC_BUILD_SRC
  read -e -i $NC_JAVA_INSTALL -p "Do you want Java 21 installed (Y/n)?: " NC_JAVA_INSTALL
  read -e -i $NC_DEVMODE -p "Do you want developer tools (y/N)?: " NC_DEVMODE
  read -e -i $NC_DB_CREATE -p "Do you want to deploy MariaDB locally (Y/n)?: " NC_DB_CREATE
  read -e -i $NC_DB_NAME -p "Net Central database name?: " NC_DB_NAME
  read -e -i $NC_DB_USER -p "Net Central database username?: " NC_DB_USER
  read -e -s -p "Net Central database password?: " NC_DB_PASS
  read -e -i $NC_SVC_USER -p "Net Central service account username?: " NC_SVC_USER
  read -e -s -p "Net Central service account password?: " NC_SVC_PASS
  read -n 10 -p "What is your callsign?: " NC_CALLSIGN
  read -e -i $NC_INSTALL_SERVICES -p "Do you want Net Central to be configured as services and started at boot time (Y/n)?: " NC_INSTALL_SERVICES

  if [[ "$NC_INSTALL_SERVICES" =~ ^[Yy]$ ]]; then
    NC_START_SERVICES=Y
    read -e -i $NC_START_SERVICES -p "Do you want Net Central to start these services after installation (Y/n)?: " NC_START_SERVICES
  fi

  NC_SRC_URL=https://github.com/kc1vmz/netcentral/archive/refs/tags/v$NC_VERSION.zip

  NETCENTRAL_APRS_CALLSIGN=$NC_CALLSIGN
  NETCENTRAL_SERVER_TEMP_DIR=$NC_TEMP_DIR
  NETCENTRAL_SERVER_USERNAME=$NC_SVC_USER
  NETCENTRAL_SERVER_PASSWORD=$NC_SVC_PASS
  NETCENTRAL_SERVER_MYSQL_USERNAME=$NC_DB_USER
  NETCENTRAL_SERVER_MYSQL_PASSWORD=$NC_DB_PASS
  NETCENTRAL_SERVER_MYSQL_DBNAME=$NC_DB_NAME

  sudo cp /etc/environment /etc/environment.pre_net_central_install

  echo "NETCENTRAL_INSTALL_DIR=$NC_INSTALL_DIR" | sudo tee -a /etc/environment >  /dev/null

  if [[ "$NC_INSTALL_SERVICES" =~ ^[Nn]$ ]]; then
    echo "NETCENTRAL_APRS_CALLSIGN=$NETCENTRAL_APRS_CALLSIGN" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_TEMP_DIR=$NETCENTRAL_SERVER_TEMP_DIR" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_USERNAME=$NETCENTRAL_SERVER_USERNAME" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_PASSWORD=$NETCENTRAL_SERVER_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_MYSQL_USERNAME=$NETCENTRAL_SERVER_MYSQL_USERNAME" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_MYSQL_PASSWORD=$NETCENTRAL_SERVER_MYSQL_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_SERVER_MYSQL_DBNAME=$NETCENTRAL_SERVER_MYSQL_DBNAME" | sudo tee -a /etc/environment >  /dev/null
  fi

    read -e -i $NC_TR_APRSIS -p "Connect to APRS-IS (Y/n)?: " NC_TR_APRSIS
    if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
      echo For more information about APRS-IS queries see https://www.aprs-is.net/javAPRSFilter.aspx .
      read -e -i $NC_TR_APRSIS_QUERY -p "APRS-IS query to use?: " NC_TR_APRSIS_QUERY
      read -s -p "APRS-IS passcode?: " NC_TR_APRSIS_PASSCODE
      NETCENTRAL_TRANS_APRSIS_PASSCODE=$NC_TR_APRSIS_PASSCODE
      NETCENTRAL_TRANS_APRSIS_CALLSIGN=$NC_CALLSIGN
      NETCENTRAL_TRANS_APRSIS_QUERY=$NC_TR_APRSIS_QUERY
      if [[ "$NC_INSTALL_SERVICES" =~ ^[Nn]$ ]]; then
        echo "NETCENTRAL_TRANS_APRSIS_PASSCODE=$NETCENTRAL_TRANS_APRSIS_PASSCODE" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_APRSIS_CALLSIGN=$NETCENTRAL_TRANS_APRSIS_CALLSIGN" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_APRSIS_QUERY=$NETCENTRAL_TRANS_APRSIS_QUERY" | sudo tee -a /etc/environment >  /dev/null
      fi
    fi

    read -e -i $NC_TR_KENWOOD -p "Connect to native Kenwood (y/N)?: " NC_TR_KENWOOD
    if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
      read -e -p "Serial port to use?: " NC_TR_KENWOOD_PORT
      read -e -p "Baud rate?: " NC_TR_KENWOOD_BAUD
      NETCENTRAL_TRANS_KENWOOD_TNC_PORT=$NC_TR_KENWOOD_PORT
      NETCENTRAL_TRANS_KENWOOD_TNC_BAUD=$NC_TR_KENWOOD_BAUD
      if [[ "$NC_INSTALL_SERVICES" =~ ^[Nn]$ ]]; then
        echo "NETCENTRAL_TRANS_KENWOOD_TNC_PORT=$NETCENTRAL_TRANS_KENWOOD_TNC_PORT" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KENWOOD_TNC_BAUD=$NETCENTRAL_TRANS_KENWOOD_TNC_BAUD" | sudo tee -a /etc/environment >  /dev/null
      fi
    fi

    read -e -i $NC_TR_KISS -p "Connect to KISS-enabled radio (y/N)?: " NC_TR_KISS
    if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
      read -e -p "Hostname (if using TCP/IP port)?: " NC_TR_KISS_HOST
      read -e -p "Port to use?: " NC_TR_KISS_PORT
      read -e -p "Baud rate (if using serial port)?: " NC_TR_KISS_BAUD
      read -e -p "Init command 1 (optional)?: " NC_TR_KISS_CMD1
      read -e -p "Init command 2 (optional)?: " NC_TR_KISS_CMD2
      read -e -i $NC_TR_KISS_DIGI_PATH -p "Digipeater path?: " NC_TR_KISS_DIGI_PATH
      NETCENTRAL_TRANS_KISS_TNC_HOST=$NC_TR_KISS_HOST
      NETCENTRAL_TRANS_KISS_TNC_PORT=$NC_TR_KISS_PORT
      NETCENTRAL_TRANS_KISS_TNC_BAUD=$NC_TR_KISS_BAUD
      NETCENTRAL_TRANS_KISS_TNC_INIT1=$NC_TR_KISS_CMD1
      NETCENTRAL_TRANS_KISS_TNC_INIT2=$NC_TR_KISS_CMD2
      NETCENTRAL_TRANS_KISS_DIGI_PATH=$NC_TR_KISS_DIGI_PATH
      if [[ "$NC_INSTALL_SERVICES" =~ ^[Nn]$ ]]; then
        echo "NETCENTRAL_TRANS_KISS_TNC_HOST=$NETCENTRAL_TRANS_KISS_TNC_HOST" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KISS_TNC_PORT=$NETCENTRAL_TRANS_KISS_TNC_PORT" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KISS_TNC_BAUD=$NETCENTRAL_TRANS_KISS_TNC_BAUD" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KISS_TNC_INIT1=$NETCENTRAL_TRANS_KISS_TNC_INIT1" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KISS_TNC_INIT2=$NETCENTRAL_TRANS_KISS_TNC_INIT2" | sudo tee -a /etc/environment >  /dev/null
        echo "NETCENTRAL_TRANS_KISS_DIGI_PATH=$NETCENTRAL_TRANS_KISS_DIGI_PATH" | sudo tee -a /etc/environment >  /dev/null
      fi
    fi

    mkdir $NC_INSTALL_DIR

    if [ $pkg_manager = "yum" ]; then

      echo Updating system packages
      sudo yum update -y
      sudo yum install unzip -y

      if [[ "$NC_JAVA_INSTALL" =~ ^[Yy]$ ]]; then
        echo Installing Java 21
        sudo yum install java-21-openjdk-devel -y
      fi

      JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')
      if [ "$JAVA_VER" -ge 17 ]; then
        # fall through
        echo Java 17+ verified
      else
        echo Java 17 or greater not installed - exiting.
        sudo cp /etc/environment.pre_net_central_install /etc/environment 
        sudo rm /etc/environment.pre_net_central_install
        exit 1
      fi

      if [[ "$NC_DB_CREATE" =~ ^[Yy]$ ]]; then
        echo Installing MySQL / MariaDB
        sudo yum install mariadb-server -y

        sudo systemctl start mariadb
        sudo systemctl enable --now mariadb

        echo Creating database and initial database user
        NC_SQL_INIT="create schema $NC_DB_NAME;create user '$NC_DB_USER'@'localhost' IDENTIFIED BY '$NC_DB_PASS'; grant all privileges on $NC_DB_NAME.* to '$NC_DB_USER'@'localhost';flush privileges;"
        echo $NC_SQL_INIT | sudo mysql -u root
      fi

      echo Installing NPM
      sudo yum install npm -y
      sudo yum install libcap -y
      NC_NODE_LOCATION=$(readlink -f `which node`)
      sudo setcap cap_net_bind_service=+ep $NC_NODE_LOCATION

      if [[ "$NC_DEVMODE" =~ ^[Yy]$ ]]; then
        echo Installing Git client
        sudo yum install git -y
        # echo Installing Microsoft Visual Studio Code
        # sudo yum install code -y
        # just an RPM - need to figure that out
      fi

      sudo mkdir $NC_TEMP_DIR
      sudo chown $USER $NC_TEMP_DIR
      mkdir $NC_TEMP_DIR/logs
      mkdir $NC_TEMP_DIR/reports
      mkdir $NC_TEMP_DIR/src

      if [[ "$NC_BUILD_SRC" =~ ^[Yy]$ ]]; then
        echo Installing Maven client
        sudo yum install maven -y
        echo Retrieving and copying Net Central source
        pushd .
        cd $NC_TEMP_DIR/src
        wget -q -O netcentral.zip $NC_SRC_URL
        unzip -d $NC_INSTALL_DIR netcentral.zip
        rm netcentral.zip

        echo Building Net Central from source
        cd $NC_INSTALL_DIR/netcentral-$NC_VERSION
        mvn -U clean install
        echo Perform npm install
        cd ui/net-central-ui
        npm install

        # copy all the built jars up
        cd $NC_INSTALL_DIR
        cp ./netcentral-$NC_VERSION/transceivers/transceiver-kenwood/target/transceiver-kenwood-1.0.16.jar .
        cp ./netcentral-$NC_VERSION/transceivers/transceiver-aprsis/target/transceiver-aprsis-1.0.16.jar .
        cp ./netcentral-$NC_VERSION/transceivers/transceiver-kiss/target/transceiver-kiss-1.0.16.jar .
        cp ./netcentral-$NC_VERSION/netcentral-server/target/netcentral-server-1.0.16.jar .

        popd
      else
        echo Retrieving Net Central binaries
        pushd .
        cd $NC_INSTALL_DIR
        mkdir ui

        NC_SRC_URL_ROOT=https://github.com/kc1vmz/netcentral/releases/download/v$NC_VERSION
        wget -q -O netcentral-server-$NC_VERSION.jar $NC_SRC_URL_ROOT/netcentral-server-$NC_VERSION.jar
        wget -q -O netcentral-ui.zip $NC_SRC_URL_ROOT/netcentral-ui-$NC_VERSION.zip
        wget -q -O transceiver-aprsis-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-aprsis-$NC_VERSION.jar
        wget -q -O transceiver-kenwood-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-kenwood-$NC_VERSION.jar
        wget -q -O transceiver-kiss-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-kiss-$NC_VERSION.jar
        NC_SRC_URL_ROOT=

        chmod +x netcentral-server-$NC_VERSION.jar
        chmod +x transceiver-aprsis-$NC_VERSION.jar
        chmod +x transceiver-kenwood-$NC_VERSION.jar
        chmod +x transceiver-kiss-$NC_VERSION.jar

        unzip -d $NC_INSTALL_DIR/ui netcentral-ui.zip
        rm netcentral-ui.zip

        cd ui
        npm install

        if [[ "$NC_INSTALL_SERVICES" =~ ^[Yy]$ ]]; then
          echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Description=Net Central Server' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/netcentral-server-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null

          if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Description=Net Central APRS-IS Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-aprsis-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_PASSCODE='$NETCENTRAL_TRANS_APRSIS_PASSCODE | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_CALLSIGN='$NETCENTRAL_TRANS_APRSIS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_QUERY='$NETCENTRAL_TRANS_APRSIS_QUERY | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
          fi

          if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Description=Net Central Kenwood Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-kenwood-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KENWOOD_TNC_PORT='$NETCENTRAL_TRANS_KENWOOD_TNC_PORT | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KENWOOD_TNC_BAUD='$NETCENTRAL_TRANS_KENWOOD_TNC_BAUD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
          fi

          if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Description=Net Central KISS Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-kiss-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_HOST='$NETCENTRAL_TRANS_KISS_TNC_HOST | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_PORT='$NETCENTRAL_TRANS_KISS_TNC_PORT | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_BAUD='$NETCENTRAL_TRANS_KISS_TNC_BAUD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_INIT1='$NETCENTRAL_TRANS_KISS_TNC_INIT1 | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_INIT2='$NETCENTRAL_TRANS_KISS_TNC_INIT2 | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_DIGI_PATH='$NETCENTRAL_TRANS_KISS_DIGI_PATH | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
          fi

          echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Description=Net Central Web Server UI' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'WorkingDirectory='$NC_INSTALL_DIR/ui | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'ExecStart='$NC_INSTALL_DIR'/ui/node_modules/.bin/vite' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null

          sudo systemctl daemon-reload
          sudo systemctl enable netcentral-server
          if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-aprsis
          fi
          if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-kenwood
          fi
          if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-kiss
          fi
          sudo systemctl enable netcentral-ui

          if [[ "$NC_START_SERVICES" =~ ^[Yy]$ ]]; then
            sudo systemctl start netcentral-server
            sleep 10
            if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-aprsis
            fi
            if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-kenwood
            fi
            if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-kiss
            fi
            sudo systemctl start netcentral-ui
          fi
        fi

        popd
    fi

  elif [ $pkg_manager = "apt" ]; then
    echo Updating system packages
    sudo apt update -y
    sudo apt install unzip -y

    if [[ "$NC_JAVA_INSTALL" =~ ^[Yy]$ ]]; then
      echo Installing Java 21
      sudo apt install default-jdk -y
    fi

    JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{sub("^$", "0", $2); print $1$2}')
    if [ "$JAVA_VER" -ge 17 ]; then
      # fall through
      echo Java 17+ verified
    else
      echo Java 17 or greater not installed - exiting.
      sudo cp /etc/environment.pre_net_central_install /etc/environment 
      sudo rm /etc/environment.pre_net_central_install
      exit 1
    fi

    if [[ "$NC_DB_CREATE" =~ ^[Yy]$ ]]; then
      echo Installing MySQL / MariaDB
      sudo apt install mariadb-server -y

      echo Creating database and initial database user
      NC_SQL_INIT="create schema $NC_DB_NAME;create user '$NC_DB_USER'@'localhost' IDENTIFIED BY '$NC_DB_PASS'; grant all privileges on $NC_DB_NAME.* to '$NC_DB_USER'@'localhost';flush privileges;"
      echo $NC_SQL_INIT | sudo mysql -u root
    fi

    echo Installing NPM
    sudo apt install npm -y
    sudo apt-get install libcap2-bin
    NC_NODE_LOCATION=$(readlink -f `which node`)
    sudo setcap cap_net_bind_service=+ep $NC_NODE_LOCATION

    if [[ "$NC_DEVMODE" =~ ^[Yy]$ ]]; then
      echo Installing Git client
      sudo apt install git -y
      echo Installing Microsoft Visual Studio Code
      sudo apt install code -y
    fi

    sudo mkdir $NC_TEMP_DIR
    sudo chown $USER $NC_TEMP_DIR
    mkdir $NC_TEMP_DIR/logs
    mkdir $NC_TEMP_DIR/reports
    mkdir $NC_TEMP_DIR/src

    if [[ "$NC_BUILD_SRC" =~ ^[Yy]$ ]]; then
      echo Installing Maven client
      sudo apt install maven -y
      echo Retrieving and copying Net Central source
      pushd .
      cd $NC_TEMP_DIR/src
      wget -q -O netcentral.zip $NC_SRC_URL
      unzip -d $NC_INSTALL_DIR netcentral.zip
      rm netcentral.zip

      echo Building Net Central from source
      cd $NC_INSTALL_DIR/netcentral-$NC_VERSION
      mvn -U clean install
      echo Perform npm install
      cd ui/net-central-ui
      npm install

      # copy all the built jars up
      cd $NC_INSTALL_DIR
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kenwood/target/transceiver-kenwood-1.0.16.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-aprsis/target/transceiver-aprsis-1.0.16.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kiss/target/transceiver-kiss-1.0.16.jar .
      cp ./netcentral-$NC_VERSION/netcentral-server/target/netcentral-server-1.0.16.jar .

      popd
    else
      echo Retrieving Net Central binaries
      pushd .
      cd $NC_INSTALL_DIR
      mkdir ui

      NC_SRC_URL_ROOT=https://github.com/kc1vmz/netcentral/releases/download/v$NC_VERSION
      wget -q -O netcentral-server-$NC_VERSION.jar $NC_SRC_URL_ROOT/netcentral-server-$NC_VERSION.jar
      wget -q -O netcentral-ui.zip $NC_SRC_URL_ROOT/netcentral-ui-$NC_VERSION.zip
      wget -q -O transceiver-aprsis-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-aprsis-$NC_VERSION.jar
      wget -q -O transceiver-kenwood-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-kenwood-$NC_VERSION.jar
      wget -q -O transceiver-kiss-$NC_VERSION.jar $NC_SRC_URL_ROOT/transceiver-kiss-$NC_VERSION.jar
      NC_SRC_URL_ROOT=

      chmod +x netcentral-server-$NC_VERSION.jar
      chmod +x transceiver-aprsis-$NC_VERSION.jar
      chmod +x transceiver-kenwood-$NC_VERSION.jar
      chmod +x transceiver-kiss-$NC_VERSION.jar

      unzip -d $NC_INSTALL_DIR/ui netcentral-ui.zip
      rm netcentral-ui.zip

      cd ui
      npm install

      if [[ "$NC_INSTALL_SERVICES" =~ ^[Yy]$ ]]; then
          echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Description=Net Central Server' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/netcentral-server-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null
          echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-server.service >  /dev/null

          if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Description=Net APRS-IS Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-aprsis-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_PASSCODE='$NETCENTRAL_TRANS_APRSIS_PASSCODE | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_CALLSIGN='$NETCENTRAL_TRANS_APRSIS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_APRSIS_QUERY='$NETCENTRAL_TRANS_APRSIS_QUERY | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-aprsis.service >  /dev/null
          fi

          if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Description=Net Central Kenwood Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-kenwood-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KENWOOD_TNC_PORT='$NETCENTRAL_TRANS_KENWOOD_TNC_PORT | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KENWOOD_TNC_BAUD='$NETCENTRAL_TRANS_KENWOOD_TNC_BAUD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kenwood.service >  /dev/null
          fi

          if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
            echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Description=Net Central KISS Transceiver' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'WorkingDirectory='$NC_INSTALL_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'SuccessExitStatus=143' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'ExecStart=java -jar '$NC_INSTALL_DIR'/transceiver-kiss-'$NC_VERSION'.jar' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_APRS_CALLSIGN='$NETCENTRAL_APRS_CALLSIGN | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_HOST='$NETCENTRAL_TRANS_KISS_TNC_HOST | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_PORT='$NETCENTRAL_TRANS_KISS_TNC_PORT | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_BAUD='$NETCENTRAL_TRANS_KISS_TNC_BAUD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_INIT1='$NETCENTRAL_TRANS_KISS_TNC_INIT1 | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_TNC_INIT2='$NETCENTRAL_TRANS_KISS_TNC_INIT2 | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_TRANS_KISS_DIGI_PATH='$NETCENTRAL_TRANS_KISS_DIGI_PATH | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_TEMP_DIR='$NETCENTRAL_SERVER_TEMP_DIR | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_USERNAME='$NETCENTRAL_SERVER_USERNAME | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'Environment=NETCENTRAL_SERVER_PASSWORD='$NETCENTRAL_SERVER_PASSWORD | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
            echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-transceiver-kiss.service >  /dev/null
          fi

          echo '[Unit]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Description=Net Central Web Server UI' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo '[Service]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'User='$LOGNAME | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'WorkingDirectory='$NC_INSTALL_DIR/ui | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Type=simple' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'Restart=always' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'RemainAfterExit=yes' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'ExecStart='$NC_INSTALL_DIR'/ui/node_modules/.bin/vite' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo '[Install]' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null
          echo 'WantedBy=multi-user.target' | sudo tee -a /etc/systemd/system/netcentral-ui.service >  /dev/null

          sudo systemctl daemon-reload
          sudo systemctl enable netcentral-server
          if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-aprsis
          fi
          if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-kenwood
          fi
          if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
            sudo systemctl enable netcentral-transceiver-kiss
          fi
          sudo systemctl enable netcentral-ui

          if [[ "$NC_START_SERVICES" =~ ^[Yy]$ ]]; then
            sudo systemctl start netcentral-server
            sleep 10
            if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-aprsis
            fi
            if [[ "$NC_TR_KENWOOD" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-kenwood
            fi
            if [[ "$NC_TR_KISS" =~ ^[Yy]$ ]]; then
              sudo systemctl start netcentral-transceiver-kiss
            fi
            sudo systemctl start netcentral-ui
          fi
      fi
      popd
    fi
  fi

  if [[ "$NC_INSTALL_SERVICES" =~ ^[Nn]$ ]]; then
    echo Environment variables have been added to /etc/environment.  Make sure to reboot or run 'source /etc/environment' to add them into any new terminal.
  fi

  if [[ "$NC_DB_CREATE" =~ ^[Nn]$ ]]; then
    echo Be sure to run the following commands against the MariaSB/MySQL server you are using:
    NC_SQL_INIT="create schema $NC_DB_NAME;create user '$NC_DB_USER'@'localhost' IDENTIFIED BY '$NC_DB_PASS'; grant all privileges on $NC_DB_NAME.* to '$NC_DB_USER'@'localhost';flush privileges;"
    echo $NC_SQL_INIT
    echo Also add environment variable values for NETCENTRAL_SERVER_MYSQL_HOST and NETCENTRAL_SERVER_MYSQL_PORT and add to /etc/environment file.
  fi

  if [[ "$NC_INSTALL_SERVICES" =~ ^[Yy]$ ]]; then
    echo Net Central services running as background services.
  else
    echo You can run each jar with java -jar command in the $NC_INSTALL_DIR directory, and start the UI running with "npm run dev" from the $NC_INSTALL_DIR/ui directory
  fi

  # cleanup environment
  NC_INSTALL_DIR=
  NC_DB_NAME=
  NC_DB_USER=
  NC_DB_PASS=
  NC_SVC_USER=
  NC_SVC_PASS=
  NC_DEVMODE=
  NC_TEMP_DIR=
  NC_TR_APRSIS=
  NC_TR_APRSIS_QUERY=
  NC_TR_APRSIS_PASSCODE=
  NC_NODE_LOCATION=
  NC_VERSION=
  NC_DB_CREATE=
  NC_JAVA_INSTALL=
  NC_INSTALL_SERVICES=
  NC_START_SERVICES=
  NC_TR_KENWOOD=
  NC_TR_KENWOOD_PORT=
  NC_TR_KENWOOD_BAUD=
  NC_TR_KISS=
  NC_TR_KISS_HOST=
  NC_TR_KISS_PORT=
  NC_TR_KISS_BAUD=
  NC_TR_KISS_CMD1=
  NC_TR_KISS_CMD2=
  NC_TR_KISS_DIGI_PATH=

elif [ "$NC_OS" = "Darwin" ]; then

  echo "This is a Mac Machine - not yet supported"
  exit 1
else
  echo "Unsupported OS -" $NC_OS
  exit 1
fi

echo "Net Central installation complete."