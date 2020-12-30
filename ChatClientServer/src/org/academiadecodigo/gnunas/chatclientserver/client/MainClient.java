package org.academiadecodigo.gnunas.chatclientserver.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) throws UnknownHostException {

        try {
            new Client(52424, InetAddress.getLocalHost()).start();
        } catch (IOException e) {
            System.err.println("Unable to launch client.");;
        }

    }
}
