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
Environment variables
Operating System specifics
Java
Third-party software
MySQL 


## Running Net Central

Net Central is composed of a Server and one or more transceiver services to connect to APRS data.
Follow the configuration steps above and start each service.


## License

No license has been selected to govern reuse or sharing.  Once that decision is made, the license will be added.