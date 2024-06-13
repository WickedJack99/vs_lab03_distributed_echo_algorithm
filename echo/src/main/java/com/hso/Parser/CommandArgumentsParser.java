package com.hso.Parser;

import java.util.ArrayList;
import java.util.List;

import com.hso.AppInfo.AppInfo;
import com.hso.AppInfo.EAppType;
import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public class CommandArgumentsParser {

    public static AppInfo parseCommandString(String[] args) {
        String mode = args[0];

        if (mode.equals("controller")) {
            return createControllerInfo(args);
        } else if (mode.equals("node")) {
            return createNodeInfo(args);
        } else {
            System.out.println("Error, unknown mode. Aborting.");
            return null;
        }
    }

    private static AppInfo createControllerInfo(String[] args) {
        Peer controllerNetworkInformation = new Peer(args[1]);
        return new AppInfo(EAppType.Controller, null, controllerNetworkInformation, 0, null);
    }

    private static AppInfo createNodeInfo(String[] args) {
        Peer nodeNetworkInformation = new Peer(args[1]);
        int storage = Integer.valueOf(args[2]);
        Peer controllerNetworkInformation = new Peer(args[3]);
        List<Peer> networkNodes = new ArrayList<Peer>(args.length - 4);
        for (int i = 4; i < args.length; i++) {
            networkNodes.add(new Peer(args[i]));
        }
        return new AppInfo(EAppType.Node, nodeNetworkInformation, controllerNetworkInformation, storage, networkNodes);
    }
}
