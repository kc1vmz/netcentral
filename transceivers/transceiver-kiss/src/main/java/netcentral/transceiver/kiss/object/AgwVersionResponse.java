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
