package com.hso;

import com.hso.AppInfo.AppInfo;
import com.hso.Controller.ControllerApp;
import com.hso.Node.NodeApp;
import com.hso.Parser.CommandArgumentsParser;

/**
 * @author Aaron Moser
 */

public class Main {
    public static void main(String[] args) {

        AppInfo parsedAppInfo = CommandArgumentsParser.parseCommandString(args);

        switch (parsedAppInfo.getAppType()) {

            case Controller: {
                ControllerApp controllerApp = new ControllerApp(parsedAppInfo);
                controllerApp.start();
                try {
                    controllerApp.join();
                    System.out.println("Controller app has stopped.");
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }break;
        
            case Node: {
                NodeApp nodeApp = new NodeApp(parsedAppInfo);
                nodeApp.start();
                try {
                    nodeApp.join();
                    System.out.println("Node app has stopped.");
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }break;

            default: {
                System.out.println("Unknown app type. Aborting.");
            }break;
        }
    }
}
