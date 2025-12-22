package netcentral.transceiver.kiss.client;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fazecast.jSerialComm.SerialPort;

import netcentral.transceiver.kiss.enums.KISSControlCode;
import netcentral.transceiver.kiss.object.KISSPacket;


public class KISSSerialClient {
    private SerialPort activePort = null;
    private static final Logger logger = LogManager.getLogger(KISSSerialClient.class);

    public void connect(String comPort, int baudRate, List<String> initCommands) {
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
            logger.info(String.format("Port %s opened successfully.", activePort.getSystemPortName()));
            activePort.setBaudRate(baudRate);
            activePort.setNumDataBits(8);
            activePort.setNumStopBits(1);
            activePort.setParity(0);
            activePort.setFlowControl(0);
        }

        // initialize KISS
        byte [] kissInit = new byte[3]; // \x1B@K
        kissInit[0] = 0x1B;
        kissInit[1] = '@';
        kissInit[2] = 'K';
//        write(kissInit);

        if (initCommands != null) {
            for (String initCommand : initCommands) {
                write(initCommand.getBytes());
                pause(1000);
                read();
                pause(1000);
            }
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
            int numBytes = activePort.readBytes(readBuffer, readBuffer.length, 0); 
            if ((numBytes <= 0) && (!active)) {
                return null;
            }
            if ((!active) && (readBuffer[0] == KISSControlCode.FEND.getValue())) {
                packet[index] = readBuffer[0];
                index++;
                active = true;
            } else if ((!active) && (readBuffer[0] == KISSControlCode.DATA.getValue())) {
                packet[index] = KISSControlCode.FEND.getValue();
                index++;
                packet[index] = readBuffer[0];
                index++;
                active = true;
            } else if ((active) && (readBuffer[0] == KISSControlCode.FEND.getValue())) {
                // at the end
                break;
            } else if (active) {
                packet[index] = readBuffer[0];
                index++;
            } else {
                logger.info(("Throwing away character >> " + new String(readBuffer)));
            }
        }

        // return the exact buffer
        return Arrays.copyOfRange(packet, 0, index);
    }

    public void write(byte [] data) {
        activePort.writeBytes(data, data.length);
        logger.info("Sent: " + new String(data));
    }

    public void write(KISSPacket cmd) throws IOException {
        
//        write(cmd.getBytes());
    }

    private void pause(int milli) {
        try {
            Thread.sleep(milli);
        } catch (Exception e) {
        }
    }

    public KISSPacket listen() throws IOException {
        byte[] resp = read();
        if (resp == null) {
            pause(50);
            return null;
        }
        KISSPacket packet = new KISSPacket();
        packet.setPacket(new String(resp));

        List<String> digipeaters = new ArrayList<>();

        if (resp[0] != KISSControlCode.FEND.getValue()) {
            return null;
        }
        if (resp[1] != KISSControlCode.DATA.getValue()) {
            return null;
        }

        //2-8 (7 bytes, dest callsign - bitshift right to decode)
        byte[] callsignToBytes = Arrays.copyOfRange(resp, 2, 9);
        packet.setCallsignTo(decodeAX25Callsign(callsignToBytes));

        byte[] callsignFromBytes = Arrays.copyOfRange(resp, 9, 16);
        packet.setCallsignFrom(decodeAX25Callsign(callsignFromBytes));

        int currentPos = 16; //skipping past 2 header and 2 callsigns
        int callsignLength = 7;
        byte currentByte = resp[currentPos];
				
		KISSControlCode nextControl = this.identify(currentByte);
        //loop reading callsigns until UI control frame reached (indicates
        //end of digipeater callsign list)
        while (nextControl != KISSControlCode.UI) {
            byte[] repeatBytes = Arrays.copyOfRange(resp, currentPos, currentPos+callsignLength+1);	// here for 7 characters
            digipeaters.add(decodeAX25Callsign(repeatBytes));
            currentPos += callsignLength;
            currentByte = resp[currentPos];
            nextControl = identify(currentByte);
        }
        currentPos +=2;
        byte[] dataBytes = Arrays.copyOfRange(resp, currentPos,  resp.length);
        packet.setData(new String(dataBytes));
        packet.setDigipeaters(digipeaters);

        return packet;
    }

    public void enableReception() throws IOException {
    }

    private String decodeAX25Callsign(byte[] source) {
		StringBuilder sb = new StringBuilder();
        boolean spaceFound = false;

        for (int i = 0; i < 6; i++) {
            byte next = source[i];
			int unsignedInt = next & 0xFF;
			int shifted = unsignedInt >>> 1; 
            if (shifted == 32) {
                spaceFound = true;
            }
            if (!spaceFound) {
    			sb.append(new String(Character.toChars(shifted)));
            }
		}

        // bits 2 to 5 get shifted right 1 to make SSID
        int unsignedInt = source[6] & 31;
        int shifted = unsignedInt >>> 1; 
        if (shifted != 0) {
            String valStr = String.format("-%d", shifted);
            sb.append(valStr);
        }
		return sb.toString();
	}

  	private KISSControlCode identify(byte raw) {
		KISSControlCode result = KISSControlCode.UNKNOWN; 

		if (raw >= 0x20 && raw <= 0x7E) {
    		// printable character: between " " (0x20) and "~" (0x7E)
			result = KISSControlCode.PRINTABLE;
		} else {
			//KISS control codes: https://en.wikipedia.org/wiki/KISS_(TNC)
			switch (raw) {
			case ((byte)0xC0): 
				result = KISSControlCode.FEND;
				break;
			case ((byte)0x00):
				result = KISSControlCode.DATA;
				break;
			case ((byte)0x03):
				result = KISSControlCode.UI;
				break;
			case ((byte)0xF0):
				result = KISSControlCode.NO_L3;
				break;
			}
		}
		return result;
    }

}
