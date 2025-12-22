package netcentral.transceiver.kiss.object;

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

public class AgwCommand {
    private static final int AX25_MAX_PACKET_LEN = 10*7+2+4+2048; // ( AX25_MAX_ADDRS 10  * 7 + 2 + 3 + AX25_MAX_INFO_LEN 2048)
    private AgwHeader header;
    String data = "";
    int length = 0;

    public static final int getMaxPacketSize(){
        return AX25_MAX_PACKET_LEN;
    }

    public AgwCommand(byte portx, byte datakind, String call_from, String call_to, String data) {
        this.header = new AgwHeader(portx, datakind, call_from, call_to);
        this.data = data;
        this.header.setData_len_NETLE(0);
        if (this.data != null) {
            this.header.setData_len_NETLE(this.data.length());
        }
    }

    public AgwCommand(byte portx, byte datakind, byte[] header, byte [] data) {
        this.header = new AgwHeader(portx, datakind, header);
        this.data = new String(data);
        this.header.setData_len_NETLE(0);
        if (this.data != null) {
            this.header.setData_len_NETLE(this.data.length());
        }
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
        int arrayLen = header.length();
        if (data != null) {
            arrayLen += data.length();
        }

        // vpacket 
        arrayLen += 21;

        byte[] ret = new byte[arrayLen]; 
        for (int c = 0; c < ret.length; c++) {
            ret[c] = 0;
        }
        int index = 0;
        byte[] headerb = header.getBytes();
        for (index = 0; index < headerb.length; index++) {
            ret[index] = headerb[index];
        }
    
/*
 * 
 */
        // lets make this a V packet
        ret[index] = 1;
        byte [] digi = "W1DMR-7  ,W1DMR-3   ".getBytes();
        for (int c = 0; c < digi.length; c++) {
            ret[index+1+c] = digi[c];
        }
        index += 21;
/*
 * 
 */
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

    public int length() {
        return this.length;
    }
}
