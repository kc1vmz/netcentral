package netcentral.transceiver.kiss.util;

import netcentral.transceiver.kiss.enums.KISSControlCode;

public class KissPacketBuilder {

    public static byte[] build(byte [] ax25Bytes, byte port) {
        if (ax25Bytes == null) {
            return null;
        }

        int len = ax25Bytes.length + 3;
        byte [] ret = new byte[len];

        ret[0] = KISSControlCode.FEND.getValue();
        ret[1] = port;  // port 0 - serial , single port

        for (int i = 0; i < ax25Bytes.length; i++) {
            ret[i+2] = ax25Bytes[i];
        }
        ret[len-1] = KISSControlCode.FEND.getValue();;
        return ret;
    }
}
