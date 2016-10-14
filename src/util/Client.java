package util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.JSONObject;
import util.constant.Key;
import util.constant.Value;
import util.crypto.Caesar;
import util.crypto.DiffieHellman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

public class Client implements SocketListener, Initializable {
    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    ClientInfo info;

    @FXML
    TextField messageField;

    @FXML
    Label aLabel, bLabel, pLabel, gLabel, sLabel, secretALabel, encryptionLabel;

    @FXML
    ListView<String> chatWindow;

    @FXML
    RadioButton noneRadio, xorRadio, caesarRadio;
    ToggleGroup encryptionGroup;

    @FXML
    public void applyEncryption(ActionEvent e) {
        if (xorRadio.isSelected()) {
            info.setEncryption(Value.XOR);
            encryptionLabel.setText("Encryption: " + Value.XOR);
            send(Key.ENCRYPTION, Value.XOR);
        } else if (caesarRadio.isSelected()) {
            info.setEncryption(Value.CAESAR);
            encryptionLabel.setText("Encryption: " + Value.CAESAR);

            send(Key.ENCRYPTION, Value.CAESAR);
        } else {
            info.setEncryption(Value.NONE);
            encryptionLabel.setText("Encryption: " + Value.NONE);

            send(Key.ENCRYPTION, Value.NONE);
        }
    }

    @FXML
    public void onSendMessage() throws IOException {
        if (!messageField.getText().isEmpty()) {
            String message = messageField.getText();
            String encrypted = encrypt(message);
            String encoded = Base64.getEncoder().encodeToString(encrypted.getBytes(StandardCharsets.UTF_8));
            send(Key.MESSAGE, encoded);
        }
        messageField.setText("");
    }

    public String encrypt(String message) {
        if (info.getEncryption() == null) return message;
        if (info.getEncryption().equals(Value.CAESAR)) {
            return Caesar.encrypt(message, info.getS());
        }
        if (info.getEncryption().equals(Value.XOR)) {

        }
        return message;
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
        if (json.has(Key.MESSAGE)) {

            String message = json.getString(Key.MESSAGE);
            message = new String(Base64.getDecoder().decode(message), StandardCharsets.UTF_8);
            chatWindow.getItems().add(decrypt(message));
        }
        if (json.has(Key.A_VALUE)) {

            System.out.println("Client should never receive A value!");
        }
        if (json.has(Key.B_VALUE)) {

            int b = json.getInt("b");
            info.setB(b);


            if (info.isReady()) {
                info.setS(DiffieHellman.makeClientSecret(info));
            }
            updateInfo();
        }
        if (json.has(Key.P_VALUE)) {

            int p = json.getInt("p");
            info.setP(p);
            int g = json.getInt("g");
            info.setG(g);
            info.setA(DiffieHellman.makeA(info));
            JSONObject aJson = new JSONObject();
            aJson.put(Key.A_VALUE, info.getA());
            out.println(aJson.toString());
            out.flush();
            updateInfo();
        }
        if (json.has(Key.ENCRYPTION)) {
            if (info.isReady()) {
                info.setS(DiffieHellman.makeClientSecret(info));
                updateInfo();
            }
        }
    }

    public String decrypt(String message) {
        if (info.getEncryption() == null) return message;
        if (info.getEncryption().equals(Value.CAESAR)) {
            return Caesar.decrypt(message, info.getS());
        }
        if (info.getEncryption().equals(Value.XOR)) {

        }
        return message;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        encryptionGroup = new ToggleGroup();
        caesarRadio.setToggleGroup(encryptionGroup);
        noneRadio.setToggleGroup(encryptionGroup);
        xorRadio.setToggleGroup(encryptionGroup);
    }

    private class ChatListener extends Thread {

        SocketListener listener;

        ChatListener(SocketListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            send(Key.REQUEST, Value.KEYS);

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
