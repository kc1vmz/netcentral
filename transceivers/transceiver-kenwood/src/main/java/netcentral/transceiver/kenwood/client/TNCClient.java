package netcentral.transceiver.kenwood.client;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

import netcentral.transceiver.kenwood.object.AgwCommand;
import netcentral.transceiver.kenwood.object.AgwFrameType;
import netcentral.transceiver.kenwood.object.AgwVersionResponse;


public class TNCClient {
    private SerialPort activePort = null;
    private static final Logger logger = LogManager.getLogger(TNCClient.class);
    public void connect(String comPort, int baudRate) {
        SerialPort[] comPorts = SerialPort.getCommPorts();
        int index = 0;
        if (comPorts.length == 0) {
            logger.error("No serial ports found.");
            return;
        } else {
            for (int i = 0; i < comPorts.length; i++) {
                if (comPorts[i].getSystemPortName().equals(comPort)) {
                    index = i;
                    break;
                }
            }
        }

        // Select the first available port (you might need to adjust this)
        activePort = comPorts[index]; 
        logger.info("Using port: " + activePort.getSystemPortName());

        // Open the port
        if (activePort.openPort()) {
            System.out.println("Port opened successfully.");
            activePort.setBaudRate(baudRate);
            activePort.setNumDataBits(8);
            activePort.setNumStopBits(1);
            activePort.setParity(0);
            activePort.setFlowControl(0);
        }


    }
    public void disconnect() {
        // Close the port
        activePort.closePort();
        activePort = null;
    }

    public byte[] read() {
        if (activePort == null) {
            throw new NullPointerException("No active com port");
        }
        byte [] packet = new byte[2048];
        int index = 0;
        boolean active = false;

        // Read data from the port (example for non-blocking read)
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
        byte[] readBuffer = new byte[1];
        while (index < packet.length) {
            int numBytes = activePort.readBytes(readBuffer, readBuffer.length, 0); // Timeout 0 for non-blocking
            if ((numBytes == 0) && (!active)) {
                return null;
            }
            active = true;
            if (readBuffer[0] == 10) {
                break;
            } else {
                packet[index] = readBuffer[0];
                index++;
            }
        }

        // return the exact buffer
        return Arrays.copyOfRange(packet, 0, index);
    }

    public void write(byte [] data) {
        activePort.writeBytes(data, data.length);
        logger.info("Sent: " + new String(data));
    }

    public AgwVersionResponse requestVersion(byte channel) throws IOException {
        AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.GET_VERSION, null,null, null);
        write(command);
        byte[] resp = read();
        return new AgwVersionResponse(resp);
    }
    public void write(AgwCommand cmd) throws IOException {
        write(cmd.getBytes());
    }
    public void enableReception(byte channel) throws IOException {
        // AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RECEPTION, null,null, null);
        byte[] resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
        write("RESET\r".getBytes());
        pause(500);
        resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
    }
    public void enableTransparentMode(byte channel) throws IOException {
        // AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RECEPTION, null,null, null);
        byte[] resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
        write("CONMODE TRANS\r".getBytes());
        pause(500);
        resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
    }
    public void enableCallsign(byte channel, String callsign) {
        // AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RECEPTION, null,null, null);
        byte[] resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
        write(String.format("MYCALL %s\r", callsign).getBytes());
        pause(500);
        resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
    }
    public void enableUnprotoAPRS(byte channel, String appname) {
        // AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RECEPTION, null,null, null);
        byte[] resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
        write(String.format("UNPROTO %s\r", appname).getBytes());
        pause(500);
        resp = read(); // clear the buffer
        if (resp != null) {
            logger.info("Message cleared - " + new String (resp));
        }
    }

    private void pause(int milli) {
        try {
            Thread.sleep(milli);
        } catch (Exception e) {
        }
    }
}
