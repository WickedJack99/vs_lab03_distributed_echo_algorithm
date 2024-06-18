package com.hso.Receiving;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Aaron Moser
 */

public class Receiver extends Thread {

    private volatile boolean running = false;
    private final DatagramSocket datagramSocket;
    private final ReceivingQueue receivingQueue;

    public Receiver(DatagramSocket datagramSocket, ReceivingQueue receivingQueue) {
        this.datagramSocket = datagramSocket;
        this.receivingQueue = receivingQueue;
    }

    public void run() {
        running = true;
        DatagramPacket datagramPacket = null;

        while (running) {
            byte[] receiveBuffer = new byte[65535];
            datagramPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                this.datagramSocket.receive(datagramPacket);
                if (receiveBuffer != null) {
                    this.receivingQueue.add(ReceivableFactory.createReceivableFromByteData(receiveBuffer));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
