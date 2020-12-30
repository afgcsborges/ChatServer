package org.academiadecodigo.gnunas.chatclientserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private int portNumber;
    private Socket clientSocket;
    private InetAddress serverHost;
    private PrintWriter out;
    private BufferedReader in;

    public Client(int portNumber, InetAddress serverHost) throws IOException {

        this.portNumber = portNumber;
        this.serverHost = serverHost;
        openClientSocket();
    }

    private void openClientSocket() throws IOException {

            clientSocket = new Socket(serverHost,portNumber);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void start() {

        new Thread(new TerminalInputHandler(out)).start();

        while(true){
            try {
                String message = in.readLine();
                if(message == null){
                    System.out.println("Server is no longer reachable. Disconnecting from server.");
                    break;
                }
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
