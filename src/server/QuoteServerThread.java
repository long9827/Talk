package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class QuoteServerThread extends Thread {
    private HashMap<String, Client> client = new HashMap<String, Client>();
    private DatagramSocket socket = null;
    private boolean moreQuotes = true;

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    @Override
    public void run() {
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData())));
                String sender = in.readLine();
                String receiver = in.readLine();
                String message = in.readLine();
//                System.out.println(message);
                if (receiver.equals("System")) {
//                    System.out.println("sender null");
                    client.put(sender, new Client(sender, packet.getAddress(), packet.getPort()));
                    continue;
                }
                Client receiverClient = client.get(receiver);
                if (receiverClient == null) {
//                    System.out.println("receiver null");
                    String string = "System: " +  receiver + " not found!\n";
                    buf = string.getBytes();
                    packet = new DatagramPacket(buf, buf.length, client.get(sender).getAddress(), client.get(sender).getPort());
                    socket.send(packet);
                } else {
                    String string = sender + ": " + message;
                    buf = string.getBytes();
                    packet = new DatagramPacket(buf, buf.length, receiverClient.getAddress(), receiverClient.getPort());
                    socket.send(packet);
                }
            }catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }
}

class Client {
    private String name;
    private InetAddress address;
    private int port;

    public Client(String name, InetAddress address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}