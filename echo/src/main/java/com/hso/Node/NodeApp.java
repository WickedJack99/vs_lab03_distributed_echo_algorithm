package com.hso.Node;

import com.hso.AppInfo.AppInfo;
import com.hso.Messages.EMessageType;
import com.hso.Messages.EchoMessage;
import com.hso.Messages.InfoMessage;
import com.hso.Messages.LogMessage;
import com.hso.Messages.Receivable;
import com.hso.Messages.ResultMessage;
import com.hso.Peer.Peer;
import com.hso.Receiving.Receiver;
import com.hso.Receiving.ReceivingQueue;
import com.hso.Sending.SenderThread;
import com.hso.Sending.SendingQueue;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Aaron Moser
 */

public class NodeApp extends Thread {

    private final AppInfo nodeAppInfo;
    private final SendingQueue sendingQueue;
    private final ReceivingQueue receivingQueue;
    private volatile boolean running = false;

    private boolean informed = false;
    private boolean initiator = false;
    private int neighborsInformed = 0;
    private int storageSum = 0;
    private Peer parent = null;

    public NodeApp(AppInfo appInfo, SendingQueue sendingQueue, ReceivingQueue receivingQueue) {
        this.nodeAppInfo = appInfo;
        this.sendingQueue = sendingQueue;
        this.receivingQueue = receivingQueue;
        this.storageSum = nodeAppInfo.getStorage();
    }

    public void run() {

        try {
            InetAddress ipAddress = InetAddress.getByName(nodeAppInfo.getNodeNetworkInformation().getIPAddress());
            int port = nodeAppInfo.getNodeNetworkInformation().getPort();
            System.out.println(ipAddress + " " + port);
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
                    case Info: {
                        sleep(nodeAppInfo.getReceiveLatency());
                        handleInfoMessage(receivedMessage);
                    }break;

                    case Echo: {
                        sleep(nodeAppInfo.getReceiveLatency());
                        handleEchoMessage(receivedMessage);
                    }break;                 
                
                    case SetInitiator: {
                        handleSetInitiatorMessage(receivedMessage);
                    }break;

                    default: {
                        System.err.println("Error, unknown message type received in node app.");
                    }break;
                }
            } catch (InterruptedException e) {
                System.err.println("Receiving queue was interrupted.");
            }
        }
    }

    private void handleInfoMessage(Receivable receivedMessage) {
        InfoMessage infoMessage = (InfoMessage)receivedMessage;
        sendLogForInfoMessage(infoMessage);
        neighborsInformed++;
        if (!informed) {
            informed = true;
            parent = infoMessage.getParent();
            for (Peer neighbor : nodeAppInfo.getNetworkNodes()) {
                if (!neighbor.equals(parent)) {
                    sendingQueue.add(new InfoMessage(nodeAppInfo.getNodeNetworkInformation(), neighbor));
                }
            }
        }
        if (neighborsInformed == nodeAppInfo.getNetworkNodes().size()) {
            if (!initiator) {
                sendingQueue.add(new EchoMessage(storageSum, nodeAppInfo.getNodeNetworkInformation(), parent));
            }
        }
    }

    private void sendLogForInfoMessage(InfoMessage infoMessage) {
        sendingQueue.add(
            new LogMessage(
                getCurrentTimestamp(), 
                infoMessage.getParent(), 
                nodeAppInfo.getNodeNetworkInformation(), 
                EMessageType.Info, 
                nodeAppInfo.getControllerNetworkInformation(),
                0
            )
        );
    }

    private void handleEchoMessage(Receivable receivedMessage) {
        EchoMessage echoMessage = (EchoMessage)receivedMessage;
        sendLogForEchoMessage(echoMessage);
        neighborsInformed++;
        storageSum += echoMessage.getStorageSum();
        if (neighborsInformed == nodeAppInfo.getNetworkNodes().size()) {
            if (initiator) {
                sendingQueue.add(new ResultMessage(storageSum, nodeAppInfo.getControllerNetworkInformation()));
            } else {
                sendingQueue.add(new EchoMessage(storageSum, nodeAppInfo.getNodeNetworkInformation(), parent));
            }
        }
    }

    private void sendLogForEchoMessage(EchoMessage echoMessage) {
        sendingQueue.add(
            new LogMessage(
                getCurrentTimestamp(), 
                echoMessage.getSender(), 
                nodeAppInfo.getNodeNetworkInformation(), 
                EMessageType.Echo, 
                nodeAppInfo.getControllerNetworkInformation(),
                echoMessage.getStorageSum()
            )
        );
    }

    private void handleSetInitiatorMessage(Receivable receivedMessage) {
        initiator = true;
        for (Peer neighbor : nodeAppInfo.getNetworkNodes()) {
            sendingQueue.add(new InfoMessage(nodeAppInfo.getNodeNetworkInformation(), neighbor));
        }
    }

    public String getCurrentTimestamp() {
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        
        int day = currentDate.getDayOfMonth();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        int hour = currentDateTime.getHour();
        int minute = currentDateTime.getMinute();
        int second = currentDateTime.getSecond();
        int milliSecond = currentDateTime.getNano() / 1_000_000;
        
        return String.format("%02d.%02d.%04d-%02d:%02d:%02d:%03d", day, month, year, hour, minute, second, milliSecond);
    }

    public void reset() {
        informed = false;
        initiator = false;
    }

    public void exit() {
        running = false;
    }
}
