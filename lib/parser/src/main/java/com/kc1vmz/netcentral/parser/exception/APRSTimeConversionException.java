package com.kc1vmz.netcentral.parser.exception;

public class APRSTimeConversionException extends Exception {

    public APRSTimeConversionException(String message) {
        super(message);
    }
    public APRSTimeConversionException(String message, Exception e) {
        super(message, e);
    }

}
