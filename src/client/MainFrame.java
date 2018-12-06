package client;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {
    private JList<String> userList = new JList<>();

    private void refreshUsers() {
        DefaultListModel<String> dlm = new DefaultListModel<>();
        int random = (int) (Math.random()*20 + 1);
        for (int i=0; i<random; ++i) {
            dlm.addElement("user" + String.valueOf(i+1));
        }
        userList.setModel(dlm);
        userList.setFixedCellWidth(130);
    }

    public MainFrame(String username) {
        setTitle("聊天室");
        setSize(500, 400);
        setLocationRelativeTo(null);    //居中
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel usersPanel = new JPanel();
        usersPanel.setSize(150, 400);
        JPanel talkPanel = new JPanel();
        talkPanel.setSize(250, 400);
        talkPanel.setBackground(Color.lightGray);


        refreshUsers();

        JScrollPane scrollPane = new JScrollPane(userList);
        userList.setVisibleRowCount(16);
        userList.setFixedCellHeight(20);
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(userList.getSelectedValue());
            }
        });
        usersPanel.add(scrollPane);

        //设置刷新按钮
        JButton refresh = new JButton("刷新");
        usersPanel.add(refresh);
        refresh.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userList.getValueIsAdjusting()) {
                    refreshUsers();
                }
            }
        });

        add(usersPanel);
        add(talkPanel);

    }
}
