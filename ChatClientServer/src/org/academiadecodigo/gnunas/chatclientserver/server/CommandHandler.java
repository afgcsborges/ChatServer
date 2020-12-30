package org.academiadecodigo.gnunas.chatclientserver.server;

import org.academiadecodigo.gnunas.chatclientserver.client.Client;

import java.util.HashMap;

public final class CommandHandler {

    private HashMap<Command, Commandline> commandList;
    private Server server;


    public CommandHandler(Server server) {
        this.server = server;
        commandList = new HashMap<>();
        configureCommands();
    }

    public void executeCommand(String input, ClientConnection clientConnection) {

        //String commandTag = input.substring(2);
        System.out.println("iamhere");
        System.out.println(input.length());

        Command command = input.length() == 2 ? Command.INVALID_COMMAND : Command.getCorrespondingCommand(input.substring(2).split(" ")[0]);

        commandList.get(command).runCommand(input, clientConnection);

    }

    private void configureCommands() {

        commandList.put(Command.LIST_CMD, (input, clientConnection) -> listCmd(input, clientConnection));
        commandList.put(Command.INVALID_COMMAND, (input, clientConnection) -> invalidCommand(clientConnection));
        commandList.put(Command.LIST_USERS, (input, clientConnection) -> listUsers(input, clientConnection));
        commandList.put(Command.QUIT, (input, clientConnection) -> quit(input,clientConnection));
        commandList.put(Command.CHANGE_USERNAME, (input, clientConnection) -> changeUsername(input,clientConnection));
        commandList.put(Command.PRIVATE_MESSAGE, ((input, clientConnection) -> sendPrivateMessage(input, clientConnection)));
        commandList.put(Command.ADMIN_PRIVILEGES, ((input, clientConnection) -> getAdminPrivileges(input, clientConnection)));
    }

    private void getAdminPrivileges(String input, ClientConnection clientConnection) {

        if (clientConnection.getUserType() == ClientConnection.UserType.ADMIN_USER){
            clientConnection.sendToClient("You already have ADMIN privileges");
            return;
        }

        String[] command = input.split(" ",3);

        if (command.length != 2) {
            invalidCommand(clientConnection);
            return;
        }

        if (command[1].hashCode() == 70537939) {
            clientConnection.setAdmin();
            clientConnection.sendToClient("ADMIN privileges granted.");
            return;
        }

        clientConnection.sendToClient("Incorrect admin password.");
    }

    private void sendPrivateMessage(String input, ClientConnection clientConnection) {

        String[] command = input.split(" ",3);

        if (command.length != 3) {
            invalidCommand(clientConnection);
            return;
        }

        ClientConnection target = server.getTargetForPM(command[1]);
        if(target == null){
            clientConnection.sendToClient("User " + command[1] + " does not exist.");
            return;
        }

        target.sendToClient("<Private Message> " + clientConnection.getUsername() + ": " + command[2]);
    }

    private void changeUsername(String input, ClientConnection clientConnection){

        String[] command = input.split(" ",2);

        if (command.length == 1 || command[1].startsWith(" ")) {
            invalidCommand(clientConnection);
            return;
        }

        clientConnection.changeUsername(command[1]);

    }

    private void quit(String input, ClientConnection clientConnection){

        if (input.split(" ").length > 1) {
            invalidCommand(clientConnection);
            return;
        }

        server.killMePlease(clientConnection);
        clientConnection.close();

    }

    private void listUsers(String input, ClientConnection clientConnection) {

        if (input.split(" ").length > 1) {
            invalidCommand(clientConnection);
            return;
        }

        clientConnection.sendToClient(server.listAllUsers());
    }

    private void invalidCommand(ClientConnection clientConnection) {

        clientConnection.sendToClient(Command.INVALID_COMMAND.instructions);
    }

    public void listCmd(String input, ClientConnection clientConnection) {
        if (input.split(" ").length > 1) {
            invalidCommand(clientConnection);
            return;
        }

        clientConnection.sendToClient(Command.listAllCommands());
    }


    private enum Command {

        LIST_CMD("listcmd", "//listcmd to list all commands."),
        LIST_USERS("listusers", "//listusers to list all users connected."),
        QUIT("quit", "//quit to exit the chat."),
        CHANGE_USERNAME("changename", "//changename <new username> to change username."),
        PRIVATE_MESSAGE("pm", "//pm <username> <message> to send a private message."),
        INVALID_COMMAND("invalid", "Invalid command. Enter //listcmd to view possible commands."),
        ADMIN_PRIVILEGES("becomeadmin", "//becomeadmin <password> to be granted admin privileges.");

        private final String command;
        private final String instructions;

        Command(String command, String instructions) {
            this.command = command;
            this.instructions = instructions;
        }

        public static Command getCorrespondingCommand(String command) {
            for (Command corresponding : Command.values()) {
                if (corresponding.command.equals(command)) {
                    return corresponding;
                }
            }
            return INVALID_COMMAND;
        }

        public static String listAllCommands() {
            String commands = "";

            for (Command command : Command.values()) {
                if (command == INVALID_COMMAND) {
                    continue;
                }
                commands += command.instructions + "\n";
            }

            return commands;
        }

    }

    private interface Commandline {

        void runCommand(String input, ClientConnection clientConnection);
    }
}
