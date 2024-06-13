package com.hso.Controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.hso.AppInfo.AppInfo;
import com.hso.Messages.EMessageType;
import com.hso.Messages.LogMessage;
import com.hso.Messages.Receivable;
import com.hso.Messages.ResultMessage;
import com.hso.Receiving.Receiver;
import com.hso.Receiving.ReceivingQueue;
import com.hso.Sending.SenderThread;
import com.hso.Sending.SendingQueue;

/**
 * @author Aaron Moser
 */

public class ControllerApp extends Thread {

    private volatile boolean running = false;

    private final AppInfo controllerAppInfo;
    private final SendingQueue sendingQueue;
    private final ReceivingQueue receivingQueue;

    private final ExchangedEchoInfoMessagesCounter exchangedEchoInfoMessagesCounter;

    public ControllerApp(AppInfo appInfo, SendingQueue sendingQueue, ReceivingQueue receivingQueue) {
        this.controllerAppInfo = appInfo;
        this.sendingQueue = sendingQueue;
        this.receivingQueue = receivingQueue;
        this.exchangedEchoInfoMessagesCounter = new ExchangedEchoInfoMessagesCounter();
    }

    public void run() {
        InputThread inputThread = new InputThread(this.sendingQueue);
        inputThread.start();
        try {
            InetAddress ipAddress = InetAddress.getByName(controllerAppInfo.getControllerNetworkInformation().getIPAddress());
            int port = controllerAppInfo.getControllerNetworkInformation().getPort();
            DatagramSocket ds = new DatagramSocket(port, ipAddress);
            Receiver receiver = new Receiver(ds, receivingQueue);
            receiver.start();
            SenderThread sender = new SenderThread(ds, sendingQueue);
            sender.start();
        } catch (SocketException e1) {
            System.err.println("Socket exception.");
        } catch (UnknownHostException e2) {
            System.err.println("Unknown host.");
        }

        running = true;
        while (running) {
            try {
                Receivable receivedMessage = receivingQueue.take();
                switch (receivedMessage.getMessageType()) {
                    case Result: {
                        handleResultMessage(receivedMessage);
                    }break;

                    case Log: {
                        handleLogMessage(receivedMessage);
                    }break;
                
                    default: {
                        System.err.println("Unknown message type received.");
                    }break;
                }
            } catch (InterruptedException e) {
                System.err.println("Error in controller app, receiving queue was interrupted.");
            }
        }
    }

    public void handleResultMessage(Receivable receivedMessage) {
        ResultMessage resultMessage = (ResultMessage)receivedMessage;
        System.out.println("Result received, sum of storage in network is: " + resultMessage.getStorageSum());
        System.out.println("Count exchanged info messages: " + this.exchangedEchoInfoMessagesCounter.getCountInfo());
        System.out.println("Count exchanged echo messages: " + this.exchangedEchoInfoMessagesCounter.getCountEcho());
    }

    public void handleLogMessage(Receivable receivedMessage) {
        LogMessage logMessage = (LogMessage)receivedMessage;
        System.out.println(
            "Log received at: " + logMessage.getTimeStamp() + 
            ", Start was: " + logMessage.getStart().getIPAddress() + ":" + logMessage.getStart().getPort() +
            ", Target was: " + logMessage.getTarget().getIPAddress() + ":" + logMessage.getTarget().getPort() +
            ", Message type was: " + logMessage.getMessageType().toString());
        if (logMessage.getMessageType() == EMessageType.Echo) {
            this.exchangedEchoInfoMessagesCounter.incrementCountEcho();
        } else {
            this.exchangedEchoInfoMessagesCounter.incrementCountInfo();
        }
    }

    public void exit() {
        running = false;
    }
}
