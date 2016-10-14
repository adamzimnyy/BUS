package util;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
        setSize(new Dimension(400, 307));
        Container c = getContentPane();
        c.setBackground(Color.BLACK);
        getContentPane().setLayout(null);
        TA = new JTextArea();
        TA.setEditable(true);
        TA.setBackground(Color.BLACK);
        TA.setForeground(Color.GREEN);
        JScrollPane scrollPane = new JScrollPane(TA);
        scrollPane.setBounds(0, 0, 384, 269);
        c.add(scrollPane);
        setVisible(true);
    }

    public void start(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                display("Waiting for client connections on "
                        + InetAddress.getLocalHost().getHostAddress() + ":"
                        + server.getLocalPort());
                Socket conn = server.accept();

                ClientThread t = new ClientThread(conn);
                cList.add(t);
                t.start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }// start()

    private void display(String msg) {
        TA.append(msg + "\n");
        TA.setCaretPosition(TA.getDocument().getLength());
    }


    private synchronized void broadcast(String msg) {
        for (ClientThread ct : cList) {
            ct.out.println(msg);
            ct.out.flush();
        }
    }

    private synchronized void sendMessage(String msg, ClientThread ct) {
        ct.out.println(msg);
        ct.out.flush();
    }

    class ClientThread extends Thread implements SocketListener {
        PrintWriter out;
        BufferedReader in;
        ClientInfo info = new ClientInfo();
        Socket socket;

        ClientThread(Socket socket) {
            info.setId(++uniqueId);
            this.socket = socket;
            ;
            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                display("User " + info.getId() + " connected from "
                        + socket.getRemoteSocketAddress() + ".");
            } catch (IOException ioe) {
                display("Error creating in/out in ClientThread");
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

        @Override
        public void onMessage(String line) {
              JSONObject json = new JSONObject(line);
                if (json.has(Keys.MESSAGE)) {
                    broadcast(line);
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
    }
}
