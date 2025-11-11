package com.kc1vmz.netcentral.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kc1vmz.netcentral.aprsobject.interfaces.APRSPacketInterface;

public class PacketLogger {
    private static final Logger logger = LogManager.getLogger(PacketLogger.class);

    private boolean logPackets = false;
    private boolean binaryMode = false;
    private PrintWriter packetLogger = null;
    private String logFileName = null;


    public PacketLogger(String logFileName) {
        this.logFileName = logFileName;
        this.binaryMode = true;
    }

    public PacketLogger() {
        this.logFileName = null;
        this.binaryMode = false;
    }

    public String getFileName() {
        return this.logFileName;
    }

    public void start() {
        if (!logPackets) {
            logPackets = true;
            if ((packetLogger == null) && (binaryMode)) {
                try {
                    FileWriter fw = new FileWriter(logFileName, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw);
                    packetLogger = out;    
                } catch( IOException e ){
                    // File writing/opening failed at some stage.
                    logger.error("Exception caught opening packet logging file", e);
                }
            }
        }
    }

    public void logPacket(APRSPacketInterface packet) {
        if (binaryMode) {
            if (packetLogger != null) {
                StringBuffer buffer = new StringBuffer();
                byte [] header = packet.getHeader();
                for(int i=0; i < header.length; i++){
                    buffer.append(Character.forDigit((header[i] >> 4) & 0xF, 16));
                    buffer.append(Character.forDigit((header[i] & 0xF), 16));
                }
                byte [] data = packet.getData();
                for(int i=0; i < data.length; i++){
                    buffer.append(Character.forDigit((data[i] >> 4) & 0xF, 16));
                    buffer.append(Character.forDigit((data[i] & 0xF), 16));
                }
                buffer.append("\n");
                packetLogger.append(buffer);
                packetLogger.flush();
            }
        } else {
            APRSObjectPrinter.print(packet);
        }
    }

    public void stop() {
        if (packetLogger != null) {
            packetLogger.close();
        }
        packetLogger = null;
        logPackets = false;
    }

    public List<byte []> readLoggedPackets() {
        List<byte []> ret = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(getFileName()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                byte [] hexLine = hexStringToByteArray(line);
                ret.add(hexLine);
            }
        } catch (IOException e) {
            logger.error("Exception caught reading test file for packets", e);
        }
        
        return ret;
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
