package com.hso.Messages;

/**
 * @author Aaron Moser
 */

public enum EMessageType {
    Info,
    Echo,
    Log,
    SetInitiator,
    Result
    ;
    @Override
    public String toString() {
        switch (this.ordinal()) {
            case 0:
                return "info";
            case 1:
                return "echo";
            case 2:
                return "log";
            case 3:
                return "setInitiator";
            case 4:
                return "result";
            default:
                return null;
        }
    }
}
