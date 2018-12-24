package server;

import common.FilesTransfer;
import common.Message;

import java.io.*;
import java.net.*;
import java.util.*;

public class QuoteServerThread extends Thread {
    public static String SAVEDFILESPATH = "D:\\JavaTalkTmp";
    private HashMap<String, Client> clients = new HashMap<String, Client>();
    private DatagramSocket socket;
    private boolean moreQuotes = true;
    private HashMap<Integer,Message> notSendMessages;

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
        File dir = new File(SAVEDFILESPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public QuoteServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        notSendMessages = new HashMap<>();
    }

    @Override
    public void run() {
        new Thread(new Send(socket, notSendMessages, clients)).start();
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                new HandleMessageThread(packet, clients, socket, notSendMessages).start();
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

class HandleMessageThread extends Thread {
    private DatagramPacket packet;
    private HashMap<String, Client> clients;
    private DatagramSocket socket;
    private HashMap<Integer,Message> notSendMessages;

    public HandleMessageThread(DatagramPacket packet, HashMap<String, Client> clients, DatagramSocket socket, HashMap<Integer, Message> notSendMessages) {
        this.packet = packet;
        this.clients = clients;
        this.socket = socket;
        this.notSendMessages = notSendMessages;
    }

    @Override
    public void run() {
        try {
            byte[] buf;
            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData()), "utf-8"));
            String sender = in.readLine();
            String receiver = in.readLine();
//            System.out.println("handle message");
            if (receiver.equals("System")) {
                //系统消息
                String message = in.readLine();
                if (message.equals("login")) {
                    //请求登录
                    String string;
                    synchronized (clients) {
                        if (clients.get(sender) == null) {
                            //未登录
                            clients.put(sender, new Client(sender, packet.getAddress(), packet.getPort()));
                            System.out.println(sender + " login");
                            string = "System\n" + sender + "\n" + "true\n";
                        } else {
                            //已登录
                            string = "System\n" + sender + "\n" + "false\n";
                        }
                    }
                    buf = string.getBytes();
                    DatagramPacket tmp = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                    socket.send(tmp);
                } else if (message.equals("logout")) {
                    System.out.println(sender + " logout");
                    clients.remove(sender);
                } else if (message.equals("sendFile")) {
                    //请求发送文件
//                    System.out.println("sendFile");
                    String fileReceiver = in.readLine();
                    String fileName = in.readLine();
//                    System.out.println("fileReceiver: " + fileReceiver);
//                    System.out.println("fileName: " + fileName);
                    //Thread
                    Boolean result = FilesTransfer.receiveFile(QuoteServerThread.SAVEDFILESPATH, packet.getAddress(), null);
                    if (result) {
                        //接收成功
                        String string = "PleaseReceiveFile\n" + sender + "\n" + fileName + "\n";
                        Message msg = new Message("System", fileReceiver, string);
                        synchronized (notSendMessages) {
//                            System.out.println(string + msg.toString());
                            notSendMessages.put(msg.hashCode(), msg);
                        }
                    }
                } else if (message.equals("receiveFile")) {
                    //发送文件
                    String fileName = in.readLine();
//                    System.out.println("will send file: " + fileName);
                    String string = "CanReceiveFile\n" + fileName + "\n";
                    Message msg = new Message("System", sender, string);
                    synchronized (notSendMessages) {
                        notSendMessages.put(msg.hashCode(), msg);
                    }
                    new FilesTransfer.SendThread(QuoteServerThread.SAVEDFILESPATH + "\\" + fileName, null).start();
                } else {
                    System.out.println("Unknown Error");
                }
            } else {    //非系统消息
                //提取信息
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line=in.readLine()) != null) {
//                    System.out.println("#" + line  +"#");
                    stringBuffer.append(line);
                    stringBuffer.append("\n");
                }
                //添加到未发送消息
                synchronized (notSendMessages) {
                    Message message = new Message(sender, receiver, stringBuffer.toString());
                    notSendMessages.put(message.hashCode(), message);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Send implements Runnable {
    private DatagramSocket socket;
    private byte[] buf = new byte[256];
    private HashMap<Integer, Message> notSendMessages;
    private HashMap<String, Client> clients;
    private DatagramPacket packet;

    public Send(DatagramSocket socket, HashMap<Integer, Message> notSendMessages, HashMap<String, Client> clients) {
        this.socket = socket;
        this.notSendMessages = notSendMessages;
        this.clients = clients;
    }


    @Override
    public void run(){
        try {
            while (true) {
                synchronized (notSendMessages) {
//                    System.out.println("未处理消息：" + notSendMessages.size() + "条");
                    HashSet<Integer> all = new HashSet<>(notSendMessages.keySet());
                    HashSet<Integer> remove = new HashSet<>();
                    for (Integer key:all) {
                        Message current = notSendMessages.get(key);
                        synchronized (clients) {
                            Client receiver = clients.get(current.getReceiver());
                            if (receiver != null) {
                                //接收者在线
                                try {
                                    buf = current.toString().getBytes("utf-8");
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                packet = new DatagramPacket(buf, buf.length, receiver.getAddress(), receiver.getPort());
                                socket.send(packet);
                                remove.add(key);
                            }
                        }
                    }
                    //移除已发送消息
                    for (Integer key:remove) {
                        notSendMessages.remove(key);
                    }
                }
//                try {
//                    Thread.sleep(10);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}