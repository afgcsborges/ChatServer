package org.academiadecodigo.gnunas.chatclientserver.server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        try {
            new Server(52424).start();
        } catch (IOException e) {
            System.err.println("Unable to launch server");
        }
    }
}
