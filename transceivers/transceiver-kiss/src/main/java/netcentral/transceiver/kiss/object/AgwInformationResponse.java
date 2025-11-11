package netcentral.transceiver.kiss.object;

public class AgwInformationResponse extends AgwResponse {

    private static final int RESPONSE_LENGTH_MIN = 36; // length of version response
    private String text;

    public AgwInformationResponse(byte [] data) {
        super(data);
        if ((!getHeader().isValid()) || (getHeader().getDatakind() != AgwFrameType.INFORMATION) || (data == null) || (data.length < RESPONSE_LENGTH_MIN)) {
            setValid(false);
        } else {
            text = "";
            int c = 36;
            int len = getHeader().getData_len_NETLE();
            while (len > 0) {
                // while not null terminated
                if (data[c] == 0) {
                    break;
                }
                text += (char) data[c];
                c++;
                len--;
            }
            setValid(true);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
