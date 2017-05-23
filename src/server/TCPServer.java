package server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Junior Polygraphist on 23.05.2017.
 */
public class TCPServer extends Thread {

    public static final int SERVER_PORT = 4444;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;

    public static void main(String[] args) {

        JServerActivity jServerActivity = new JServerActivity();
        jServerActivity.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jServerActivity.pack();
        jServerActivity.setVisible(true);
    }

    // init constructor
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    // send message from server to client
    public void sendMessage(String message) {
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public void run() {
        super.run();

        running = true;

        try {
            System.out.println("Server: Connecting...");

            //create a server socket
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            //create client socket
            Socket client = serverSocket.accept();
            System.out.println("Server: Receiving...");

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                while (running) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        messageListener.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                System.out.println("Server: Error");
                e.printStackTrace();
            } finally {
                client.close();
                System.out.println("Server: Done.");
            }

        } catch (Exception e) {
            System.out.println("Server: Error");
            e.printStackTrace();
        }

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
