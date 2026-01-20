# Running Net Central in a container

## Overview

Net Central can be run in a containerized environment, such as Docker Desktop.

For the "full" version, you can run a single container containing all of the software, including the database.  This is the simplest version to use, but it will be constrained by database size.
For the "split" version, each microservice is contained in its own container. The MySQL / MariaDB database is not provided, but it you're using containers, you're smarter than the average bear. Deploy it in a manner that maximizes disk usage, such as a container with external storage.

You can also configure the "full" version to use an external MySQL / MariaDB database.  Details below.

You need to build the container from source.
Soon you will be able to just download the container and go!

Currently only APRS-IS server connectivity is supported in a containerized environment.


## Building the container yourself

Download the entire source from Github.
Build the software using "mvn clean package"

If you are using the "full" version, from the "docker\full" directory, run the "build.bat" file.  
Make sure that your container software such as Docker Desktop is already running.
This will build the image and make it available to run.


## Configuring the container

If you are using Docker Desktop, there are two ways you run Net Central - using "docker run" and "docker-compose".
But before you do this, you must modify the environment variables needed by Net Central specific to you.

The environment variable defaults will use the following ports on your local machine to make Net Central visible to you:

User interface available at http://localhost:8880 
API available at http://localhost:8881.
Live updates for UI on port 9880.

You may also want to change the service account username and password as well.

### Environment variables for "docker run"

To use "docker run" you can edit "docker\full\run.bat" and change the values of the following environment variables:

YOUR_CALLSIGN_HERE  (found twice)
YOUR_PASSCODE_HERE
YOUR_QUERY_HERE (must be in quotation marks)

If you want to change the service account username and password from its default of serviceAccount / serviceAccountPassword, you can edit "docker\full\run.bat" and change the values of the following environment variables:

NETCENTRAL_SERVER_USERNAME
NETCENTRAL_SERVER_PASSWORD

If you change these values, you must remember them for later when you create your service account below.


### Environment variables for "docker-compose"

To use "docker run" you can edit "docker\full\docker-compose.yml" and change the values of the following environment variables:

YOUR_CALLSIGN_HERE  (found twice)
YOUR_PASSCODE_HERE
YOUR_QUERY_HERE (must be in quotation marks)

If you want to change the service account username and password from its default of serviceAccount / serviceAccountPassword, you can edit "docker\full\run.bat" and change the values of the following environment variables:

NETCENTRAL_SERVER_USERNAME
NETCENTRAL_SERVER_PASSWORD

If you change these values, you must remember them for later when you create your service account below.


### Using non-default port numbers

There are three ports needed to be exposed to the host computer that is running Docker Desktop:
UI (default 8880)
API (default 8881)
Live update (default 9880)

If you wish to change these defaults, do so with caution.  If you change the port number, environment variables may need to be added and modified in run.bat or docker-compose.yml.


## Running the container

If you are using Docker Desktop, there are two ways you run Net Central - using "docker run" and "docker-compose".

To use the "docker run" method, use the modified run.bat script in "docker\full".

To use the "docker-compose" method, run the modified "docker-compose.yml" file using "docker-compose up".
 

## Configuring Net Central

Once the container has started, it will take approximately 1 minute for the user interface to be reachable at http://localhost:8880

Once the user interface is usable, you must perform the following configuration steps:
Create your system admin user
Create your service account user (used for software to communicate between services)
Enable the APRS-IS transceiver for receive and transmit


### Create your system admin user

The first user created in Net Central acts as a system administrator and has the most privileges in the software.
To create this user, go to http://localhost:8880/register and provide your username, password (twice), optionally your call sign, and press Register.

### Create your service account user

The service account user is for the software to talk amongst its various microservices.
To create this user, go to http://localhost:8880/register and provide the username, password (twice), no call sign, and press Register.
If you changed the defaults of serviceAccount/serviceAccountPassword above, use the new values here.

### Enable the APRS-IS transceiver

To receive and send APRS traffic, you must enable the APRS-IS transceiver to do so.
Log into Net Central, and click on the "Setup" menu on the left side.
The APRS-IS transceiver will be in the list at the top.
Select the APRS-IS transceiver in the list, and then select the "Actions" tab to the right.
Press the "Enable Receive" button and confirm.
Press the "Enable Transmit" button and confirm.
To verify that your connectivity to APRS-IS is working, click on the "Dashboard" menu and the page should update with the numbers of objects heard.

And with that, you are ready to go!



## Advanced topics

### Directory contents

The docker directory contains two directories - full and split.  Each are a different implementation of containers.

Full is everything, including a database.  It is the easiest to get up and running and play with Net Central.

Split is each microservice in its own container, held together by a single docker-compose file.  It is the most correct of container implementations, but requires more effort and an external MySQL / MariaDB database.

Split is not yet complete.


### Overview of the process

You made it this far - why not reward you with the details of how the container image is put together.

#### Full variant

The full variant Dockerfile performs the following steps:
The container is an Alpine image with Java 21 pre-installed.
Several dependendies are added, including bash, sudo, openrc, mariadb, npm and libcap.
A Linux user and group (netcentral / netcentralgroup) is created.
MariaDB is configured and enables networking.
Net Central directory structure is then placed on the image.
Certain helper files are placed on the image and ensured to be in Unix format.
setcap is run to allow Node.JS to run on port 80.
Net Central built binaries for the server, transceivers and UI are placed on the image.
MariaDB is set to start on startup, but will be manually started later.
Net Central server and APRS-IS transceiver are set up as services with rc-service. They will be manually started later.
Net Central UI (using Node.JS) is set up as a service, but that doesn't yet work. It will be manually started later.
Net Central UI zip file is expanded and prepared with an "npm install".
The command "/netcentral/src/all_processes.sh & sleep infinity" is issued to start the software when the container starts and never stop.


When "/netcentral/src/all_processes.sh" runs, it does the following:
Starts the MariaDB database
Initializes the database with required users, schemas and privileges.
Net Central's required environment variables are forced by "/netcentral/src/config_env.sh" into the proper /etc/conf.d directories, as rc-service does not inherit them from the container.
Net Central server is started as a service
Net Central APRS-IS transceiver service is started as a service
Node.JS UI is started manually and detached


#### Split variant

This is not yet started.  


