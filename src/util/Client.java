package util;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.json.JSONObject;

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

    ClientInfo info;

    @FXML
    TextField message;

    @FXML
    ListView<String> chatWindow;

    public void sendMessage() throws IOException {
        System.out.println("Sending message:");
        if (!message.getText().isEmpty()) {
            JSONObject json = new JSONObject();
            json.put(Keys.MESSAGE, message.getText());
            out.println(json.toString());
            System.out.println("\t" + json.toString());
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
        JSONObject json = new JSONObject(line);
        if (json.has(Keys.MESSAGE)) {
            chatWindow.getItems().addAll(json.getString(Keys.MESSAGE));
        }
        if (json.has(Keys.A_VALUE)) {
        }
        if (json.has(Keys.B_VALUE)) {
        }
        if (json.has(Keys.P_VALUE)) {
        }
        if (json.has(Keys.ENCRYPTION)) {
        }
    }

    private class ChatListener extends Thread {

        SocketListener listener;

        ChatListener(SocketListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            final String line[] = new String[1];
            while (true) {
                try {
                    line[0] = in.readLine();
                    System.out.println("Received message:\n\t" + line[0]);
                    Platform.runLater(() -> listener.onMessage(line[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
