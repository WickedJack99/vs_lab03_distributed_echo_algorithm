package com.hso.Peer;

/**
 * @author Aaron Moser
 */

public class Peer {

    private final String ipAddress;
    private final int port;

    public Peer(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public Peer(Peer other) {
        if (other != null) {
            this.ipAddress = other.ipAddress;
            this.port = other.port;
        } else {
            this.ipAddress = null;
            this.port = 0;
        }
    }

    public String getIPAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean equals(Peer other) {
        return this.ipAddress.equals(other.ipAddress) && (this.port == other.port);
    }
}
