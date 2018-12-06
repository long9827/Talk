package AWT;

import java.awt.*;

public class Login {
    public static void main(String[] args) {
        Frame frame = new Frame("User Login");
        frame.setSize(280, 150);
        frame.setLayout(null);
        Button button = new Button("login");
        button.setBounds((frame.getWidth()-100)/2, (frame.getHeight()-50)/2, 100, 50);
        frame.add(button);
        frame.setBackground(Color.lightGray);
        frame.setVisible(true);
    }
}
