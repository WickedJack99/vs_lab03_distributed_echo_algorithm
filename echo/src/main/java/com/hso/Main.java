package com.hso;

import com.hso.AppInfo.AppInfo;
import com.hso.Controller.ControllerApp;
import com.hso.Deployer.DeployerApp;
import com.hso.Node.NodeApp;
import com.hso.Parser.CommandArgumentsParser;
import com.hso.Receiving.ReceivingQueue;
import com.hso.Sending.SendingQueue;

/**
 * @author Aaron Moser
 */

public class Main {
    public static void main(String[] args) {

        AppInfo parsedAppInfo = CommandArgumentsParser.parseCommandString(args);
        SendingQueue sendingQueue =  new SendingQueue();
        ReceivingQueue receivingQueue = new ReceivingQueue();

        switch (parsedAppInfo.getAppType()) {

            case Controller: {
                ControllerApp controllerApp = new ControllerApp(parsedAppInfo, sendingQueue, receivingQueue);
                controllerApp.start();
                try {
                    controllerApp.join();
                    System.out.println("Controller app has stopped.");
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }break;
        
            case Node: {
                NodeApp nodeApp = new NodeApp(parsedAppInfo, sendingQueue, receivingQueue);
                nodeApp.start();
                try {
                    nodeApp.join();
                    System.out.println("Node app has stopped.");
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }break;

            case Deployer: {
                DeployerApp deployerApp = new DeployerApp();
                deployerApp.start();
                try {
                    deployerApp.join();
                    System.out.println("Deployer app has stopped.");
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }break;

            default: {
                System.err.println("Unknown app type. Aborting.");
            }break;
        }
    }
}
