package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class InfoMessage implements Sendable, Receivable {

    private final Peer receiver;

    private Peer parent = null;

    public InfoMessage(Peer parent, Peer receiver) {
        this.receiver = new Peer(receiver);

        this.parent = new Peer(parent);
    }

    public Peer getParent() {
        return this.parent;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"info\"" +
            "," + 
            "\"parent\":\"" + parent.getIPAddress() + ":" + parent.getPort() + "\"}";
        return request.getBytes();
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.Info;
    }
}
