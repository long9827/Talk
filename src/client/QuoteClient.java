package client;

import java.io.*;
import java.net.*;

public class QuoteClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("用户名：");
        String name = in.readLine();
        new Thread(new Send(name, socket)).start();
        new Thread(new Receive(socket)).start();

    }
}

class Send implements Runnable {
    private String name;
    private DatagramSocket socket;
    private byte[] buf = new byte[256];


    public Send(String name, DatagramSocket socket) {
        this.name = name;
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            String message = name + "\nSystem\nlogin\n";
            buf = message.getBytes();
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            while (true) {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                message = name + "\n" + in.readLine() + "\n" + in.readLine() + "\n";
//                System.out.println("#" + message + "#");
                buf = message.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, 4445);
                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Receive implements Runnable {
    private byte[] buf = new byte[256];
    private DatagramSocket socket;

    public Receive(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());
                System.out.println(received);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}