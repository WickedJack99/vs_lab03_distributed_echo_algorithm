package com.hso.Deployer;

import java.io.IOException;

import com.jcraft.jsch.*;

public final class RemoteDeployerThread extends Thread {

    // Logging for debugging purposes
    // static {
    //     JSch.setLogger(new Logger() {
    //         public boolean isEnabled(int level) {
    //             return true;
    //         }

    //         public void log(int level, String message) {
    //             System.out.println("JSch [" + level + "]: " + message);
    //         }
    //     });
    // }

    private final String username;
    private final String hostname;
    private final String privateKey;
    private final String command;
    private final String source;
    private final String destination;

    private final EDeployFunction deployFunction;

    public RemoteDeployerThread(
        String username, 
        String hostname, 
        String privateKey, 
        String command, 
        String source,
        String destination,
        EDeployFunction deployFunction) {
        this.username = username;
        this.hostname = hostname;
        this.privateKey = privateKey;
        this.command = command;
        this.source = source;
        this.destination = destination;

        this.deployFunction = deployFunction;
    }

    public void run() {
        switch (deployFunction) {

            case DeployExecutable: {
                deployExecutable();
            }break;

            case ExecuteNode: {
                executeNode();
            }break;
        
            default: {
                System.err.println("Unknown deploy function: " + deployFunction);
            }break;
        }
    }

    private void deployExecutable() {
        String[] args = {this.source, this.username + "@" + this.hostname + ":" + this.destination};

        // Construct controller command
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(buildCommand(args));
        try {
            // Execute copy command
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println("-------------------------------------------------");
            System.out.println("Deployment of executable complete.");
            System.out.println(
                "Executable can be found at: " + this.username + "@" + this.hostname + ":" + this.destination);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String[] buildCommand(String[] args) {
        String[] command = new String[args.length + 1];
        command[0] = "scp";
        System.arraycopy(args, 0, command, 1, args.length);
        return command;
    }

    private void executeNode() {
        System.out.println("Entered executeNode");
        JSch javaSecureChannel = new JSch();
        Session session = null;

        try {
            javaSecureChannel.addIdentity(this.privateKey, "");
            session = javaSecureChannel.getSession(this.username, this.hostname, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(this.command);

            channelExec.connect();
            System.out.println("-------------------------------------------------");
            System.out.println("Execution of node started.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
