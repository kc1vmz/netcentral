# Net Central

## What is this?

Net Central allows you to maintain situational awareness using APRS in emergency and non-emergency situations.  
  
Using an APRS net, you can coordinate and track all participants. Adding objects and providing well-known reporting information, you will enrich the APRS ecosystem with important near-time information.  

## How APRS nets work
  
Start a net and let people know about it. Nets can be announced as bulletins when started.  
Operators can then participate by checking in and out of a net by sending APRS messages to the callsign of the net.  
  
List of valid commands sent as APRS messages to the net callsign:  
CI - Check in to net  
CO - Check out of net  
I - Send net info (name, voice frequency, description)  
S status - report your status so other participants know what you are doing  
T callsign - set tactical callsign for net  
F frequency - report your voice freqency that you are monitoring  
X n - report your transmit power n in watts  
E (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR) - report your electrical power type  
B (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR) - report your backup electrical power type  
Y (APPLIANCE, BASE, HANDHELD, INTERNET, MOBILE, OTHER) - report your radio style  
M - send message to all participants in net  
MC - send message to only net control  
R n - replay last n messages sent to the net (n is a number)  
L - list net participants, their callsigns, status, location, and voice freqency monitored  
P callsign - ping callsign to test connectivity  
O callsign - get known operational status of a station by its callsign  
Q - send a question to all participants in net  
A n - answer question n sent to the net  
QL - send a list of unanswered questions  
INV callsign[,callsign2,...] - invite one or more callsigns to a net  
H - display help message of all commands  
  
CI, CO, T, F, X, E, B, and Y can all be combined in a single message (ex: CI X 50 E COMM B SO)  
  
You must be a registered user on Net Central to start and close nets, but any operator with APRS can participate!  
  
## Build notes

Net Central is a Maven-based build.  

From the root directory, run "mvn clean package".  This will build all java components for the libraries, server and transceiver services.

You may also use your favorite IDE that supports maven projects, such as VS Code.  

## Configuration notes

Make sure that the environment variables are set per the next section.  
Enrivonment variables must reference directories that exist; directories will not be created for you.  

### Public IP addresses

To access the UI from systems other than localhost, update the Net Central UI .env file with public IP addresses:  
VITE_APP_API_URL=http://YOURIPADDRESS:8880/api/v1  
VITE_APP_UPDATE_URL=http://YOURIPADDRESS:8881  


### Environment variables

Environment variables  

#### Mandatory  
NETCENTRAL_SERVER_TMP_DIR  
NETCENTRAL_SERVER_USERNAME  
NETCENTRAL_SERVER_PASSWORD  
NETCENTRAL_APRS_CALLSIGN  
  
  
#### Optional  
NETCENTRAL_SERVER_HTTP_PORT     default 8880  
NETCENTRAL_SERVER_UPDATE_HTTP_PORT  default 8881  
NETCENTRAL_SERVER_MYSQL_HOST      default localhost  
NETCENTRAL_SERVER_MYSQL_PORT      defalut 3306  
NETCENTRAL_SERVER_MYSQL_DBNAME    default netcentral  
NETCENTRAL_SERVER_MYSQL_USERNAME  default netcentral  
NETCENTRAL_SERVER_MYSQL_PASSWORD  default netcentral  
VITE_APP_API_PORT                 default 8880  
VITE_APP_UPDATE_PORT              default 8881  
VITE_APP_API_HOST                 default not specified (defaults to Node.JS address)  
  
  
#### All transceivers  
##### Optional  
NETCENTRAL_SERVER_IP      default localhost  
NETCENTRAL_SERVER_PORT    default 8880  
  
  
#### AGW Transceiver  
##### Optional  
NETCENTRAL_AGW_HTTP_PORT          default 8004 (should be same)  
NETCENTRAL_TRANS_AGW_PORT         default 8004 (should be same)  
NETCENTRAL_TRANS_AGW_TNC_HOSTNAME default localhost  
NETCENTRAL_TRANS_AGW_TNC_PORT     default 8000  
NETCENTRAL_TRANS_AGW_TNC_CHANNEL  default 0  
  
  
#### APRS-IS Transceiver  
  
##### Mandatory  
NETCENTRAL_TRANS_APRSIS_CALLSIGN  
NETCENTRAL_TRANS_APRSIS_PASSCODE  
NETCENTRAL_TRANS_APRSIS_QUERY  
  
##### Optional  
NETCENTRAL_TRANS_APRSIS_IS_SERVER default noam.aprs2.net  
NETCENTRAL_TRANS_APRSIS_IS_PORT   default 14580  
NETCENTRAL_TRANS_APRSIS_HTTP_PORT default 8002  
NETCENTRAL_TRANS_APRSIS_PORT      default 8002  
  
  
#### Kenwood Transceiver  
##### Optional  
NETCENTRAL_TRANS_KENWOOD_TNC_PORT   default COM6  
NETCENTRAL_TRANS_KENWOOD_TNC_BAUD   default 9600  
NETCENTRAL_TRANS_KENWOOD_HTTP_PORT  default 8003  
NETCENTRAL_TRANS_KENWOOD_PORT       default 8003  
  
  
#### KISS Transceiver  
##### Mandatory  
NETCENTRAL_TRANS_KISS_TNC_PORT  
NETCENTRAL_TRANS_KISS_TNC_BAUD  

##### Optional  
NETCENTRAL_TRANS_KISS_DIGI_PATH     default WIDE2-1,WIDE1-1  
NETCENTRAL_TRANS_KISS_HTTP_PORT     default 8005  
NETCENTRAL_TRANS_KISS_TNC_INIT1  
NETCENTRAL_TRANS_KISS_TNC_INIT2  
NETCENTRAL_TRANS_KISS_TNC_HOST      default localhost  
  
  
#### Test Transceiver  
##### Optional  
NETCENTRAL_TEST_HTTP_PORT       default 8010  


### Operating System specifics

#### Linux

##### Port number usage
If you attempt to change port numbers other than the defaults, remember that Linux restricts the use of port numbers below 1024 to root without extra effort.  

For Node.JS, the use of port 80 requires you to perform the following steps to enable the use of port 80.  
Run:  
sudo apt-get install libcap2-bin  
readlink -f `which node`     (gives you NODE_PATH)  
sudo setcap cap_net_bind_service=+ep NODE_PATH  

##### Firewall settings

Ensure that ports 8880 and 80 are open on the server's firewall. Failure to open these ports will prevent UI and API access.  

#### Windows

##### Firewall settings

Ensure that ports 8880 and 80 are open on the server's firewall. Failure to open these ports will prevent UI and API access.  


### Java

Java 21 JDK is required for build, and a JRE based on Java 21 for execution.  

Obtain the Java 21 JDK for your specific platform and build using maven or your favorite IDE supporting maven projects.

### Third-party software

Java 21 JRE must be installed and configured prior to running any Net Central service.
MySQL must be installed and configured prior to running the Net Central server.  Currently tested with MySQL version 8.x.
Node.JS must be installed prior to running the user interface.  Currently supports Node.JS version 20.19 or 22.12+

### MySQL 

Install MySQL and make note of the port number and root credentials supplied.  
Create a schema, making note of its name.  By default Net Central expects the name "netcentral".  
Create a user and password, and give all privileges to the new schema to that user.  By default, Net Central expects "netcentral" / "netcentral".  
  
The tables will be created upon startup of the Net Central server.  


## Running Net Central

Net Central is composed of a Server and one or more transceiver services to connect to APRS data.  
Follow the configuration steps above and run each service from a command shell.  

Each service can be found in its /target subdirectory after the build has completed.  
  
netcentral-server-1.0.3.jar  
transceiver-agw-1.0.3.jar  
transceiver-aprsis-1.0.3.jar  
transceiver-kenwood-1.0.3.jar  
transceiver-kiss-1.0.3.jar  
transceiver-test-1.0.3.jar  

To run the JAR file, specify:  
    java -jar jar-file-name


## License
  
This software is licensed under GPL v3.  For more information, see LICENSE file.  