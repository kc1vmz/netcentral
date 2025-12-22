package netcentral.transceiver.agw.object;

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

/*
 * struct agw_cmd_s {		

  struct agw_hdr_s hdr;			
  char data[AX25_MAX_PACKET_LEN];	
};
 */

import java.util.List;

public class AgwCommand {
    private static final int AX25_MAX_PACKET_LEN = 10*7+2+4+2048; // ( AX25_MAX_ADDRS 10  * 7 + 2 + 3 + AX25_MAX_INFO_LEN 2048)
    private AgwHeader header;
    String data = "";
    int length = 0;
    private List<String> digipeaters = null;

    public static final int getMaxPacketSize(){
        return AX25_MAX_PACKET_LEN;
    }

    public AgwCommand(byte portx, byte datakind, String call_from, String call_to, String data, List<String> digipeaters) {
        this.header = new AgwHeader(portx, datakind, call_from, call_to);
        this.data = data;
        this.header.setData_len_NETLE(0);
        if (this.data != null) {
            this.header.setData_len_NETLE(this.data.length());
        }
        this.digipeaters = digipeaters;
    }

    public AgwCommand(byte portx, byte datakind, byte[] header, byte [] data) {
        this.header = new AgwHeader(portx, datakind, header);
        this.data = new String(data);
        this.header.setData_len_NETLE(0);
        if (this.data != null) {
            this.header.setData_len_NETLE(this.data.length());
        }
        this.digipeaters = null;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getBytes() {
        int arrayLen = header.length();
        if (data != null) {
            arrayLen += data.length();
        }

        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }
        int index = 0;
        byte[] headerb = header.getBytes();
        for (index = 0; index < headerb.length; index++) {
            ret[index] = headerb[index];
        }
    
        this.length = header.length();
        if (data != null) {
            byte[] datab = data.getBytes();
            int max = AX25_MAX_PACKET_LEN;
            if (datab.length < max) {
                max = datab.length;
            }

            for (int c = 0; c < max; c++) {
                ret[index++] = datab[c];
            }
            this.length += max;
        }

        return ret;
    } 

    public byte[] getBytesV() {
        this.header.setData_len_NETLE(this.data.length()+1);  // fake the header to have one more byte for pre-pended code

        byte[] headerb = header.getBytes();

        int arrayLen = headerb.length+20+1; // AX.25 header + APRS packet constants; must add digis and data
        if (data != null) {
            arrayLen += data.length();
        } else {
            arrayLen++; // must always have one byte
        }

        digipeaters = null;

        if (digipeaters != null) {
            arrayLen += (7 * digipeaters.size());
        }

        // Clear buffer for sure
        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }

        int index = 0;
        // write header into buffer
        for (index = 0; index < headerb.length; index++) {
            ret[index] = headerb[index];
        }
        //
        // start on APRS part
        // flag
        ret[index++] = 0 ; // Flag
        // des address
        byte [] dest_bytes = this.header.getCall_to().getBytes();
        for (int c = 0; c < this.header.getCall_to().length() && c < 7; c++) {
            ret[index+c] = dest_bytes[c];
        }
        index += 7;

        // src address
        byte [] src_bytes = this.header.getCall_from().getBytes();
        int k = 0;
        for (; k < this.header.getCall_from().length() && k < 6; k++) {
            ret[index+k] = (byte) (src_bytes[k] << 1);
        }
        for ( ; k < 6; k++) {
             ret[index+k] = (0x40); // space shifted left 1
        }
        ret[index+k] = 1;
        
        index += 7;

        // digipeaters
        if (digipeaters != null) {
            for (String digipeater : digipeaters) {
                byte [] digi_bytes = digipeater.getBytes();
                for (int c = 0; c < digi_bytes.length && c < 7; c++) {
                    ret[index+c] = digi_bytes[c];
                }
                index += 7;
            }
        }

        // control field
        ret[index++] = 3 ; // Flag
        ret[index++] = (byte) 240; // Protocol
//        ret[index++] = 0x0; // Kiss command byte - why?  present in YAAC

        // data
        if (data != null) {
            byte[] datab = data.getBytes();
            for (int c = 0; c < datab.length && c < 256; c++) {
                ret[index+c] = datab[c];
            }
            index += datab.length;
        } else {
            ret[index++] = ','; // invalid DTI
        }

        // END
        ret[index++] = 0; // FCS ??
        ret[index++] = 0; // FCS ??
        ret[index++] = 0; // end

        return ret;
    } 

    public int length() {
        return this.length;
    }

    public List<String> getDigipeaters() {
        return digipeaters;
    }

    public void setDigipeaters(List<String> digipeaters) {
        this.digipeaters = digipeaters;
    }
}
