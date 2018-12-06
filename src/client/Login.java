package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Login extends JFrame {
    private Container container;
    private JPanel jPanel = new JPanel();
    private JButton jButton = new JButton();
    private JTextField userName = new JTextField();

    public Login() {
        jbInit();
    }
    private void jbInit() {
        int windowWidth = 350;
        int windowHeight = 250;
        container = getContentPane();
        this.setSize(new Dimension(windowWidth, windowHeight));
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.setLocation((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);
        this.setTitle("登录");
        jButton.setText("登录");
        jButton.setBounds(0,0, 30, 30);
        userName.setText("           ");
        userName.setBounds(30, 0, 50, 30);
//        container.add(jTextField);
        jPanel.add(jButton);
        jPanel.add(userName);
        this.add(jPanel);
        this.setVisible(true);
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(userName.getText());
            }
        });
    }

    public static void main(String[] args) {
        Login login = new Login();
        login.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

}
