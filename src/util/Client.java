package util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    Label aLabel, bLabel, pLabel, gLabel, sLabel, secretALabel;

    @FXML
    ListView<String> chatWindow;

    @FXML
    public void applyEncryption(ActionEvent e) {
        //TODO get encryption from radioButton and send json "encryption"
    }

    @FXML
    public void onSendMessage() throws IOException {
        if (!message.getText().isEmpty()) {
            send(Keys.MESSAGE, message.getText());
        }
        message.setText("");
    }

    private void send(String key, String value) {
        JSONObject json = new JSONObject();
        json.put(key, value);
        out.println(json.toString());
        out.flush();
    }

    public void startClient(String ip, int port) {
        info = new ClientInfo();
        info.setPort(port);
        info.setSecretA(DiffieHellman.getInitialSecret());
        updateInfo();
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        new ChatListener(this).start();
    }

    @Override
    public void onMessage(String line) {
        JSONObject json = new JSONObject(line);
        if (json.has(Keys.MESSAGE)) {
            //TODO encrypt
            chatWindow.getItems().addAll(json.getString(Keys.MESSAGE));
        }
        if (json.has(Keys.A_VALUE)) {

            System.out.println("Client should never receive A value!");
        }
        if (json.has(Keys.B_VALUE)) {

            int b = json.getInt("b");
            info.setB(b);


            if (info.isReady()) {
                info.setS(DiffieHellman.makeClientSecret(info));
            }
            updateInfo();
        }
        if (json.has(Keys.P_VALUE)) {

            int p = json.getInt("p");
            info.setP(p);
            int g = json.getInt("g");
            info.setG(g);
            info.setA(DiffieHellman.makeA(info));
            JSONObject aJson = new JSONObject();
            aJson.put(Keys.A_VALUE, info.getA());
            out.println(aJson.toString());
            out.flush();
            updateInfo();
        }
        if (json.has(Keys.ENCRYPTION)) {
            if (info.isReady()) {
                info.setS(DiffieHellman.makeClientSecret(info));
                updateInfo();
            }
        }
    }

    private void updateInfo() {
        if (info.getA() != null) aLabel.setText("A: " + info.getA());
        else aLabel.setText("A:");
        if (info.getB() != null) bLabel.setText("B: " + info.getB());
        else bLabel.setText("B:");
        if (info.getP() != null) pLabel.setText("P: " + info.getP());
        else pLabel.setText("P:");
        if (info.getG() != null) gLabel.setText("G: " + info.getG());
        else gLabel.setText("G:");
        if (info.getS() != null) sLabel.setText("S: " + info.getS());
        else sLabel.setText("S:");
        if (info.getSecretA() != null) secretALabel.setText("a: " + info.getSecretA());
        else secretALabel.setText("a:");
    }

    private class ChatListener extends Thread {

        SocketListener listener;

        ChatListener(SocketListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            send(Keys.REQUEST, Keys.KEYS);

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
