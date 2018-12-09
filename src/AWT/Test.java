package AWT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame {
    private JButton refresh = new JButton("refresh");
    private JPanel jPanelList;
    private JScrollPane scrollPane;
    private int i = 0;


    private void scrollToEnd() {
        Point p = new Point();
        p.setLocation(0, jPanelList.getHeight() + 500);
        scrollPane.getViewport().setViewPosition(p);
    }

    public Test() {
        setSize(500, 400);
        setLocationRelativeTo(null);    //居中
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        jPanelList = new JPanel();
        jPanelList.setLayout(new BoxLayout(jPanelList, BoxLayout.Y_AXIS));
        for (int k=0; k<20; ++k) {
            jPanelList.add(Box.createVerticalGlue());
        }
//        jPanelList.add(Box.createVerticalGlue());
//        jPanelList.add(Box.createVerticalStrut(50));
        scrollPane = new JScrollPane(jPanelList);
        add(scrollPane, BorderLayout.CENTER);


        JPanel tmp = new JPanel();
        tmp.add(refresh);
        add(tmp, BorderLayout.EAST);

        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("begin: " + jPanelList.getHeight());
//                scrollPane.add(new JLabel("refresh"));
                JPanel tmp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JTextArea jTextArea = new JTextArea();
                jTextArea.setText("Panel\nLabel\nPanel\nLabel\nPanel\nLabel\n" + ++i);
                jTextArea.setEditable(false);
                tmp.add(jTextArea);
                tmp.setBackground(new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255)));
                jPanelList.add(tmp);
                jPanelList.add(Box.createVerticalStrut(5));
//                jPanelList.updateUI();
//                jPanelList.repaint();

                scrollToEnd();



                System.out.println("end: " + jPanelList.getHeight());
            }
        });
        System.out.println(jPanelList.getHeight());
    }

    public static void main(String[] args) {
        new Test().setVisible(true);
    }
}
