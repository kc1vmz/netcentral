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

public class AgwFrameType {
    public static final char GET_VERSION = 'R';
    public static final char REGISTER_CALLSIGN = 'X';
    public static final char UNREGISTER_CALLSIGN = 'x';
    public static final char GET_PORT = 'G';
    public static final char GET_PORT_CAPABILITIES = 'g';
    public static final char GET_HEARD_CALLSIGNS = 'H';
    public static final char ENABLE_RECEPTION = 'm';
    public static final char INFORMATION = 'T';
    public static final char ENABLE_RAW_RECEPTION = 'k';
}
