package com.kc1vmz.netcentral.parser.util;

public class MicELonMinuteProcessor {
    private int value;

    public MicELonMinuteProcessor(byte input) {
        if ((input >= 88) && (input <= 97)) {
            value = input - 88;
        } else if ((input >= 38) && (input <= 87)) {
            value = (input - 38)+10;
        }
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}
