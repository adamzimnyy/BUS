package util;

import org.json.JSONObject;
import util.constant.Key;
import util.constant.Value;
import util.crypto.Caesar;
import util.crypto.DiffieHellman;
import util.crypto.Xor;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Base64;

@SuppressWarnings("InfiniteLoopStatement")
public class Server extends JFrame {

    private static final long serialVersionUID = 3998655654695771862L;

    private static int uniqueId = 0;
    private JTextArea TA;
    private ArrayList<ClientThread> cList = new ArrayList<>();

    public static void main(String[] args) {
        Server s = new Server();
        if (args.length != 0)
            s.start(Integer.parseInt(args[0]));
        else s.start(0);
    }

    public Server() {
        super("Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(1600, 900));
        Container c = getContentPane();
        c.setBackground(Color.BLACK);
        getContentPane().setLayout(null);
        TA = new JTextArea();
        TA.setEditable(true);
        TA.setBackground(Color.BLACK);
        TA.setForeground(Color.GREEN);
        JScrollPane scrollPane = new JScrollPane(TA);
        scrollPane.setBounds(0, 0, 1600, 900);
        c.add(scrollPane);
        setVisible(true);
    }

    public void start(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            DiffieHellman.initPrimes(DiffieHellman.primeSize);
            display("Waiting for client connections on "
                    + InetAddress.getLocalHost().getHostAddress() + ":"
                    + server.getLocalPort());
            while (true) {

                Socket conn = server.accept();

                ClientThread t = new ClientThread(conn);
                cList.add(t);
                t.start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void display(String msg) {
        TA.append(msg + "\n");
        TA.setCaretPosition(TA.getDocument().getLength());
    }


    private synchronized void broadcast(String msg, String name) {
        for (ClientThread ct : cList) {
            String message = ct.encrypt(msg);
            String encoded = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
            JSONObject json = new JSONObject();
            json.put(Key.MESSAGE, encoded);
            json.put(Key.FROM, name);
            sendJson(json.toString(), ct);
        }
    }

    private synchronized void sendJson(String json, ClientThread ct) {
        ct.out.println(json);
        ct.out.flush();
    }

    private class ClientThread extends Thread implements SocketListener {
        PrintWriter out;
        BufferedReader in;
        ClientInfo info = new ClientInfo();
        Socket socket;

        ClientThread(Socket socket) {

            try {
                info.setId(++uniqueId);
                info.setSecretB(DiffieHellman.getInitialSecret());
                info.setP(DiffieHellman.generateP());
                info.setG(DiffieHellman.generateG());
                info.setB(DiffieHellman.makeB(info));
                this.socket = socket;
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                display("User " + info.getId() + " connected from "
                        + socket.getRemoteSocketAddress() + ".");
            } catch (IOException ioe) {
                display("Error creating in/out in ClientThread");
            } catch (NoSuchAlgorithmException | InvalidParameterSpecException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            String line;
            while (true) {
                try {
                    line = in.readLine();
                    System.out.println("Received message:\n\t" + line);
                    onMessage(line);
                } catch (IOException e) {
                    System.out.println(e);
                    break;
                }
            }
        }

        private String decrypt(String s) {
            if (info.getEncryption() != null && info.getEncryption().equals(Value.CAESAR)) {
                System.out.println("Decrypted: " + Caesar.decrypt(s, info.getS()));
                return Caesar.decrypt(s, info.getS());
            } else if (info.getEncryption() != null && info.getEncryption().equals(Value.XOR)) {
                //   return Xor.encrypt(s, info.getS());
                return new String(Xor.encrypt(s, info.getS()));

            }
            return s;
        }

        public String encrypt(String message) {
            if (info.getEncryption() == null) return message;
            if (info.getEncryption().equals(Value.CAESAR)) {
                return Caesar.encrypt(message, info.getS());
            }
            if (info.getEncryption().equals(Value.XOR)) {
                //  return Xor.encrypt(message, info.getS());
                return new String(Xor.encrypt(message, info.getS()));

            }
            return message;
        }

        @Override
        public void onMessage(String line) {
            JSONObject json = new JSONObject(line);
            display(line);
            if (json.has(Key.MESSAGE)) {
                String encoded = json.getString(Key.MESSAGE);
                String encrypted = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
                String message = decrypt(encrypted);
                String name = json.getString(Key.FROM);
                broadcast(message,name);
            }
            if (json.has(Key.REQUEST)) {
                if (json.getString(Key.REQUEST).equals(Value.KEYS)) {
                    JSONObject pgJson = new JSONObject();
                    try {
                        pgJson.put(Key.P_KEY, DiffieHellman.generateP());
                        pgJson.put(Key.G_KEY, DiffieHellman.generateG());

                    } catch (NoSuchAlgorithmException | InvalidParameterSpecException e) {
                        e.printStackTrace();
                    }
                    sendJson(pgJson.toString(), this);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JSONObject bJson = new JSONObject();
                    bJson.put(Key.B_KEY, info.getB());
                    sendJson(bJson.toString(), this);
                }
            }

            if (json.has(Key.A_KEY)) {
                info.setA(json.getBigInteger("a"));
                if (info.isReady()) {
                    info.setS(DiffieHellman.makeServerSecret(info));
                    System.out.println(info.toString());
                    display(info.toString());
                }
            }
            if (json.has(Key.B_KEY)) {
                System.out.println("Server should never receive B value!");

            }
            if (json.has(Key.P_KEY)) {
                System.out.println("Server should never receive p or g value!");
            }
            if (json.has(Key.ENCRYPTION)) {
                String en = json.getString(Key.ENCRYPTION);
                if (en.equals(Value.CAESAR) || en.equals(Value.NONE) || en.equals(Value.XOR))
                    info.setEncryption(en);
            }
        }
    }
}
