package server;

import common.Message;

import java.io.*;
import java.net.*;
import java.util.*;

public class QuoteServerThread extends Thread {
    private HashMap<String, Client> clients = new HashMap<String, Client>();
    private DatagramSocket socket = null;
    private boolean moreQuotes = true;
    private HashMap<Integer,Message> notSendMessages;

    public QuoteServerThread() throws IOException {
        this("QuoteServerThread");
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
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData())));
                String sender = in.readLine();
                String receiver = in.readLine();
//                System.out.println(message);
                if (receiver.equals("System")) {
                    //系统消息
                    String message = in.readLine();
//                    System.out.println(message + "end");
//                    System.out.println(message.equals("login"));
                    if (message.equals("login")) {
                        //请求登录
                        String string;
                        synchronized (clients) {
                            if (clients.get(sender) == null) {
                                //未登录
                                clients.put(sender, new Client(sender, packet.getAddress(), packet.getPort()));
                                System.out.println(sender + packet.getAddress() + packet.getPort());
                                string = "System\n" + sender + "\n" + "true\n";
                            } else {
                                //已登录
                                string = "System\n" + sender + "\n" + "false\n";
                            }
                        }
//                        System.out.println(string);
                        buf = string.getBytes();
                        DatagramPacket tmp = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
                        socket.send(tmp);

                        socket.send(packet);
                    }
//                    System.out.println("sender null");
                    continue;
                } else {    //非系统消息
                    //提取信息
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line=in.readLine()) != null) {
//                    System.out.println("#" + line  +"#");
                        stringBuffer.append(line);
                        stringBuffer.append("\n");
                    }
//                    System.out.println(stringBuffer);
                    //添加到未发送消息
                    synchronized (notSendMessages) {
                        Message message = new Message(sender, receiver, stringBuffer.toString());
                        notSendMessages.put(message.hashCode(), message);
                    }
                }
//                stringBuffer.append("end");
//                System.out.println();
//                Client receiverClient = client.get(receiver);
//                if (receiverClient == null) {
////                    System.out.println("receiver null");
//                    String string = "System: " +  receiver + " not found!\n";
//                    buf = string.getBytes();
//                    packet = new DatagramPacket(buf, buf.length, client.get(sender).getAddress(), client.get(sender).getPort());
//                    socket.send(packet);
//                } else {
//                    String string = sender + ": " + message;
//                    buf = string.getBytes();
//                    packet = new DatagramPacket(buf, buf.length, receiverClient.getAddress(), receiverClient.getPort());
//                    socket.send(packet);
//                }
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

class Send implements Runnable {
//    private String name;
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

    //    public Send(String name, DatagramSocket socket) {
////        this.name = name;
//        this.socket = socket;
//    }

    @Override
    public void run(){
        try {
//            String message = name + "\nSystem\nlogin\n";
//            buf = message.getBytes();
//            InetAddress address = InetAddress.getLocalHost();
//            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
//            socket.send(packet);

            while (true) {
                synchronized (notSendMessages) {
                    System.out.println("未处理消息：" + notSendMessages.size() + "条");
                    HashSet<Integer> all = new HashSet<>(notSendMessages.keySet());
                    HashSet<Integer> remove = new HashSet<>();
//                    ListIterator<Message> iterator = notSendMessages.listIterator();
//                    Message current;
//                    ArrayList<Integer> removeIndex = new ArrayList<>();
//                    int index=0;
                    for (Integer key:all) {
                        Message current = notSendMessages.get(key);
                        synchronized (clients) {
//                            String receiver = current.getReceiver();
//                            System.out.println("2current: "+ index + current.toString());
                            Client receiver = clients.get(current.getReceiver());
                            if (receiver != null) {
                                //接收者在线
//                                System.out
                                System.out.println("接收者：" + receiver.getName() + "地址：" + receiver.getAddress() + "端口：" +receiver.getPort() );
                                buf = current.toString().getBytes();
                                packet = new DatagramPacket(buf, buf.length, receiver.getAddress(), receiver.getPort());
                                socket.send(packet);
                                remove.add(key);
//                                removeIndex.add(index);
//                                receiver = null;
                            }
                        }

                    }
//                    while (iterator.hasNext()) {
//                        current = iterator.next();
//                        System.out.println("current: "+ index + current.toString());
//                        synchronized (clients) {
////                            String receiver = current.getReceiver();
////                            System.out.println("2current: "+ index + current.toString());
//                            Client receiver = clients.get(current.getReceiver());
//                            if (receiver != null) {
//                                //接收者在线
////                                System.out
//                                System.out.println("接收者：" + receiver.getName() + "地址：" + receiver.getAddress() + "端口：" +receiver.getPort() );
//                                buf = current.toString().getBytes();
//                                packet = new DatagramPacket(buf, buf.length, receiver.getAddress(), receiver.getPort());
//                                socket.send(packet);
//                                removeIndex.add(index);
////                                receiver = null;
//                            }
//                        }
//                        index++;
//                    }
                    //移除已发送消息
                    for (Integer key:remove) {
                        notSendMessages.remove(key);
                    }
//                    for (int i=removeIndex.size()-1; i>=0; --i) {
//                        System.out.println(removeIndex.get(i));
//                        notSendMessages.remove(removeIndex.get(i));
//                    }
                }
                try {
//                    System.out.println("开始休眠");
                    Thread.sleep(2000);
//                    System.out.println("休眠结束");
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//                message = name + "\n" + in.readLine() + "\n" + in.readLine() + "\n";
////                System.out.println("#" + message + "#");
//                buf = message.getBytes();
//                packet = new DatagramPacket(buf, buf.length, address, 4445);
//                socket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}