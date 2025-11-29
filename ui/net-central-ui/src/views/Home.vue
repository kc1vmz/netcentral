<template>
    <div>
        <div class="pageheader">Welcome to Net Central!</div>
        <br>Net Central allows you to maintain situational awareness using APRS in emergency and non-emergency situations.
        <br>Using an APRS net, you can coordinate and track all participants. Adding objects and providing well-known reporting information, you will enrich the APRS ecosystem with important near-time information.
        <br>
        <div class="line"><hr/></div>
        <br>
    </div>
    <div class="grid-container">
      <div class="grid-item">
        <div class="pagesubheader">How APRS nets work</div>
        <br>
        <br>Start a net and let people know about it.  Nets can be announced as bulletins when started.
        <br>Operators can then participate by checking in and out of a net by sending APRS messages to the callsign of the net.
        <p></p>
        <br>List of valid commands sent as APRS messages to the net callsign:
        <br><b>CI</b> - Check in to net
        <br><b>CO</b> - Check out of net
        <br><b>I</b> - Send net info (name, voice frequency, description)
        <br><b>S status</b> - report your status so other participants know what you are doing
        <br><b>T callsign</b> - set tactical callsign for net
        <br><b>F frequency</b> - report your voice freqency that you are monitoring
        <br><b>X n</b> - report your transmit power n in watts
        <br><b>E (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR)</b> - report your electrical power type
        <br><b>B (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR)</b> - report your backup electrical power type
        <br><b>Y (APPLIANCE, BASE, HANDHELD, INTERNET, MOBILE, OTHER)</b> - report your radio style
        <br><b>M</b> - send message to all participants in net
        <br><b>MC</b> - send message to only net control
        <br><b>R n</b> - replay last n messages sent to the net (n is a number)
        <br><b>L</b> - list net participants, their callsigns, status, location, and voice freqency monitored
        <br><b>P callsign</b> - ping callsign to test connectivity
        <br><b>O callsign</b> - get known operational status of a station by its callsign
        <br><b>Q</b> - send a question to all participants in net
        <br><b>A n</b> - answer question n sent to the net
        <br><b>QL</b> - send a list of unanswered questions
        <br><b>H</b> - display help message of all commands
        <br>
        <br><b>CI, CO, T, F, X, E, B, and Y</b> can all be combined in a single message  (ex: CI X 50 E COMM B SO)
        <br>
        <br>You must be a registered user on Net Central to start and close nets, but any operator with APRS can participate!
    </div>
    <div class="grid-item">
        <div class="pagesubheader">Enriching APRS</div>
        <br>
        <br>APRS objects representing useful emergency resources, such as medical centers, shelters and EOCs can be created to pinpoint location.
        <br>These objects are then known to Net Central and will allow you to send specific reports similar to those used in Winlink such as shelter and mobilization reports, and have them available to anyone using APRS.
        <br>
        <br>
        <br>
        <div class="pagesubheader">Licensing</div>
        <br>
        <br>For license agreements, look <a href="/License">here</a>.
    </div>
  </div>
</template>

<style scoped>
.line {
  margin : 0px;
}
.grid-container {
  display: grid;
  grid-template-columns: 50% 50%;
  margin: 10px;
  gap: 10px;
}
</style>