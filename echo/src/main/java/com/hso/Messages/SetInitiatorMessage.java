package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class SetInitiatorMessage implements Sendable, Receivable {

    private final Peer receiver;

    public SetInitiatorMessage(Peer receiver) {
        this.receiver = new Peer(receiver);
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"setInitiator\"}";
        return request.getBytes();
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }
}
