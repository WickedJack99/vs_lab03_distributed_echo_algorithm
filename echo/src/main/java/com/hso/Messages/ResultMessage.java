package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class ResultMessage implements Sendable, Receivable {
    
    private final Peer receiver;

    private final int storageSum;

    public ResultMessage(int storageSum, Peer receiver) {
        this.receiver = receiver;

        this.storageSum = storageSum;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"result\"" +
            "," + 
            "\"storageSum\":" + storageSum + "}";
        return request.getBytes();
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }
}
