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
