package netcentral.transceiver.agw.accessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import netcentral.transceiver.agw.object.AgwResponse2;

public class AgwResponseProcessor {
    private static final Logger logger = LogManager.getLogger(AgwResponseProcessor.class);

    public static List<AgwResponse2> getPackets(int channel, byte [] packet, PacketLoggerAccessor packetLoggerAccessor) {
        List<AgwResponse2> ret = new ArrayList<>();

        byte [] data = Arrays.copyOfRange(packet, 36, packet.length);

        String dataStr = new String (data);
        String fromLabel = String.format(" %d:Fm ", channel+1);

        List<String> packetStrings = new ArrayList<>();
        {
            int beginIndex = 0; 
            int endIndex = 0;
            while (beginIndex < dataStr.length()) {
                if ((endIndex > dataStr.length()) || (dataStr.charAt(endIndex) == 0)) {
                    String p = dataStr.substring(beginIndex, endIndex);
                    endIndex++;
                    beginIndex = endIndex;
                    if (p.length() > 0) {
                        packetStrings.add(p);
                    }
                } else {
                    endIndex++;
                }
            }
        }

        if (packetStrings.isEmpty()) {
            return ret;
        }

        for (String packetString : packetStrings) {
            String remain = packetString;
            String callsignTo = "";
            String callsignFrom = "";
            String digis = "";
            List<String> digipeaters = new ArrayList<>();

            packetLoggerAccessor.savePacket(packetString);

            if (remain.startsWith(fromLabel)) {
                remain = remain.substring(fromLabel.length());
                int eol = remain.indexOf("\r");
                if (eol != -1) {
                    String fromToDigis = remain.substring(0, eol);
                    String [] parts = fromToDigis.split(" ");
                    if ((parts != null) && (parts.length > 5)) {
                        if ("To".equals(parts[1]) && ("Via".equals(parts[3])) && ("<UI".equals(parts[5]))) {
                            // valid start to packet
                            callsignFrom = parts[0];
                            callsignTo = parts[2];
                            digis = parts[4];
                            String [] digiParts = digis.split(",");
                            if (digiParts != null) {
                                for (String digiPart : digiParts) {
                                    digipeaters.add(digiPart);
                                }
                            }
                        }
                    }
                    remain = remain.substring(eol+1); // now past the to from line

                    AgwResponse2 r = new AgwResponse2(callsignFrom, callsignTo, digipeaters, remain);
                    ret.add(r);
                }
            }
        }

        logger.info(String.format("%d packets constructed", ret.size()));
        return ret;
    }
}
