package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class ChatRecordsPanel extends JPanel {
    public static final int SELF = 1;
    public static final int OTHER = 2;

//    private ArrayList<MessageRecords> messageRecords = new ArrayList<>();

    public ChatRecordsPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (int i=0; i<30; ++i) {
            add(Box.createVerticalGlue());
        }
    }

    public void addMessage(int type, String message) {
        //设置每条消息的布局，自己发送的消息在有，对方的在左
        int flowType = (type==1)? FlowLayout.RIGHT:FlowLayout.LEFT;
        JPanel item = new JPanel(new FlowLayout(flowType));
        JTextArea jTextArea = new JTextArea(message);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        item.add(jTextArea);
        int random = (int) (Math.random()*20 + 200);
        Color color = new Color(random, random, random);
        item.setBackground(color);
        jTextArea.setBackground(color);
        add(item);
        add(Box.createVerticalStrut(5));

        updateUI();
        repaint();
    }

    public static void main(String[] args) {

        boolean adjust = false;

        JFrame jFrame = new JFrame();
        jFrame.setSize(500, 400);
        jFrame.setLocationRelativeTo(null);    //居中
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ChatRecordsPanel chatRecordsPanel = new ChatRecordsPanel();
        MyJScrollPane scrollPane = new MyJScrollPane(chatRecordsPanel);

        jFrame.add(scrollPane, BorderLayout.CENTER);

        JButton button = new JButton("button");

        JPanel tmp = new JPanel();
        tmp.add(button);
        jFrame.add(tmp, BorderLayout.EAST);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                System.out.println("begin: " + chatRecordsPanel.getHeight());
                chatRecordsPanel.addMessage((int) (Math.random()*2), "hel士大夫拉解开了解开了路上的风" +
                        "景看来士大夫拉解开了解开了路上的风看来士大夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看看来士大夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看看来士大夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看看来士大夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看看来士大夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看景看来士大夫拉解开了解开了路上的风景看来士大" +
                        "夫拉解开了解开了路上的风景看来士大夫拉解开了解开了路上的风景看来士" +
                        "大夫拉解开了解开了路上的风景看来lo");
//                System.out.println("begin: " + chatRecordsPanel.getHeight());
            }
        });

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                System.out.println("e:" + e.getValueIsAdjusting());
                System.out.println(scrollPane.adjust);
                if (!scrollPane.adjust &&!e.getValueIsAdjusting()) {

                    JScrollBar jScrollBar = scrollPane.getVerticalScrollBar();
                    jScrollBar.setValue(jScrollBar.getMaximum());
                }
                scrollPane.adjust = e.getValueIsAdjusting();
            }
        });

        jFrame.setVisible(true);
    }

}

