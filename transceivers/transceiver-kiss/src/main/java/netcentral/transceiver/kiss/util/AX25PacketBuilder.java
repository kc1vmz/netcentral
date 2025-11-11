package netcentral.transceiver.kiss.util;

import netcentral.transceiver.kiss.object.KISSPacket;

public class AX25PacketBuilder {

    public static byte[] buildPacket(KISSPacket packet, String command) {
        String callFrom = getCallsignRoot(packet.getCallsignFrom());
        byte callFromSSID = (byte) getCallsignSSID(packet.getCallsignFrom());
        String callTo = packet.getApplicationName();
        byte callToSSID = 0;
        String msgDest = packet.getCallsignTo();
        String data = packet.getData();
        int digipeaterCount = 0;

        String aprsMessage = String.format("%s%-9s:%s",command, msgDest, data);
        if (packet.getDigipeaters() != null) {
            digipeaterCount = packet.getDigipeaters().size();
        }

        int arrayLen = 16+aprsMessage.length()+(digipeaterCount*7);
        int index = 0;

        // Clear buffer for sure
        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        // write in shifted spaces
        byte [] dest_bytes = callTo.getBytes();
        for (int c = 0; c < 7; c++) {
            ret[index+c] = ' ' << 1;
        }
        // write in destination
        for (int c = 0; c < dest_bytes.length && c < 7; c++) {
            ret[index+c] = (byte) (dest_bytes[c] << 1);
        }
        ret[index+6] = (byte) (((callToSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 0x80;  // for command
        index += 7;

        // src address
        byte [] src_bytes = callFrom.getBytes();
        int k = 0;
        for (; k < callFrom.length() && k < 6; k++) {
            ret[index+k] = (byte) (src_bytes[k] << 1);
        }
        for ( ; k < 6; k++) {
            ret[index+k] = ' ' << 1;
        }
        ret[index+6] = (byte) (((callFromSSID & 0x0F) << 1) + 0x60); // SSID 
        if (digipeaterCount == 0) {
            ret[index+6] += 1; // set bottom nibble to state end of addresses
        }
        index += 7;

        if (digipeaterCount != 0) {
            for (int i = 0; i < digipeaterCount; i++) {
                String digi = packet.getDigipeaters().get(i);
                byte digiSSID = (byte) getCallsignSSID(digi);
                digi = getCallsignRoot(digi);
                byte [] digi_bytes = digi.getBytes();
                int x = 0;
                for (; x < digi_bytes.length && x < 6; x++) {
                    ret[index+x] = (byte) (digi_bytes[x] << 1);
                }
                for ( ; x < 6; x++) {
                    ret[index+x] = ' ' << 1;
                }
                ret[index+6] = (byte) (((digiSSID & 0x0F) << 1) + 0x60); // SSID 
                if (i == digipeaterCount-1) {
                    // last one
                    ret[index+6] += 1; // set bottom nibble to state end of addresses
                }
                index += 7;
            }
        }


        ret[index++] = 3 ; // Flag
        ret[index++] = (byte) 0xF0; // Protocol
        
        // data
        if (aprsMessage != null) {
            byte[] datab = aprsMessage.getBytes();
            for (int c = 0; c < datab.length && c < 256; c++) {
                ret[index+c] = datab[c];
            }
            index += datab.length;
        }

        return ret;
    }

    public static byte[] buildPacketWithoutDigipeaters(KISSPacket packet, String command) {
        String callFrom = getCallsignRoot(packet.getCallsignFrom());
        byte callFromSSID = (byte) getCallsignSSID(packet.getCallsignFrom());
        String callTo = packet.getApplicationName();
        byte callToSSID = 0;
        String msgDest = packet.getCallsignTo();
        String data = packet.getData();

        String aprsMessage = String.format("%s%-9s:%s",command, msgDest, data);

        int arrayLen = 16+aprsMessage.length();
        int index = 0;

        // Clear buffer for sure
        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        // write in shifted spaces
        byte [] dest_bytes = callTo.getBytes();
        for (int c = 0; c < 7; c++) {
            ret[index+c] = ' ' << 1;
        }
        // write in destination
        for (int c = 0; c < dest_bytes.length && c < 7; c++) {
            ret[index+c] = (byte) (dest_bytes[c] << 1);
        }
        ret[index+6] = (byte) (((callToSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 0x80;  // for command
        index += 7;

        // src address
        byte [] src_bytes = callFrom.getBytes();
        int k = 0;
        for (; k < callFrom.length() && k < 6; k++) {
            ret[index+k] = (byte) (src_bytes[k] << 1);
        }
        for ( ; k < 6; k++) {
            ret[index+k] = ' ' << 1;
        }
        ret[index+6] = (byte) (((callFromSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 1; // set bottom nibble to state end of addresses
        index += 7;

        ret[index++] = 3 ; // Flag
        ret[index++] = (byte) 0xF0; // Protocol
        
        // data
        if (aprsMessage != null) {
            byte[] datab = aprsMessage.getBytes();
            for (int c = 0; c < datab.length && c < 256; c++) {
                ret[index+c] = datab[c];
            }
            index += datab.length;
        }

        return ret;
    }

    public static byte[] buildObjectPacket(KISSPacket packet) {
        String callFrom = getCallsignRoot(packet.getCallsignFrom());
        byte callFromSSID = (byte) getCallsignSSID(packet.getCallsignFrom());
        String callTo = packet.getApplicationName();
        byte callToSSID = 0;
        String data = packet.getData();
        int digipeaterCount = 0;

        String aprsMessage = data;

        if (packet.getDigipeaters() != null) {
            digipeaterCount = packet.getDigipeaters().size();
        }

        int arrayLen = 16+aprsMessage.length()+(digipeaterCount*7);
        int index = 0;

        // Clear buffer for sure
        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        // write in shifted spaces
        byte [] dest_bytes = callTo.getBytes();
        for (int c = 0; c < 7; c++) {
            ret[index+c] = ' ' << 1;
        }
        // write in destination
        for (int c = 0; c < dest_bytes.length && c < 7; c++) {
            ret[index+c] = (byte) (dest_bytes[c] << 1);
        }
        ret[index+6] = (byte) (((callToSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 0x80;  // for command
        index += 7;

        // src address
        byte [] src_bytes = callFrom.getBytes();
        int k = 0;
        for (; k < callFrom.length() && k < 6; k++) {
            ret[index+k] = (byte) (src_bytes[k] << 1);
        }
        for ( ; k < 6; k++) {
            ret[index+k] = ' ' << 1;
        }
        ret[index+6] = (byte) (((callFromSSID & 0x0F) << 1) + 0x60); // SSID 
        if (digipeaterCount == 0) {
            ret[index+6] += 1; // set bottom nibble to state end of addresses
        }
        index += 7;

        if (digipeaterCount != 0) {
            for (int i = 0; i < digipeaterCount; i++) {
                String digi = packet.getDigipeaters().get(i);
                byte digiSSID = (byte) getCallsignSSID(digi);
                digi = getCallsignRoot(digi);
                byte [] digi_bytes = digi.getBytes();
                int x = 0;
                for (; x < digi_bytes.length && x < 6; x++) {
                    ret[index+x] = (byte) (digi_bytes[x] << 1);
                }
                for ( ; x < 6; x++) {
                    ret[index+x] = ' ' << 1;
                }
                ret[index+6] = (byte) (((digiSSID & 0x0F) << 1) + 0x60); // SSID 
                if (i == digipeaterCount-1) {
                    // last one
                    ret[index+6] += 1; // set bottom nibble to state end of addresses
                }
                index += 7;
            }
        }

        ret[index++] = 3 ; // Flag
        ret[index++] = (byte) 0xF0; // Protocol
        
        // data
        if (aprsMessage != null) {
            byte[] datab = aprsMessage.getBytes();
            for (int c = 0; c < datab.length && c < 256; c++) {
                ret[index+c] = datab[c];
            }
            index += datab.length;
        }

        return ret;
    }

    public static byte[] buildObjectPacketWithoutDigipeater(KISSPacket packet) {
        String callFrom = getCallsignRoot(packet.getCallsignFrom());
        byte callFromSSID = (byte) getCallsignSSID(packet.getCallsignFrom());
        String callTo = packet.getApplicationName();
        byte callToSSID = 0;
        String data = packet.getData();

        String aprsMessage = data;

        int arrayLen = 16+aprsMessage.length();
        int index = 0;

        // Clear buffer for sure
        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        // write in shifted spaces
        byte [] dest_bytes = callTo.getBytes();
        for (int c = 0; c < 7; c++) {
            ret[index+c] = ' ' << 1;
        }
        // write in destination
        for (int c = 0; c < dest_bytes.length && c < 7; c++) {
            ret[index+c] = (byte) (dest_bytes[c] << 1);
        }
        ret[index+6] = (byte) (((callToSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 0x80;  // for command
        index += 7;

        // src address
        byte [] src_bytes = callFrom.getBytes();
        int k = 0;
        for (; k < callFrom.length() && k < 6; k++) {
            ret[index+k] = (byte) (src_bytes[k] << 1);
        }
        for ( ; k < 6; k++) {
            ret[index+k] = ' ' << 1;
        }
        ret[index+6] = (byte) (((callFromSSID & 0x0F) << 1) + 0x60); // SSID 
        ret[index+6] += 1; // set bottom nibble to state end of addresses
        index += 7;

        ret[index++] = 3 ; // Flag
        ret[index++] = (byte) 0xF0; // Protocol
        
        // data
        if (aprsMessage != null) {
            byte[] datab = aprsMessage.getBytes();
            for (int c = 0; c < datab.length && c < 256; c++) {
                ret[index+c] = datab[c];
            }
            index += datab.length;
        }

        return ret;
    }

    private static byte getCallsignSSID(String callsignFrom) {
        byte ret = 0;
        int index = -1;
        if ((index = callsignFrom.indexOf("-")) != -1) {
            String ssidStr = callsignFrom.substring(index+1);
            try {
                int ssid = Integer.parseInt(ssidStr);
                ret = (byte) ssid;
            } catch (Exception e) {
                // ignore
            }
        }
        return ret;
    }

    private static String getCallsignRoot(String callsignFrom) {
        int index = -1;
        if ((index = callsignFrom.indexOf("-")) != -1) {
            return callsignFrom.substring(0, index);
        }
        return callsignFrom;
    }
}
