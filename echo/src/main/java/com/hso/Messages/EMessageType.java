package com.hso.Messages;

/**
 * @author Aaron Moser
 */

public enum EMessageType {
    Info,
    Echo
    ;
    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "info";
            case 1:
                return "echo";
            default:
                return null;
        }
    }
}
