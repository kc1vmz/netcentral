package com.kc1vmz.netcentral.parser.util;

public class MicELonDegreeProcessor {
    private int value;

    public MicELonDegreeProcessor(byte input, boolean applyOffset) {
        if ((applyOffset) && (input >= 118) && (input <= 127)) {
            value = input - 118;
        } else if ((!applyOffset) && (input >= 38) && (input <= 127)) {
            value = (input - 38)+10;
        } else if ((applyOffset) && (input >= 108) && (input <= 117)) {
            value = (input - 108) + 100;
        } else if ((applyOffset) && (input >= 38) && (input <= 107)) {
            value = (input - 38) + 110;
        }
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
