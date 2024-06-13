package com.hso.AppInfo;

import java.util.List;

import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public class AppInfo {

    private final Peer node;
    private final Peer controller;
    private final int storage;
    private final List<Peer> networkNodes;
    private final EAppType appType;
    private final int receiveLatency;

    /**
     * 
     * @param appType
     * @param node
     * @param controller
     * @param storage
     * @param networkNodes
     */
    public AppInfo(EAppType appType, Peer node, Peer controller, int storage, List<Peer> networkNodes) {
        this.appType = appType;
        this.node = node;
        this.controller = controller;
        this.storage = storage;
        this.networkNodes = networkNodes;
        this.receiveLatency = (int)(Math.random() * 99) + 1;
    }

    public EAppType getAppType() {
        return appType;
    }

    public Peer getNodeNetworkInformation() {
        return this.node;
    }

    public Peer getControllerNetworkInformation() {
        return this.controller;
    }

    public int getStorage() {
        return this.storage;
    }

    public List<Peer> getNetworkNodes() {
        return networkNodes;
    }

    public int getReceiveLatency() {
        return receiveLatency;
    }
}
