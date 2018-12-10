package client;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class MainFrame extends JFrame {
    static private final int WIDTH = 800;
    static private final int HEIGHT = 500;

    private DatagramSocket socket;
    private String username;
    private DefaultListModel<String> friends;

    private JList<String> userList;
    private JPanel usersPanel;
    private JPanel chatPanel;
    private JLabel currentTalker;
    private JTextArea inputTF;
    private JButton sendButton;
    private ChatRecordsPanel chatRecordsPanel;
    private MyJScrollPane messageScrollPanel;

    private HashMap<String, ChatRecordsPanel> chatRecordsPanelHashMap = new HashMap<>();

    //TODO：初始化联系人列表
    private void initUsers() {
//        int random = (int) (Math.random()*500 + 1);
        int random = 20;
        for (int i=0; i<random; ++i) {
            String name = "user" + (i+1);
            friends.addElement(name);
            chatRecordsPanelHashMap.put(name, new ChatRecordsPanel());
        }
        userList.setModel(friends);
    }

    //TODO：发送消息
    private void sendMessage(String receiver, String message) {
        try {
            String string = username + "\n" + receiver + "\n" + message;
            byte[] buf = string.getBytes("utf-8");
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO：初始化窗口
    private void initMainFrame() {
        setTitle("聊天室");
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setLocationRelativeTo(null);    //居中
//        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMessage("System", "logout\n");
                System.exit(0);
            }
        });
    }

    //TODO：设置联系人面板
    private void setUserListPanel() {
        Color background = new Color(248, 248, 248);
        usersPanel = new JPanel();
        usersPanel.setBackground(background);
        usersPanel.setPreferredSize(new Dimension((int)(0.25*WIDTH), HEIGHT));
        usersPanel.setLayout(new BorderLayout(0, 5));

        //设置title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(background);
        titlePanel.add(new JLabel("联系人"));
        usersPanel.add(titlePanel, BorderLayout.NORTH);

        //设置联系人列表
        userList = new JList<>();
        initUsers();
        userList.setFixedCellHeight(19);
        JScrollPane scrollPane = new JScrollPane(userList);
        usersPanel.add(scrollPane, BorderLayout.CENTER);

        //设置刷新按钮
        JButton refresh = new JButton("刷新");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(background);
        buttonPanel.add(refresh);
        buttonPanel.setPreferredSize(new Dimension((int)(0.24*WIDTH), 50 ));
        usersPanel.add(buttonPanel, BorderLayout.SOUTH);
        refresh.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("刷新");
            }
        });

        //监听联系人变化
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (userList.getValueIsAdjusting()) {
                    String talker = userList.getSelectedValue();
                    currentTalker.setText(talker);
                    if (chatRecordsPanelHashMap.get(talker) == null) {
                        chatRecordsPanelHashMap.put(talker, new ChatRecordsPanel());
                    }
                    chatRecordsPanel = chatRecordsPanelHashMap.get(talker);
                    messageScrollPanel.setViewportView(chatRecordsPanel);
                    inputTF.setEditable(true);
                    sendButton.setVisible(true);
                }
            }
        });
    }

    //TODO：设置对话窗口
    private void setChatPanel() {
        Color background = new Color(255, 250, 250);
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        //设置当前聊天对象
        currentTalker = new JLabel("请选择联系人");
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(background);
        titlePanel.add(currentTalker);
        chatPanel.add(titlePanel, BorderLayout.NORTH);

        //设置聊天记录窗口
        chatRecordsPanel = new ChatRecordsPanel();
        messageScrollPanel = new MyJScrollPane(chatRecordsPanel);
        chatPanel.add(messageScrollPanel, BorderLayout.CENTER);
        messageScrollPanel.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (!messageScrollPanel.adjust &&!e.getValueIsAdjusting()) {
                    JScrollBar jScrollBar = messageScrollPanel.getVerticalScrollBar();
                    jScrollBar.setValue(jScrollBar.getMaximum());
                }
                messageScrollPanel.adjust = e.getValueIsAdjusting();
            }
        });

        //设置输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(background);
        //设置输入框
        inputTF = new JTextArea();
        inputTF.setLineWrap(true);
        inputTF.setWrapStyleWord(true);
        inputTF.setEditable(false);
        inputPanel.add(new JScrollPane(inputTF), BorderLayout.CENTER);
        //设置发送按钮
        sendButton = new JButton("发送");
        sendButton.setVisible(false);
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendPanel.setBackground(background);
        inputPanel.add(sendPanel, BorderLayout.SOUTH);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputTF.getText();
                if (!message.equals("")) {
                    inputTF.setText("");
                    chatRecordsPanel.addMessage(ChatRecordsPanel.SELF, message);
                    sendMessage(currentTalker.getText(), message);
                }
            }
        });

        inputPanel.setPreferredSize(new Dimension(0, (int) (0.2*HEIGHT)));
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
    }

    public MainFrame(String username, DatagramSocket socket) {
        this.friends =  new DefaultListModel<>();
        this.socket = socket;
        this.username = username;
        initMainFrame();
        setUserListPanel();
        setChatPanel();

        add(usersPanel, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);

        new Thread(new Receive(this.socket, friends, chatRecordsPanelHashMap)).start();
    }
}

class Receive implements Runnable {
    private byte[] buf = new byte[256];
    private DatagramSocket socket;
    private DefaultListModel<String> friends;
    private HashMap<String, ChatRecordsPanel> chatRecordsPanelHashMap;

    public Receive(DatagramSocket socket, DefaultListModel<String> friends, HashMap<String, ChatRecordsPanel> chatRecordsPanelHashMap) {
        this.socket = socket;
        this.friends = friends;
        this.chatRecordsPanelHashMap = chatRecordsPanelHashMap;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData()), "utf-8"));
                String sender = in.readLine();
                String receiver = in.readLine();
                StringBuffer stringBuffer = new StringBuffer();
                String line = in.readLine();
                while (line != null) {
                    stringBuffer.append(line);
                    line = in.readLine();
                    if (line != null) {
                        stringBuffer.append("\n");
                    }
                }
                System.out.println(stringBuffer);
                chatRecordsPanelHashMap.get(sender).addMessage(ChatRecordsPanel.OTHER, stringBuffer.toString());
                int index = 0;
                while (index < friends.size() && !friends.getElementAt(index).equals(sender)) { ++index; }
                if (friends.getElementAt(index).equals(sender)) {
                    friends.remove(index);
                    friends.add(0, sender);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}