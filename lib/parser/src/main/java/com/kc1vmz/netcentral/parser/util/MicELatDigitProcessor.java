package com.kc1vmz.netcentral.parser.util;

public class MicELatDigitProcessor {
    private boolean isNorth;
    private boolean isWest;
    private boolean hasComment;
    private boolean isStandardComment;
    private int longOffset;
    private char digit;
    private boolean hasDirection;


    public MicELatDigitProcessor(char input) {
        // yeah, ugly but effective
        if ((input >= '0') && (input <= '9')) {
            digit = input;
            hasComment = false;
            hasDirection = true;
            isNorth = false;
            isWest = false;
            longOffset = 0;
        } else if ((input >= 'A') && (input <= 'J')) {
            int tv = input;
            tv = input - (int) 'A';
            tv += (int) '0';
            digit = (char) tv;
            hasComment = true;
            isStandardComment = false;
            hasDirection = false;
        } else if (input == 'K') {
            digit = ' ';
            hasComment = true;
            isStandardComment = false;
            hasDirection = false;
            longOffset = 0;
        } else if (input == 'L') {
            digit = ' ';
            hasComment = false;
            hasDirection = true;
            isNorth = false;
            isWest = false;
        } else if ((input >= 'P') && (input <= 'Y')) {
            int tv = input;
            tv = input - (int) 'P';
            tv += (int) '0';
            digit = (char) tv;
            hasComment = true;
            isStandardComment = true;
            hasDirection = true;
            isNorth = true;
            isWest = true;
            longOffset = 100;
        } else if (input == 'Z') {
            digit = ' ';
            hasComment = true;
            isStandardComment = true;
            hasDirection = true;
            isNorth = true;
            isWest = true;
            longOffset = 100;
        }
    }

    public boolean isNorth() {
        return isNorth;
    }
    public void setNorth(boolean isNorth) {
        this.isNorth = isNorth;
    }
    public boolean isWest() {
        return isWest;
    }
    public void setWest(boolean isWest) {
        this.isWest = isWest;
    }
    public char getDigit() {
        return digit;
    }
    public void setDigit(char digit) {
        this.digit = digit;
    }
    public boolean isStandardComment() {
        return isStandardComment;
    }
    public void setStandardComment(boolean isStandardComment) {
        this.isStandardComment = isStandardComment;
    }
    public int getLongOffset() {
        return longOffset;
    }
    public void setLongOffset(int longOffset) {
        this.longOffset = longOffset;
    }
    public boolean isHasComment() {
        return hasComment;
    }
    public void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }
    public boolean isHasDirection() {
        return hasDirection;
    }
    public void setHasDirection(boolean hasDirection) {
        this.hasDirection = hasDirection;
    }
}
