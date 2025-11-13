package netcentral.transceiver.agw.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import netcentral.transceiver.agw.accessor.AgwResponseProcessor;
import netcentral.transceiver.agw.accessor.PacketLoggerAccessor;
import netcentral.transceiver.agw.object.AgwCommand;
import netcentral.transceiver.agw.object.AgwFrameType;
import netcentral.transceiver.agw.object.AgwResponse2;
import netcentral.transceiver.agw.object.AgwVersionResponse;


public class AGWPEClient {
    private static final Logger logger = LogManager.getLogger(AGWPEClient.class);
    private boolean valid = false;
    private String host = "";
    private int port = 0;
    private Socket socket = null;
    private BufferedOutputStream out = null;
    private BufferedReader in = null;
    private PacketLoggerAccessor packetLoggerAccessor;

    public void connect(String host, int port, PacketLoggerAccessor packetLoggerAccessor) {
        try {
            this.socket = new Socket(host, port);
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            logger.info("Connected to AGWPE port.");

            this.host = host;
            this.port = port;
            this.packetLoggerAccessor = packetLoggerAccessor;
            setValid(true);
            return;
        } catch (IOException e) {
            logger.error("Error connecting to AGWPE port: " + e.getMessage());
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

        CharBuffer cbuf = CharBuffer.allocate(AgwCommand.getMaxPacketSize());
        int len = in.read(cbuf);
        logger.info("Received from AGWPE port: " + len);
        if (len > 0) {
            byte [] ret = new byte[len];
            for (int i = 0; i < len; i++) {
                ret[i] = (byte) cbuf.get(i);
            }
            return ret;
        }
        return null;
    }

    public void write(AgwCommand cmd) throws IOException {
        write(cmd.getBytes());
    }

    public void writeV(AgwCommand cmd) throws IOException {
        write(cmd.getBytesV());
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

    public AgwVersionResponse requestVersion(byte channel) throws IOException {
        AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.GET_VERSION, null,null, null, null);
        write(command);
        byte[] resp = read();
        return new AgwVersionResponse(resp);
    }

    public List<AgwResponse2> listen(byte channel) throws IOException {
        byte[] resp = read();
        List<AgwResponse2> responses = AgwResponseProcessor.getPackets(channel, resp, packetLoggerAccessor);
        return responses;
    }

    public void enableReception(byte channel) throws IOException {
        AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RECEPTION, null,null, null, null);
        write(command);
    }

    public void enableRawReception(byte channel) throws IOException {
        AgwCommand command = new AgwCommand(channel, (byte) AgwFrameType.ENABLE_RAW_RECEPTION, null,null, null, null);
        write(command);
    }
}
