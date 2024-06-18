package com.hso.Messages;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public interface Sendable {
    public Peer getReceiver();
    public byte[] getMessage();
}
