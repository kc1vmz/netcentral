package netcentral.transceiver.kiss.object;

public class AgwVersionResponse extends AgwResponse {

    private int major;
    private int minor;
    private static final int RESPONSE_LENGTH = 44; // length of version response
    private static final int DATA_LENGTH = 8; // length of version response

    public AgwVersionResponse(byte [] data) {
        super(data);
        if ((!getHeader().isValid()) || (getHeader().getDatakind() != AgwFrameType.GET_VERSION) || (getHeader().getData_len_NETLE() != DATA_LENGTH) || (data == null) || (data.length < RESPONSE_LENGTH)) {
            setValid(false);
        } else {
            int major = (data[0]) + (data[1] << 8);
            int minor = (data[4]) + (data[5] << 8);
            setMajor(major);
            setMinor(minor);
            setValid(true);
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getVersion() {
        return String.format("%d.%d", getMajor(), getMinor());
    }
}
