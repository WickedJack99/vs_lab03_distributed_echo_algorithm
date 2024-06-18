package com.hso.Sending;

/**
 * @author Aaron Moser
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.hso.Messages.Sendable;

public class SenderThread extends Thread {

    private DatagramSocket ds = null;
    private SendingQueue sendingQueue = null;
    private volatile boolean senderThreadRunning = false;

    public SenderThread(DatagramSocket ds, SendingQueue sendingQueue) {
        this.ds = ds;
        this.sendingQueue = sendingQueue;
    }

    @Override
    public void run() {
        this.senderThreadRunning = true;

        while (senderThreadRunning) {
            try {
                Sendable dataToSent = sendingQueue.take();

                byte[] data = dataToSent.getMessage();

                if (data != null) {
                    String receiverIpAddress = dataToSent.getReceiver().getIPAddress();
                    int receiverPort = dataToSent.getReceiver().getPort();
                    int dataLength = data.length;

                    DatagramPacket dp =
                        new DatagramPacket(data, dataLength, InetAddress.getByName(receiverIpAddress), receiverPort);
                    ds.send(dp);
                }
            } catch (UnknownHostException e) {
                System.err.println("Error, host is unknown or unreachable.");
            } catch (IOException e) {
                System.err.println("Error, wasn't able to send data, because of problem with socket.");
            } catch (InterruptedException e) {
                System.err.println("Error, sending queue was interrupted. Terminating (this) sending thread..");
            }
        }
    }
}