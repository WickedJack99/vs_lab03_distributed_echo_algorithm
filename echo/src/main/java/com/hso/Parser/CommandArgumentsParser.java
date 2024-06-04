package com.hso.Parser;

import com.hso.AppInfo.AppInfo;
import com.hso.AppInfo.EAppType;

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
        return new AppInfo(EAppType.Controller);
    }

    private static AppInfo createNodeInfo(String[] args) {
        int direct_neighbors_count = args.length - 4;
        return new AppInfo(EAppType.Node);
    }
}
