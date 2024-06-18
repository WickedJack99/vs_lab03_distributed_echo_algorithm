package com.hso.Controller;

/**
 * @author Aaron Moser
 */

public class ExchangedEchoInfoMessagesCounter {
    private int countEchoMessages = 0;
    private int countInfoMessages = 0;

    public void incrementCountInfo() {
        countInfoMessages++;
    }

    public void incrementCountEcho() {
        countEchoMessages++;
    }

    public int getCountInfo() {
        return countInfoMessages;
    }

    public int getCountEcho() {
        return countEchoMessages;
    }
}
