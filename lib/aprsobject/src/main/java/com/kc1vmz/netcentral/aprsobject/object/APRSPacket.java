package com.kc1vmz.netcentral.aprsobject.object;

import java.util.List;

/*
 *  0   flag    (1 byte) 0x7e
 *  1-7  source  (7 bytes)
 *  8-14 dest    (7 bytes)
 *  15-71 digi list  (57 bytes)
 *  72  control (1 byte)  0x03
 *  73  protocol ID (1 byte) 0xf0
 *  74-(+255)  information (256 bytes)
 *     FCS (2 bytes)
 *     flag (1 byte) 0x7e
 */
public class APRSPacket {
    private boolean valid;
    private String source;
    private String destination;
    private List<String> digipeaterList;
    private byte control;
    private byte protocolId;
    private byte [] information;
    private byte [] frameCheckSequence;


    public APRSPacket() {
        setValid(false);
    }

    public APRSPacket(byte [] data) {
        if (data != null) {
            if (data[0] == 0x7e) {
                // get source
                String tSource = "";
                for (int i = 0; i < 6; i++) {
                    if (data[1+i] != 0) {
                        tSource += (char) data[1+i];
                    }
                }
                char ssIdSource = '0';
                ssIdSource += data[7];
                tSource += ("-"+ssIdSource);
                setSource(tSource);

                // get source
                String tDest = "";
                for (int i = 0; i < 6; i++) {
                    if (data[8+i] != 0) {
                        tDest += (char) data[8+i];
                    }
                }
                char ssIdDest = '0';
                ssIdDest += data[7];
                tDest += ("-"+ssIdDest);
                setDestination(tDest);


            }

/*            String call_from = "";
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

//            int dataLen = data[28] + (data[29] << 8) + (data[30] << 18) + (data[31] << 24);
//            int reserved = data[32] + (data[33] << 8) + (data[34] << 18) + (data[35] << 24);
//            int dataLen = fromByteArray( data[28], data[29], data[30], data[31]);
//            int reserved = fromByteArray( data[32], data[33], data[34], data[35]);
            int reserved = 0;
            int dataLen = data.length - AgwHeader.LENGTH_BYTES;

            setData_len_NETLE(dataLen);
            setUser_reserved_NETLE(reserved);
            setValid(true);
*/        } else {
            setValid(false);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getDigipeaterList() {
        return digipeaterList;
    }

    public void setDigipeaterList(List<String> digipeaterList) {
        this.digipeaterList = digipeaterList;
    }

    public byte getControl() {
        return control;
    }

    public void setControl(byte control) {
        this.control = control;
    }

    public byte getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(byte protocolId) {
        this.protocolId = protocolId;
    }

    public byte[] getInformation() {
        return information;
    }

    public void setInformation(byte[] information) {
        this.information = information;
    }

    public byte[] getFrameCheckSequence() {
        return frameCheckSequence;
    }

    public void setFrameCheckSequence(byte[] frameCheckSequence) {
        this.frameCheckSequence = frameCheckSequence;
    }


     
}
