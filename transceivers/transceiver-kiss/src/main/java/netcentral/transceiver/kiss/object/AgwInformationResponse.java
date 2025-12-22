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
