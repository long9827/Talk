package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;

public class Login extends JFrame implements ActionListener {
    private JTextField userName;
    private JTextField address;
    private JButton loginButton;
    private DatagramSocket socket;
    private InetAddress serverAddress;

    private HashSet<String> userSet;

    private void initUserSet() {
        userSet = new HashSet<>();

        for (int i=1; i<=20; ++i) {
            userSet.add("user" + i);
        }
    }

    public Login() {
        initUserSet();
        setTitle("登录");
        setSize(300, 180);
        setLocationRelativeTo(null);    //窗口居中
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JPanel jPanel = new JPanel(new GridLayout(3, 1));

        JPanel addressPanel = new JPanel();
        address = new JTextField(6);
        address.setText("127.0.0.1");
        addressPanel.add(new JLabel("服务器IP "));
        addressPanel.add(address);
        jPanel.add(addressPanel);

        JPanel inputPanel = new JPanel();
        userName = new JTextField(6);
        inputPanel.add(new JLabel("用户名 "));
        inputPanel.add(userName);
        jPanel.add(inputPanel);

        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("登录");
        loginButton.addActionListener(this);
        buttonPanel.add(loginButton);
        jPanel.add(buttonPanel);

        add(jPanel);
    }

    public void actionPerformed(ActionEvent e) {
        String name = userName.getText();
        if (userSet.contains(name)) {
            int result = connect(name, address.getText());
            if (result==0) {
                //登录成功
                setVisible(false);
                new MainFrame(name, serverAddress, socket).setVisible(true);
                dispose();
            } else if (result == 1) {
//                System.out.println("登录失败");
                JOptionPane.showMessageDialog(null, "用户已登录", "错误", JOptionPane.ERROR_MESSAGE);
            } else if (result == 2){
                JOptionPane.showMessageDialog(null, "服务器IP错误", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "无法连接到服务器", "错误", JOptionPane.ERROR_MESSAGE);
            }
//            if (!name.equals("")) {
////            System.out.println(name);
//            }
        } else {
            JOptionPane.showMessageDialog(null, "用户名错误", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int connect(String name, String addressStr) {
        try {
            serverAddress = InetAddress.getByName(addressStr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return 2;
        }
        try {
            socket = new DatagramSocket();
//            System.out.println("login: port" +socket.getPort());
            String message = name + "\n" + "System\n" + "login\n";
            byte[] buf = new byte[256];
            buf = message.getBytes();
//            InetAddress address = InetAddress.getByName(serverAddress);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
            socket.send(packet);
            socket.setSoTimeout(1000);
            socket.receive(packet);
            String received;
            // = new String(packet.getData());
            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData()), "utf-8"));
//            String sender = in.readLine();
//            String receiver = in.readLine();
//            String message = in.readLine();
            received = in.readLine();
            received = in.readLine();
            received = in.readLine();
//            System.out.println(received);
            if (received.equals("true")) {
                return 0;
            }
        } catch (UnknownHostException e) {
            return 2;
        } catch (Exception e) {
            return 3;
        }
        return 1;
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }

}
