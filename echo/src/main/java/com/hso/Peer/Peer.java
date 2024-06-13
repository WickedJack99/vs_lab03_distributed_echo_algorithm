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

    /**
     * Copy constructor.
     * @param other
     */
    public Peer(Peer other) {
        if (other != null) {
            this.ipAddress = other.ipAddress;
            this.port = other.port;
        } else {
            this.ipAddress = null;
            this.port = 0;
        }
    }

    public Peer(String ipPort) {
        String[] ipPortArray = ipPort.split(":");
        this.ipAddress = ipPortArray[0];
        this.port = Integer.valueOf(ipPortArray[1]);
    }

    public static Peer empty() {
        return new Peer("", -1);
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
