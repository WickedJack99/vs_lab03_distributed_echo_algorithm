package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class EchoMessage implements Sendable, Receivable {

    private final Peer receiver;

    private final int storageSum;

    public EchoMessage(int storageSum, Peer receiver) {
        this.receiver = new Peer(receiver);

        this.storageSum = storageSum;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"echo\"" +
            "," + 
            "\"storageSum\":" + storageSum + "}";
        return request.getBytes();
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }
}
