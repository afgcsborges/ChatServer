package org.academiadecodigo.gnunas.chatclientserver.server;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int SUPPORTED_CONNECTIONS = 1000;

    private final ServerSocket serverSocket;
    private final List<ClientConnection> clientConnectionList;
    private final ExecutorService deadPool;
    private final CommandHandler commandHandler;


    public Server(int SERVER_PORT) throws IOException {

        serverSocket = new ServerSocket(SERVER_PORT);
        clientConnectionList = new LinkedList<>();
        deadPool = Executors.newFixedThreadPool(SUPPORTED_CONNECTIONS);
        commandHandler = new CommandHandler(this);
    }

    public void start() {

        waitForClientRequest();
        start();
    }

    private void waitForClientRequest() {

        try {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connection Established");

            ClientConnection client = new ClientConnection(clientSocket, this);

            synchronized (clientConnectionList) {
                clientConnectionList.add(client);
            }

            deadPool.submit(client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void killMePlease(ClientConnection clientToBeKilled) {

        synchronized (clientConnectionList) {

            clientConnectionList.remove(clientToBeKilled);
        }
    }

    public void broadcast(String input, ClientConnection clientConnection) {

        synchronized (clientConnectionList) {

            for (ClientConnection client : clientConnectionList) {
                if (client == clientConnection) {
                    continue;
                }
                client.sendToClient(input);
            }
        }
    }

    public boolean alreadyInUse(String username, ClientConnection clientAsking) {

        synchronized (clientConnectionList) {

            for (ClientConnection client : clientConnectionList) {
                if (client.getUsername().equals(username) && client != clientAsking) {
                    return true;
                }
            }
            return false;
        }
    }

    public void executeCommand(String input, ClientConnection clientConnection){
        commandHandler.executeCommand(input,clientConnection);
    }

    public String listAllUsers() {
        String userList = "Users currently connected: \n";
        for (ClientConnection clientConnection : clientConnectionList) {
            userList += clientConnection.getUsername() + "\n";
        }
        return userList;
    }

    public ClientConnection getTargetForPM(String username) {

        for( ClientConnection clientConnection : clientConnectionList){
            if(clientConnection.getUsername().equals(username)){
                return clientConnection;
            }
        }
        return null;
    }
}
