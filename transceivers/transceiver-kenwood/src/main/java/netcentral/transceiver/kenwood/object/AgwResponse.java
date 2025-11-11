package netcentral.transceiver.kenwood.object;

import java.util.Arrays;

public class AgwResponse {

    private boolean valid;
    private AgwHeader header;
    private byte[] data;

    public AgwResponse(byte [] data) {
        this.header = new AgwHeader(data);
        this.data = Arrays.copyOfRange(data, 36, data.length);;
        setValid(this.header.isValid());
    }

    public AgwHeader getHeader() {
        return header;
    }

    public void setHeader(AgwHeader header) {
        this.header = header;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
