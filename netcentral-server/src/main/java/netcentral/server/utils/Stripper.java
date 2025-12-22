package netcentral.server.utils;

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

public class Stripper {

    public static String stripWhitespace(String val) {
        String valStripped = val.strip();
        while (true) {
            int lenStripped = valStripped.length();
            if (lenStripped == 0) {
                break;
            }
            if (Character.isWhitespace(valStripped.charAt(lenStripped-1))) {
                // is there whitespace at the end?
                valStripped = valStripped.substring(0, lenStripped-1);
            } else if (Character.isSpaceChar(valStripped.charAt(lenStripped-1))) {
                // is there a space at the end?
                valStripped = valStripped.substring(0, lenStripped-1);
            } else if (valStripped.charAt(lenStripped-1) == 0) {
                valStripped = valStripped.substring(0, lenStripped-1);
            } else {
                valStripped = val;
                break;
            }
        }
        return valStripped;
    }
}
