package com.hso.Receiving;

import org.json.JSONObject;

import com.hso.Messages.EMessageType;
import com.hso.Messages.EchoMessage;
import com.hso.Messages.InfoMessage;
import com.hso.Messages.LogMessage;
import com.hso.Messages.Receivable;
import com.hso.Messages.ResultMessage;
import com.hso.Messages.SetInitiatorMessage;
import com.hso.Peer.Peer;

/**
 * @author Aaron Moser
 */

public class ReceivableFactory {
    public static Receivable createReceivableFromByteData(byte[] byteData) {
        //System.out.println(data(byteData));
        JSONObject receivedDataAsJSONObject = new JSONObject(data(byteData).toString());
        switch (receivedDataAsJSONObject.getString("messageType")) {

            case "info": {
                Peer parent = new Peer(receivedDataAsJSONObject.getString("parent"));
                return new InfoMessage(parent, Peer.empty());
            }

            case "echo": {
                int storageSum = receivedDataAsJSONObject.getInt("storageSum");
                Peer sender = new Peer(receivedDataAsJSONObject.getString("sender"));
                return new EchoMessage(storageSum, sender, Peer.empty());
            }

            case "log": {
                String timestamp = receivedDataAsJSONObject.getString("timestamp");
                Peer start = new Peer(receivedDataAsJSONObject.getString("start"));
                Peer target = new Peer(receivedDataAsJSONObject.getString("target"));
                int storageSum = receivedDataAsJSONObject.getInt("storageSum");
                EMessageType messageType = 
                    parseMessageType(receivedDataAsJSONObject.getString("receivedMessageType"));
                return new LogMessage(timestamp, start, target, messageType, Peer.empty(), storageSum);
            }

            case "setInitiator": {
                return new SetInitiatorMessage(Peer.empty());
            }

            case "result": {
                int storageSum = receivedDataAsJSONObject.getInt("storageSum");
                return new ResultMessage(storageSum, Peer.empty());
            }
        
            default: {
                System.err.println("Unknown message received.");
            }break;
        } ;
        return null;
    }

    /**
     * Source: https://www.geeksforgeeks.org/working-udp-datagramsockets-java/
     * @param a the byte array to build a String out of it.
     * @return a StringBuilder containing the data to build a String.
     */
    private static StringBuilder data(byte[] a) { 
        if (a == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    }

    private static EMessageType parseMessageType(String messageTypeString) {
        switch (messageTypeString) {
            case "info":
                return EMessageType.Info;
            case "echo":
                return EMessageType.Echo;
            case "log":
                return EMessageType.Log;
            case "setInitiator":
                return EMessageType.SetInitiator;
            case "result":
                return EMessageType.Result;
            default:
                return null;
        }
    }
}
