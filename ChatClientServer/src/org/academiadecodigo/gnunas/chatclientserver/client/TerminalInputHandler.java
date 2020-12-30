package org.academiadecodigo.gnunas.chatclientserver.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class TerminalInputHandler implements Runnable{

    private PrintWriter out ;

    public TerminalInputHandler(PrintWriter out) {

        this.out = out;
    }

    @Override
    public void run() {

        BufferedReader inputFromTerm = new BufferedReader(new InputStreamReader(System.in));

        while(true) {

            String message ;
            try {
                message = inputFromTerm.readLine();
                out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
