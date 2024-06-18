package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class EchoMessage implements Sendable, Receivable {

    private final Peer sender;
    private final Peer receiver;

    private final int storageSum;

    public EchoMessage(int storageSum, Peer sender, Peer receiver) {
        this.sender = new Peer(sender);
        this.receiver = new Peer(receiver);

        this.storageSum = storageSum;
    }

    public int getStorageSum() {
        return this.storageSum;
    }

    public Peer getSender() {
        return sender;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"echo\"" +
            "," + 
            "\"sender\":\"" + sender.getIPAddress() + ":" + sender.getPort() + "\"" +
            "," + 
            "\"storageSum\":" + storageSum + "}";
        return request.getBytes();
    }

    @Override
    public Peer getReceiver() {
        return receiver;
    }

    @Override
    public EMessageType getMessageType() {
        return EMessageType.Echo;
    }
}
