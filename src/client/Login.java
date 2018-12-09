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

public class Login extends JFrame implements ActionListener {
    private JTextField userName;
    private JButton loginButton;
    private DatagramSocket socket;

    public Login() {
        setTitle("登录");
        setSize(300, 180);
        setLocationRelativeTo(null);    //窗口居中
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JPanel jPanel = new JPanel(new GridLayout(2, 1));

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

//        try {
//            socket = new DatagramSocket();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void actionPerformed(ActionEvent e) {
        String name = userName.getText();
        if (!name.equals("")) {
            System.out.println(name);
            if (connect(name)) {
                //登录成功
                setVisible(false);
                new MainFrame(name, socket).setVisible(true);
                dispose();
            }
        }
    }

    private boolean connect(String name) {
        try {
            socket = new DatagramSocket();
            System.out.println("login: port" +socket.getPort());
            String message = name + "\n" + "System\n" + "login\n";
            byte[] buf = new byte[256];
            buf = message.getBytes();
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);
            socket.receive(packet);
            String received;
            // = new String(packet.getData());
            BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData())));
//            String sender = in.readLine();
//            String receiver = in.readLine();
//            String message = in.readLine();
            received = in.readLine();
            received = in.readLine();
            received = in.readLine();
            System.out.println(received);
            if (received.equals("true")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }

}
