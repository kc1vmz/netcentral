package netcentral.transceiver.kiss.object;

import java.util.Arrays;

public class AgwResponse {

    private boolean valid;
    private AgwHeader header;
    private byte[] data;
    private byte[] original;

    public AgwResponse(byte [] original) {
        this.header = new AgwHeader(original);
        this.data = Arrays.copyOfRange(original, 36, original.length);
        this.original = original;
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

    public byte[] getOriginal() {
        return original;
    }

    public void setOriginal(byte[] original) {
        this.original = original;
    }
    
}
