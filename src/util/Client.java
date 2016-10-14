package util;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import util.SocketListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

;

public class Client implements SocketListener {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    @FXML
    TextField message;

    @FXML
    ListView<String> chatWindow;

    public void sendMessage() throws IOException {
        System.out.println("Sending message:");
        if (!message.getText().isEmpty()) {
            out.println(message.getText());
            System.out.println("\t" + message.getText());
            out.flush();
        }
        message.setText("");
        System.out.println("Done.");
    }

    public void startClient(String ip, int port) {
        try {
            System.out.println("Starting client...");
            socket = new Socket(ip, port);
            System.out.println("\tsocket " + ip + ":" + port);

            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Readers: \n\t" + in + "\n\t" + out);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Listener...");

        new ChatListener(this).start();
        System.out.println("Done.");

    }

    @Override
    public void onMessage(String line) {
        chatWindow.getItems().addAll(line);
    }

    private class ChatListener extends Thread {

        SocketListener listener;

        ChatListener(SocketListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
          final  String line[] = new String[1];

            while (true) {
                try {
                    line[0] = in.readLine();
                    System.out.println("Received message:\n\t" + line[0]);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.onMessage(line[0]);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
