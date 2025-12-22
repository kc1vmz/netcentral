package com.kc1vmz.netcentral.parser.util;

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
