package com.hso.Controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
        System.out.println("-------------------------------------------------");
        System.out.println("Controller app started.");
        printNodesAndEdgesCountFromNetworkFile();
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
        System.out.println("-------------------------------------------------");
        System.out.println("Result received, sum of storage in network is: " + resultMessage.getStorageSum());
        System.out.println("Count exchanged info messages: " + this.exchangedEchoInfoMessagesCounter.getCountInfo());
        System.out.println("Count exchanged echo messages: " + this.exchangedEchoInfoMessagesCounter.getCountEcho());
    }

    public void handleLogMessage(Receivable receivedMessage) {
        LogMessage logMessage = (LogMessage)receivedMessage;
        String messageToPrint = "Log received: [ Timestamp: " + logMessage.getTimeStamp() + 
            ", Start was: " + logMessage.getStart().getIPAddress() + ":" + logMessage.getStart().getPort() +
            ", Target was: " + logMessage.getTarget().getIPAddress() + ":" + logMessage.getTarget().getPort() +
            ", Message type was: " + logMessage.getReceivedMessageType().toString();
        if (logMessage.getReceivedMessageType() == EMessageType.Echo) {
            messageToPrint += ", Storage sum was: " + logMessage.getStorageSum() + " ]";
            this.exchangedEchoInfoMessagesCounter.incrementCountEcho();
        } else {
            messageToPrint += " ]";
            this.exchangedEchoInfoMessagesCounter.incrementCountInfo();
        }
        System.out.println(messageToPrint);
    }

    private void printNodesAndEdgesCountFromNetworkFile() {
        int nodeCount = 0;
        List<Edge> edgeList = new ArrayList<Edge>();

        try {
            FileReader reader = new FileReader(controllerAppInfo.getPathToNetworkFile());
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();

            JSONArray nodesJSONArray = peerFileAsJSONObject.getJSONArray("nodes");

            // For each node in the json array, create a bidirectional edge if not existing
            for (Object node : nodesJSONArray) {
                JSONObject nodeJSONObject = (JSONObject)node;
                String nodeIpPort = nodeJSONObject.getString("ipPort");
                for (Object directNeighbor : nodeJSONObject.getJSONArray("direct_neighbors")) {
                    JSONObject directNeighborJSONObject = (JSONObject)directNeighbor;
                    String directNeighborIpPort = directNeighborJSONObject.getString("ipPort");

                    Edge currentEdge = new Edge(nodeIpPort, directNeighborIpPort);
                    if (!edgeList.contains(currentEdge)) {
                        edgeList.add(currentEdge);
                    }
                }
                nodeCount++;
            }

            System.out.println("-------------------------------------------------");
            System.out.println("Count of nodes: " + nodeCount);
            System.out.println("Count of edges: " + edgeList.size());

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void exit() {
        running = false;
    }
}
