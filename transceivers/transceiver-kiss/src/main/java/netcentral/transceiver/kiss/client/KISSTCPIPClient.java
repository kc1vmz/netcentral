package netcentral.transceiver.kiss.client;

import java.io.BufferedInputStream;

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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.Arrays;

import netcentral.transceiver.kiss.enums.KISSControlCode;
import netcentral.transceiver.kiss.object.KISSPacket;


public class KISSTCPIPClient {
    private static final Logger logger = LogManager.getLogger(KISSTCPIPClient.class);
    private boolean valid = false;
    private String host = "";
    private int port = 0;
    private Socket socket = null;
    private BufferedOutputStream out = null;
    private BufferedInputStream in = null;

    public void connect(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.in = new BufferedInputStream(this.socket.getInputStream());

            logger.info("Connected to KISS port.");

            this.host = host;
            this.port = port;
            setValid(true);
            return;
        } catch (IOException e) {
            logger.error("Error connecting to KISS port: " + e.getMessage());
            logger.error("Exception caught", e);
        }
        setValid(false);
    }

    public void disconnect() {
        this.host = "";
        this.port = 0;
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                logger.error("Exception caught", e);
            }
            this.socket = null;
        }
        setValid(false);
    }


    public byte[] read() throws IOException {
        if (this.socket == null) {
            return null;
        }

        ByteBuffer bbuf = ByteBuffer.allocate(1024);
        int len = in.read(bbuf.array());
        logger.info("Received from KISS port: " + len);
        if (len > 0) {
            byte [] ret = new byte[len];
            for (int i = 0; i < len; i++) {
                ret[i] = (byte) bbuf.get(i);
            }
            return ret;
        }
        return null;
    }

    public void write(KISSPacket cmd) throws IOException {
//        write(cmd.getBytes());
    }

    public void write(byte [] buffer) throws IOException {
        if ((this.socket == null) || (this.out == null)) {
            return;
        }

        this.out.write(buffer);
        this.out.flush();

        return;
    }

    public String getHost() {
        return host;
    }
    public int getPort() {
        return port;
    }
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public KISSPacket listen() throws IOException {
        byte[] resp = read();
        KISSPacket packet = new KISSPacket();
        packet.setPacket(new String(resp));

        List<String> digipeaters = new ArrayList<>();

        if (resp[0] != KISSControlCode.FEND.getValue()) {
            return packet;
        }
        if (resp[1] != KISSControlCode.DATA.getValue()) {
            return packet;
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
        currentPos++;  // skip the UI ending the loop
        nextControl = identify(resp[currentPos]);
        if (nextControl == KISSControlCode.NO_L3) {
            currentPos++;
        }

        byte[] dataBytes = Arrays.copyOfRange(resp, currentPos,  resp.length);
        packet.setData(new String(dataBytes));
        packet.setValid(true);
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
