package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Login extends JFrame implements ActionListener {
    private JTextField userName;
    private JButton loginButton;

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

    }

    public void actionPerformed(ActionEvent e) {
        String name = userName.getText();
        if (!name.equals("")) {
            System.out.println(name);
            setVisible(false);
            new MainFrame(name).setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        new Login().setVisible(true);
    }

}
