package com.hso.Deployer;

import java.io.BufferedReader;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.hso.Peer.Peer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @author Aaron Moser
 */

public class DeployerApp extends Thread {

    private boolean running = false;

    private String pathToNetworkConfigFile = null;
    private String pathToJar = null;
    private String pathToPrivateKey = null;
    private String destinationPathToJar = null;

    public void run() {
        running = true;
        Scanner inputScanner = new Scanner(System.in);
        System.out.println("-------------------------------------------------");
        System.out.println("Started echo algorithm deployer.");
        setPathToJar(inputScanner);
        setPathToNetworkConfigFile(inputScanner);
        setPathToPrivateKey(inputScanner);
        setDestinationPathToJar(inputScanner);
        createProcesses2();
        inputScanner.close();
    }

    public void terminate() {
        running = false;
    }

    private void setPathToNetworkConfigFile(Scanner inputScanner) {
        System.out.println("-------------------------------------------------");
        System.out.println("Enter path to network config file: ");
        String inputString = null;
        try {
            inputString = inputScanner.nextLine();
        } catch (Exception e) {
            System.err.println(
                "Error at DeployerApp:setPathToNetworkConfigFile, unable to resolve command from input scanner.");
        }
        if ((inputString != null) && (!inputString.equals(""))) {
            File f = new File(inputString);
            if(f.exists() && !f.isDirectory()) { 
                pathToNetworkConfigFile = inputString;
            } else {
                System.err.println("Error, file doesn't exist xor is directory.");
                setPathToNetworkConfigFile(inputScanner);
            }
        }
    }

    private void setPathToJar(Scanner inputScanner) {
        System.out.println("-------------------------------------------------");
        System.out.println("Enter path to jar file: ");
        String inputString = null;
        try {
            inputString = inputScanner.nextLine();
        } catch (Exception e) {
            System.err.println(
                "Error at DeployerApp:setPathToJar, unable to resolve command from input scanner.");
        }
        if ((inputString != null) && (!inputString.equals(""))) {
            File f = new File(inputString);
            if(f.exists() && !f.isDirectory()) { 
                pathToJar = inputString;
            } else {
                System.err.println("Error, file doesn't exist xor is directory.");
                setPathToJar(inputScanner);
            }
        }
    }

    private void setDestinationPathToJar(Scanner inputScanner) {
        System.out.println("-------------------------------------------------");
        System.out.println("Enter destination path for jar file: ");
        String inputString = null;
        try {
            inputString = inputScanner.nextLine();
        } catch (Exception e) {
            System.err.println(
                "Error at DeployerApp:setDestinationPathToJar, unable to resolve command from input scanner.");
        }
        if ((inputString != null) && (!inputString.equals(""))) {
            destinationPathToJar = inputString;
        }
    }

    private void setPathToPrivateKey(Scanner inputScanner) {
        System.out.println("-------------------------------------------------");
        System.out.println("Enter path to private key file: ");
        String inputString = null;
        try {
            inputString = inputScanner.nextLine();
        } catch (Exception e) {
            System.err.println(
                "Error at DeployerApp:setPathToPrivateKey, unable to resolve command from input scanner.");
        }
        if ((inputString != null) && (!inputString.equals(""))) {
            File f = new File(inputString);
            if(f.exists() && !f.isDirectory()) { 
                pathToPrivateKey = inputString;
            } else {
                System.err.println("Error, file doesn't exist xor is directory.");
                setPathToPrivateKey(inputScanner);
            }
        }
    }

    private void createProcesses2() {
        try {
            FileReader reader = new FileReader(this.pathToNetworkConfigFile);
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();

            JSONObject controllerJSONObject = peerFileAsJSONObject.getJSONObject("controller");
            JSONArray nodesJSONArray = peerFileAsJSONObject.getJSONArray("nodes");

            // For each node in the json array, create a process
            for (Object node : nodesJSONArray) {

                JSONObject nodeJSONObject = (JSONObject)node;
                String ipPort = nodeJSONObject.getString("ipPort");
                Peer nodePeer = new Peer(ipPort);

                List<String> nodeJarArgs = new ArrayList<String>();
                nodeJarArgs.add("node");
                nodeJarArgs.add(ipPort);
                nodeJarArgs.add(Integer.toString(nodeJSONObject.getInt("storage")));
                nodeJarArgs.add(controllerJSONObject.getString("ipPort"));
                for (Object directNeighbor : nodeJSONObject.getJSONArray("direct_neighbors")) {
                    JSONObject directNeighborJSONObject = (JSONObject)directNeighbor;
                    nodeJarArgs.add(directNeighborJSONObject.getString("ipPort"));
                }

                // Distribute executables to systems
                Thread thread = new RemoteDeployerThread(
                    "student", 
                    nodePeer.getIPAddress(), 
                    this.pathToPrivateKey, 
                    null, 
                    pathToJar, 
                    destinationPathToJar, 
                    EDeployFunction.DeployExecutable);
                thread.run();
                try {
                    thread.join();
                    String command = String.join(
                    " ", buildCommand(this.destinationPathToJar, nodeJarArgs.toArray(new String[0])));
                    // Execute node apps
                    new RemoteDeployerThread(
                        "student", 
                        nodePeer.getIPAddress(), 
                        this.pathToPrivateKey, 
                        command, 
                        null, 
                        null, 
                        EDeployFunction.ExecuteNode).run();
                } catch (InterruptedException e) {
                    System.err.println(
                        "Thread of node: " + nodeJarArgs.get(1) + " was interrupted while distributing executable.");
                }
            }

            String[] controllerJarArgs = {
                "controller", controllerJSONObject.getString("ipPort"), this.pathToNetworkConfigFile};
            // Construct controller command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(buildCommand(pathToJar, controllerJarArgs));
            // Start the controller process
            Process process = processBuilder.start();

            // Handle process input and output in separate threads for input / output of controller process.
            Thread outputThread = new Thread(() -> handleOutput(process));
            outputThread.start();

            Thread inputThread = new Thread(() -> handleInput(process));
            inputThread.start();

            try {
                outputThread.join();
                inputThread.join();
                process.waitFor();
                System.out.println("-------------------------------------------------");
                System.out.println("Deployer app stopped.");
            } catch (InterruptedException e) {
                System.err.println(e);
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void createProcesses() {
        try {
            FileReader reader = new FileReader(this.pathToNetworkConfigFile);
            JSONObject peerFileAsJSONObject = new JSONObject(new JSONTokener(reader));
            reader.close();

            JSONObject controllerJSONObject = peerFileAsJSONObject.getJSONObject("controller");
            JSONArray nodesJSONArray = peerFileAsJSONObject.getJSONArray("nodes");

            // For each node in the json array, create a process
            for (Object node : nodesJSONArray) {
                JSONObject nodeJSONObject = (JSONObject)node;
                List<String> nodeJarArgs = new ArrayList<String>();
                nodeJarArgs.add("node");
                nodeJarArgs.add(nodeJSONObject.getString("ipPort"));
                nodeJarArgs.add(Integer.toString(nodeJSONObject.getInt("storage")));
                nodeJarArgs.add(controllerJSONObject.getString("ipPort"));
                for (Object directNeighbor : nodeJSONObject.getJSONArray("direct_neighbors")) {
                    JSONObject directNeighborJSONObject = (JSONObject)directNeighbor;
                    nodeJarArgs.add(directNeighborJSONObject.getString("ipPort"));
                }
                // Construct node commands
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command(buildCommand(pathToJar, nodeJarArgs.toArray(new String[0])));
                // Start the node processes
                processBuilder.start();
                System.out.println("-------------------------------------------------");
                System.out.println("Started node process:\njava -jar " + pathToJar + " " + nodeJarArgs.toString());
            }

            String[] controllerJarArgs = {
                "controller", 
                controllerJSONObject.getString("ipPort"), 
                this.pathToNetworkConfigFile};
            // Construct controller command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(buildCommand(pathToJar, controllerJarArgs));
            // Start the controller process
            Process process = processBuilder.start();

            // Handle process input and output in separate threads for input / output of controller process.
            Thread outputThread = new Thread(() -> handleOutput(process));
            outputThread.start();

            Thread inputThread = new Thread(() -> handleInput(process));
            inputThread.start();

            try {
                outputThread.join();
                inputThread.join();
                process.waitFor();
                System.out.println("-------------------------------------------------");
                System.out.println("Deployer app stopped.");
            } catch (InterruptedException e) {
                System.err.println(e);
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private static String[] buildCommand(String jarPath, String[] args) {
        // Create the command array with "java", "-jar", jarPath, mode, 
        // ipPort, storage, controller ipPort, direct neighbors ipPort
        String[] command = new String[args.length + 3];
        command[0] = "java";
        command[1] = "-jar";
        command[2] = jarPath;
        System.arraycopy(args, 0, command, 3, args.length);
        return command;
    }

    private static void handleOutput(Process process) {
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleInput(Process process) {
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = reader.readLine()) != null) {
                writer.write(input + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
