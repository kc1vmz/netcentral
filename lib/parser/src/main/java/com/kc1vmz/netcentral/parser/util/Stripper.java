package com.kc1vmz.netcentral.parser.util;

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
                break;
            }
        }
        return valStripped;
    }
}
