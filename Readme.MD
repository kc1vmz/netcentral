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
R n - replay last n messages sent to the net (n is a number)
L - list net participants, their callsigns, status, location, and voice freqency monitored
P callsign - ping callsign to test connectivity
O callsign - get known operational status of a station by its callsign
H - display help message of all commands

CI, CO, T, F, X, E, B, and Y can all be combined in a single message (ex: CI X 50 E COMM B SO)

You must be a registered user on Net Central to start and close nets, but any operator with APRS can participate!

## Build notes

Net Central is a Maven-based build.

## Configuration notes

TBD

### Environment variables

Environment variables

Mandatory
NETCENTRAL_SERVER_TMP_DIR
NETCENTRAL_SERVER_USERNAME
NETCENTRAL_SERVER_PASSWORD
NETCENTRAL_APRS_CALLSIGN


Optional
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



All transceivers
Optional
NETCENTRAL_SERVER_IP      default localhost
NETCENTRAL_SERVER_PORT    default 8880


AGW Transceiver
Optional
NETCENTRAL_AGW_HTTP_PORT          default 8004 (should be same)
NETCENTRAL_TRANS_AGW_PORT         default 8004 (should be same)
NETCENTRAL_TRANS_AGW_TNC_HOSTNAME default localhost
NETCENTRAL_TRANS_AGW_TNC_PORT     default 8000
NETCENTRAL_TRANS_AGW_TNC_CHANNEL  default 0


APRS-IS Transceiver

Mandatory
NETCENTRAL_TRANS_APRSIS_CALLSIGN
NETCENTRAL_TRANS_APRSIS_PASSCODE
NETCENTRAL_TRANS_APRSIS_QUERY

Optional
NETCENTRAL_TRANS_APRSIS_IS_SERVER default noam.aprs2.net
NETCENTRAL_TRANS_APRSIS_IS_PORT   default 14580
NETCENTRAL_TRANS_APRSIS_HTTP_PORT default 8002
NETCENTRAL_TRANS_APRSIS_PORT      default 8002


Kenwood Transceiver
Optional
NETCENTRAL_TRANS_KENWOOD_TNC_PORT   default COM6
NETCENTRAL_TRANS_KENWOOD_TNC_BAUD   default 9600
NETCENTRAL_TRANS_KENWOOD_HTTP_PORT  default 8003
NETCENTRAL_TRANS_KENWOOD_PORT       default 8003


KISS Transceiver
Mandatory
NETCENTRAL_TRANS_KISS_TNC_PORT
NETCENTRAL_TRANS_KISS_TNC_BAUD

Optional
NETCENTRAL_TRANS_KISS_DIGI_PATH     default WIDE2-1,WIDE1-1
NETCENTRAL_TRANS_KISS_HTTP_PORT     default 8005
NETCENTRAL_TRANS_KISS_TNC_INIT1
NETCENTRAL_TRANS_KISS_TNC_INIT2
NETCENTRAL_TRANS_KISS_TNC_HOST      default localhost


Test Transceiver
Optional
NETCENTRAL_TEST_HTTP_PORT       default 8010



### Operating System specifics

### Java

### Third-party software

### MySQL 


## Running Net Central

Net Central is composed of a Server and one or more transceiver services to connect to APRS data.
Follow the configuration steps above and start each service.


## License

No license has been selected to govern reuse or sharing.  Once that decision is made, the license will be added.