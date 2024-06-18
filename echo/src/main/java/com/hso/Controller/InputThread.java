package com.hso.Controller;

import java.util.Scanner;

import com.hso.Messages.SetInitiatorMessage;
import com.hso.Peer.Peer;
import com.hso.Sending.SendingQueue;

/**
 * @author Aaron Moser
 */

public class InputThread extends Thread {

    private final SendingQueue sendingQueue;

    public InputThread(SendingQueue sendingQueue) {
        this.sendingQueue = sendingQueue;
    }

    public void run() {
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("-------------------------------------------------");
        System.out.println("Enter ip:port of start node:");
        String inputString = null;
        try {
            inputString = inputScanner.nextLine();
        } catch (Exception e) {
            System.err.println("Error at InputThread:run, unable to resolve command from input scanner.");
        }
        if ((inputString != null) && (!inputString.equals(""))) {
            sendingQueue.add(new SetInitiatorMessage(new Peer(inputString)));
            System.out.println("-------------------------------------------------");
            System.out.println("Node at " + inputString + " was set as initiator node.");
            System.out.println("-------------------------------------------------");
        }
        inputScanner.close();
    }
}
