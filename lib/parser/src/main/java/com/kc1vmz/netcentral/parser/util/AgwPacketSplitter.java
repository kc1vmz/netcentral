package com.kc1vmz.netcentral.parser.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.parser.APRSParser;

public class AgwPacketSplitter {
    private static final int HEADER_LENGTH = 36;
    private static final Logger logger = LogManager.getLogger(APRSParser.class);

    public static List<byte []> split(byte[] data) {
        List<byte []> ret = new ArrayList<>();

        int startIndex = 0;
        int packetLength = 0;
        byte [] remaining = null;

        while (startIndex < data.length) {
            remaining = Arrays.copyOfRange(data, startIndex, data.length);
            if (remaining.length >= HEADER_LENGTH) {
                // we could have a packet in here
                packetLength = calculatePacketLength(data);
                if (remaining.length >= HEADER_LENGTH+packetLength) {
                    // still could have a packet
                    byte [] packet = Arrays.copyOfRange(remaining, 0, HEADER_LENGTH+packetLength);
                    ret.add(packet);
                    startIndex += (HEADER_LENGTH+packetLength);
                } else {
                    // length bigger than packet
                    logger.error("Header says data bigger than packet available");
                    break;
                }
            } else if (remaining.length > 0) {
                logger.error("Partial packet found");
                break;
            }

        }
        return ret;
    }

    private static int calculatePacketLength(byte[] data) {
        int packetLength = 0; 
        int packetLength1 = 0;
        int packetLength2 = data.length - HEADER_LENGTH;
        int packetLength3 = 0;

        // calculate from header
        int d1 = (int) data[28];
        if (d1 < 0) {
            d1 = (int) (data[28] & 0x7f);
        }
        int d2 = (int) (data[29] << 8);
        if (d2 < 0) {
            d2 = (int) (data[29] & 0x7f);
        }
        int d3 = (int) (data[30] << 16);
        if (d3 < 0) {
            d3 = (int) (data[30] & 0x7f) ;
        }
        int d4 = (int) (data[31] << 24);
        if (d4 < 0) {
            d4 = (int) (data[31] & 0x7f);
        }
        packetLength1 = d1 | d2 | d3 | d4;    

        // calculate based on null character
        packetLength3 = 0;
        for (int i = HEADER_LENGTH; i < data.length; i++) {
            packetLength3++;
            if (data[i] == 0x00) {
                break;
            }
        }
        packetLength = packetLength1;
        if (packetLength1 != packetLength3) {
            logger.error(String.format("Inconsistent packet length %d %d %d", packetLength1, packetLength2, packetLength3));
            packetLength = packetLength3;
        }
        return packetLength;
    }
}
