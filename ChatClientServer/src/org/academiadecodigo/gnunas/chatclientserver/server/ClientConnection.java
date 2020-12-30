package org.academiadecodigo.gnunas.chatclientserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private String username = "";
    private UserType userType = UserType.NORMAL_USER;

    public ClientConnection(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {

        try {
            username = askForUsername();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {

            String input = readFromInputStream();

            if (input != null) {
                System.out.println(input);

                if (input.startsWith("//")) {
                    server.executeCommand(input, this);
                    continue;
                }
                server.broadcast(username + ": " + input, this);
                continue;
            }

            if (!socket.isClosed()) {
                close();
            }
            server.killMePlease(this);
            server.broadcast("User " + username + " has left the chat.", this);
            return;
        }
    }

    private String askForUsername() throws IOException {
        out.println("Enter desired Username:");

        username = in.readLine();

        System.out.println(username);

        if (server.alreadyInUse(username, this)) {
            out.println("Username is already being used.");
            return askForUsername();
        }
        out.println("Username set to: " + username + "\n" +
                "Hello " + username + "! Welcome to this amazing chat. You are in the General room. \n" +
                "You user privileges are: " + userType.toString() + "\n" +
                "To see possible commands type //listcmd");

        return username;
    }

    private String readFromInputStream() {

        String input = "";

        if (socket.isClosed()) {
            return null;
        }

        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

    public void sendToClient(String toSend) {

        out.println(toSend);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getUsername() {

        return username;
    }

    public void changeUsername(String newUsername) {

        if (server.alreadyInUse(newUsername, this)) {
            out.println("Username already in use.");
            return;
        }

        out.println("Username changed from " + username + " to " + newUsername);
        server.broadcast("User " + username + " changed username to " + newUsername, this);
        this.username = newUsername;

    }

    public UserType getUserType() {
        return userType;
    }

    public void setAdmin() {
        userType = UserType.ADMIN_USER;
    }

    public enum UserType {
        NORMAL_USER(),
        ADMIN_USER()


    }
}
