package com.hso.Controller;

public class Edge {
    private String leftNodeIpPort = null;
    private String rightNodeIpPort = null;

    public Edge(String leftNodeIpPort, String rightNodeIpPort) {
        this.leftNodeIpPort = leftNodeIpPort;
        this.rightNodeIpPort = rightNodeIpPort;
    }

    public boolean equals(Object other) {
        Edge otherEdge = (Edge)other;
        boolean equal = this.leftNodeIpPort.equals(otherEdge.leftNodeIpPort) && this.rightNodeIpPort.equals(otherEdge.rightNodeIpPort);
        boolean inverted = this.leftNodeIpPort.equals(otherEdge.rightNodeIpPort) && this.rightNodeIpPort.equals(otherEdge.leftNodeIpPort);
        return equal || inverted;
    }
}
