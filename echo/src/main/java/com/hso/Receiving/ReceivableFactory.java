package com.hso.Receiving;

import org.json.JSONException;
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
        JSONObject receivedDataAsJSONObject = interpretAsJSONObject(byteData);
        switch (receivedDataAsJSONObject.getString("messageType")) {

            case "info": {
                Peer parent = new Peer(receivedDataAsJSONObject.getString("parent"));
                return new InfoMessage(parent, Peer.empty());
            }

            case "echo": {
                int storageSum = Integer.parseInt(receivedDataAsJSONObject.getString("storageSum"));
                Peer sender = new Peer(receivedDataAsJSONObject.getString("sender"));
                return new EchoMessage(storageSum, sender, Peer.empty());
            }

            case "log": {
                String timestamp = receivedDataAsJSONObject.getString("timestamp");
                Peer start = new Peer(receivedDataAsJSONObject.getString("start"));
                Peer target = new Peer(receivedDataAsJSONObject.getString("target"));
                EMessageType messageType = 
                    parseMessageType(receivedDataAsJSONObject.getString("receivedMessageType"));
                return new LogMessage(timestamp, start, target, messageType, Peer.empty());
            }

            case "setInitiator": {
                return new SetInitiatorMessage(Peer.empty());
            }

            case "result": {
                int storageSum = Integer.parseInt(receivedDataAsJSONObject.getString("storageSum"));
                return new ResultMessage(storageSum, Peer.empty());
            }
        
            default: {
                System.err.println("Unknown message received.");
            }break;
        } ;
        return null;
    }

    private static JSONObject interpretAsJSONObject(byte[] byteData) {
        try {
            JSONObject receivedDataAsJSONObject = null;
            receivedDataAsJSONObject = new JSONObject(byteData);
            return receivedDataAsJSONObject;
        } catch (JSONException e) {
            System.out.println(byteData);
            System.err.println("File is a json file, not able to parse it.");
        }
        return null;
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
