package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public final class LogMessage implements Sendable, Receivable {

    private final Peer receiver;

    private final String timestamp;
    private final Peer start;
    private final Peer target;
    private final EMessageType messageType;
    private final int storageSum;

    public LogMessage(String timestamp, Peer start, Peer target, EMessageType messageType, Peer receiver, int storageSum) {
        this.receiver = new Peer(receiver);

        this.timestamp = timestamp;
        this.start = new Peer(start);
        this.target = new Peer(target);
        this.messageType = messageType;
        this.storageSum = storageSum;
    }

    public String getTimeStamp() {
        return this.timestamp;
    }

    public Peer getStart() {
        return this.start;
    }

    public Peer getTarget() {
        return this.target;
    }

    public EMessageType getReceivedMessageType() {
        return this.messageType;
    }

    public int getStorageSum() {
        return this.storageSum;
    }

    @Override
    public byte[] getMessage() {
        String request =
            "{\"messageType\":\"log\"" +
            "," + 
            "\"timestamp\":\"" + timestamp + "\"" +
            "," + 
            "\"start\":\"" + start.getIPAddress() + ":" + start.getPort() + "\"" +
            "," + 
            "\"target\":\"" + target.getIPAddress() + ":" + target.getPort() + "\"" +
            "," + 
            "\"receivedMessageType\":\"" + messageType.toString() + "\"" + 
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
        return EMessageType.Log;
    }
}
