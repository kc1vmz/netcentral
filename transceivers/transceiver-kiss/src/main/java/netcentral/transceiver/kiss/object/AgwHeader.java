package netcentral.transceiver.kiss.object;

public class AgwHeader {
    private boolean valid;

    private byte portx;	
    private byte reserved1;
    private byte reserved2;
    private byte reserved3;
    private byte datakind;	
    private byte reserved4;
    private byte pid;
    private byte reserved5;
    private String call_from; //    char call_from[10];
    private String call_to; //  char call_to[10];
    private int data_len_NETLE;		
    private int user_reserved_NETLE;
    private static final int LENGTH_BYTES = 36;

    public AgwHeader() {
        setValid(false);
    }

    public AgwHeader(byte portx, byte datakind, String call_from, String call_to) {
        setPortx(portx);
        setDatakind(datakind);
        setCall_from(call_from);
        setCall_to(call_to);
        setValid(true);
    }

    public AgwHeader(byte portx, byte datakind, byte [] data) {
        init(data);
        if (isValid()) {
            data[0] = portx;
            setPortx(data[0]);
            data[4] = datakind;
            setDatakind(data[4]);
        }
    }

    public AgwHeader(byte [] data) {
        init(data);
    }

    private void init(byte [] data) {
        if ((data != null) && (data.length >= LENGTH_BYTES)) {
            setPortx(data[0]);
            setReserved1(data[1]);
            setReserved2(data[2]);
            setReserved3(data[3]);
            setDatakind(data[4]);
            setReserved4(data[5]);
            setPid(data[6]);
            setReserved5(data[7]);
            String call_from = "";
            for (int i = 0; i < 10; i++) {
                if (data[8+i] != 0) {
                    call_from += (char) data[8+i];
                }
            }
            setCall_from(call_from);  
            String call_to = "";
            for (int i = 0; i < 10; i++) {
                if (data[18+i] != 0) {
                    call_to += (char) data[18+i];
                }
            }
            setCall_to(call_to);

            int reserved = 0;
            int dataLen = data.length - AgwHeader.LENGTH_BYTES;

            setData_len_NETLE(dataLen);
            setUser_reserved_NETLE(reserved);
            setValid(true);
        } else {
            setValid(false);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public byte getPortx() {
        return portx;
    }

    public void setPortx(byte portx) {
        this.portx = portx;
    }

    public byte getReserved1() {
        return reserved1;
    }

    public void setReserved1(byte reserved1) {
        this.reserved1 = reserved1;
    }

    public byte getReserved2() {
        return reserved2;
    }

    public void setReserved2(byte reserved2) {
        this.reserved2 = reserved2;
    }

    public byte getReserved3() {
        return reserved3;
    }

    public void setReserved3(byte reserved3) {
        this.reserved3 = reserved3;
    }

    public byte getDatakind() {
        return datakind;
    }

    public void setDatakind(byte datakind) {
        this.datakind = datakind;
    }

    public byte getReserved4() {
        return reserved4;
    }

    public void setReserved4(byte reserved4) {
        this.reserved4 = reserved4;
    }

    public byte getPid() {
        return pid;
    }

    public void setPid(byte pid) {
        this.pid = pid;
    }

    public byte getReserved5() {
        return reserved5;
    }

    public void setReserved5(byte reserved5) {
        this.reserved5 = reserved5;
    }

    public String getCall_from() {
        return call_from;
    }

    public void setCall_from(String call_from) {
        this.call_from = call_from;
    }

    public String getCall_to() {
        return call_to;
    }

    public void setCall_to(String call_to) {
        this.call_to = call_to;
    }

    public int getData_len_NETLE() {
        return data_len_NETLE;
    }

    public void setData_len_NETLE(int data_len_NETLE) {
        this.data_len_NETLE = data_len_NETLE;
    }

    public int getUser_reserved_NETLE() {
        return user_reserved_NETLE;
    }

    public void setUser_reserved_NETLE(int user_reserved_NETLE) {
        this.user_reserved_NETLE = user_reserved_NETLE;
    }

    public byte[] getBytes() {
        byte[] ret = new byte[LENGTH_BYTES]; 
        int index = 0;

        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        ret[index++] = getPortx();  // 0
        ret[index++] = getReserved1(); // 1
        ret[index++] = getReserved2(); // 2
        ret[index++] = getReserved3(); // 3
        ret[index++] = getDatakind(); // 4
        ret[index++] = getReserved4(); // 5
        ret[index++] = getPid(); // 6
        ret[index++] = getReserved5(); // 7
//call_f
        if (call_from != null) {
            byte [] cfb = call_from.getBytes();
            int maxCfb = cfb.length;
            if (maxCfb > 10) {
                maxCfb = 10;
            }
            index = 8;
            for (int c = 0; c < maxCfb; c++) {
                ret[index++] = cfb[c];
            }
        }
//call_t
        if (call_to != null) {
            byte [] ctb = call_to.getBytes();
            int maxCtb = ctb.length;
            if (maxCtb > 10) {
                maxCtb = 10;
            }
            index = 18;
            for (int c = 0; c < maxCtb; c++) {
                ret[index++] = ctb[c];
            }
        }
        ret[28] = (byte)(getData_len_NETLE() & 0xff);
        ret[29] = (byte)((getData_len_NETLE() >> 8) & 0xff);
        ret[30] = (byte)(getData_len_NETLE() >> 16 & 0xff);
        ret[31] = (byte)((getData_len_NETLE() >> 24) & 0xff);

        ret[32] = (byte)(getUser_reserved_NETLE() & 0xff);
        ret[33] = (byte)((getUser_reserved_NETLE() >> 8) & 0xff);
        ret[34] = (byte)(getUser_reserved_NETLE() >> 16 & 0xff);
        ret[35] = (byte)((getUser_reserved_NETLE() >> 24) & 0xff);
        return ret;
    }

    public int length() {
        return LENGTH_BYTES;
    }
}
