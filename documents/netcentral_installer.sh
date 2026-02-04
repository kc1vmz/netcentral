#!/bin/bash
# Determine OS name
NC_OS=$(uname)
if [ "$NC_OS" = "Linux" ]; then
  echo "Welcome to the Net Central installer for Linux"

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
  NC_TEMP_DIR=/tmp/netcentral
  NC_TR_APRSIS=Y
  NC_TR_APRSIS_QUERY=r/42.222/-71.57/500
  NC_VERSION=1.0.10
  NC_DB_CREATE=Y
  NC_JAVA_INSTALL=Y

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
  read -n 10 -p "Callsign?: " NC_CALLSIGN

  NC_SRC_URL=https://github.com/kc1vmz/netcentral/archive/refs/tags/v$NC_VERSION.zip

  NETCENTRAL_APRS_CALLSIGN=$NC_CALLSIGN
  NETCENTRAL_SERVER_TEMP_DIR=$NC_TEMP_DIR
  NETCENTRAL_SERVER_USERNAME=$NC_SVC_USER
  NETCENTRAL_SERVER_PASSWORD=$NC_SVC_PASS
  NETCENTRAL_SERVER_MYSQL_USERNAME=$NC_DB_USER
  NETCENTRAL_SERVER_MYSQL_PASSWORD=$NC_DB_PASS
  NETCENTRAL_SERVER_MYSQL_DBNAME=$NC_DB_NAME

  echo "NETCENTRAL_APRS_CALLSIGN=$NETCENTRAL_APRS_CALLSIGN" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_TEMP_DIR=$NETCENTRAL_SERVER_TEMP_DIR" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_USERNAME=$NETCENTRAL_SERVER_USERNAME" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_PASSWORD=$NETCENTRAL_SERVER_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_MYSQL_USERNAME=$NETCENTRAL_SERVER_MYSQL_USERNAME" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_MYSQL_PASSWORD=$NETCENTRAL_SERVER_MYSQL_PASSWORD" | sudo tee -a /etc/environment >  /dev/null
  echo "NETCENTRAL_SERVER_MYSQL_DBNAME=$NETCENTRAL_SERVER_MYSQL_DBNAME" | sudo tee -a /etc/environment >  /dev/null

  read -e -i $NC_TR_APRSIS -p "Connect to APRS-IS (Y/n)?: " NC_TR_APRSIS
  if [[ "$NC_TR_APRSIS" =~ ^[Yy]$ ]]; then
    echo For more information about APRS-IS queries see https://www.aprs-is.net/javAPRSFilter.aspx .
    read -e -i $NC_TR_APRSIS_QUERY -p "APRS-IS query to use?: " NC_TR_APRSIS_QUERY
    read -s -p "APRS-IS passcode?: " NC_TR_APRSIS_PASSCODE
    NETCENTRAL_TRANS_APRSIS_PASSCODE=$NC_TR_APRSIS_PASSCODE
    NETCENTRAL_TRANS_APRSIS_CALLSIGN=$NC_CALLSIGN
    NETCENTRAL_TRANS_APRSIS_QUERY=$NC_TR_APRSIS_QUERY
    echo "NETCENTRAL_TRANS_APRSIS_PASSCODE=$NETCENTRAL_TRANS_APRSIS_PASSCODE" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_TRANS_APRSIS_CALLSIGN=$NETCENTRAL_TRANS_APRSIS_CALLSIGN" | sudo tee -a /etc/environment >  /dev/null
    echo "NETCENTRAL_TRANS_APRSIS_QUERY=$NETCENTRAL_TRANS_APRSIS_QUERY" | sudo tee -a /etc/environment >  /dev/null
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
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kenwood/target/transceiver-kenwood-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-aprsis/target/transceiver-aprsis-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kiss/target/transceiver-kiss-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/netcentral-server/target/netcentral-server-1.0.10.jar .

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

      chmod +x netcentral-server-$NC_VERSION.jar
      chmod +x transceiver-aprsis-$NC_VERSION.jar
      chmod +x transceiver-kenwood-$NC_VERSION.jar
      chmod +x transceiver-kiss-$NC_VERSION.jar
      unzip -d $NC_INSTALL_DIR/ui netcentral-ui.zip
      rm netcentral-ui.zip

      NC_SRC_URL_ROOT=

      cd ui
      npm install

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
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kenwood/target/transceiver-kenwood-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-aprsis/target/transceiver-aprsis-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/transceivers/transceiver-kiss/target/transceiver-kiss-1.0.10.jar .
      cp ./netcentral-$NC_VERSION/netcentral-server/target/netcentral-server-1.0.10.jar .

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

      chmod +x netcentral-server-$NC_VERSION.jar
      chmod +x transceiver-aprsis-$NC_VERSION.jar
      chmod +x transceiver-kenwood-$NC_VERSION.jar
      chmod +x transceiver-kiss-$NC_VERSION.jar
      unzip -d $NC_INSTALL_DIR/ui netcentral-ui.zip
      rm netcentral-ui.zip

      NC_SRC_URL_ROOT=

      cd ui
      npm install

      popd
    fi
  fi

  echo Environment variables have been added to /etc/environment.  Make sure to reboot or run 'source /etc/environment' to add them into any new terminal.

  if [[ "$NC_DB_CREATE" =~ ^[Nn]$ ]]; then
    echo Be sure to run the following commands against the MariaSB/MySQL server you are using:
    NC_SQL_INIT="create schema $NC_DB_NAME;create user '$NC_DB_USER'@'localhost' IDENTIFIED BY '$NC_DB_PASS'; grant all privileges on $NC_DB_NAME.* to '$NC_DB_USER'@'localhost';flush privileges;"
    echo $NC_SQL_INIT
    echo Also add environment variable values for NETCENTRAL_SERVER_MYSQL_HOST and NETCENTRAL_SERVER_MYSQL_PORT and add to /etc/environment file.
  fi

  echo You can run each jar with java -jar command, and start the UI running "npm run dev" from the  $NC_INSTALL_DIR/ui directory

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

elif [ "$NC_OS" = "Darwin" ]; then

  echo "This is a Mac Machine - not yet supported"
  exit 1
else
  echo "Unsupported OS -" $NC_OS
  exit 1
fi

echo "Net Central installation complete."