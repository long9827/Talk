package client;

import common.FilesTransfer;
import jdk.nashorn.internal.scripts.JO;

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

public class MainFrame extends JFrame implements ActionListener {
    static private final int WIDTH = 800;
    static private final int HEIGHT = 500;

    private DatagramSocket socket;
    private InetAddress serverAddress;
    private String username;
    private DefaultListModel<String> friends;

    private JList<String> userList;
    private MyRenderer myRenderer;
    private JPanel usersPanel;
    private JPanel chatPanel;
    private JLabel currentTalker;
    private JTextArea inputTF;
    private JButton sendButton;
    private JButton sendFileButton;
    private ChatRecordsPanel chatRecordsPanel;
    private MyJScrollPane messageScrollPanel;

    //发送文件
    private JDialog dialog;
    private JButton cancel;
    private JButton selectFile;
    private JButton sendFile;
    private JTextField filePath;
    //进度条
    private JProgressBar progressBar;

    //接收文件
    private JDialog pathDialog;
    private JButton selectPath;
    private JButton receiveFile;
    private JTextField rfPath;
    private String fileName;
    //进度条
    private JProgressBar receiveBar;

    private HashMap<String, ChatRecordsPanel> chatRecordsPanelHashMap = new HashMap<>();

    //TODO：初始化联系人列表
    private void initUsers() {
        int random = 20;
        for (int i=0; i<random; ++i) {
            String name = "user" + (i+1);
            if (name.equals(username)) {
                continue;
            }
            friends.addElement(name);
            chatRecordsPanelHashMap.put(name, new ChatRecordsPanel());
        }
        userList.setModel(friends);
        myRenderer = new MyRenderer(Color.red);
        userList.setCellRenderer(myRenderer);
//        myRenderer.addRow(5);
    }

    //TODO：发送消息
    void sendMessage(String receiver, String message) {
        try {
            String string = username + "\n" + receiver + "\n" + message;
            byte[] buf = string.getBytes("utf-8");
//            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddress, 4445);
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
                    int index = userList.getSelectedIndex();
                    myRenderer.removeRow(index);
                    String talker = userList.getSelectedValue();
                    currentTalker.setText(talker);
                    if (chatRecordsPanelHashMap.get(talker) == null) {
                        chatRecordsPanelHashMap.put(talker, new ChatRecordsPanel());
                    }
                    chatRecordsPanel = chatRecordsPanelHashMap.get(talker);
                    messageScrollPanel.setViewportView(chatRecordsPanel);
                    inputTF.setEditable(true);
                    sendButton.setVisible(true);
                    sendFileButton.setVisible(true);
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
        //设置文件发送按钮
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendFileButton = new JButton("发送文件");
        sendFileButton.setVisible(false);
        sendPanel.add(sendFileButton, BorderLayout.WEST);
        //设置发送按钮
        sendButton = new JButton("发送");
        sendButton.setVisible(false);
        sendPanel.add(sendButton, BorderLayout.EAST);
        sendPanel.setBackground(background);
        inputPanel.add(sendPanel, BorderLayout.SOUTH);
        //添加监听事件
        sendFileButton.addActionListener(this);
        sendButton.addActionListener(this);

        inputPanel.setPreferredSize(new Dimension(0, (int) (0.2*HEIGHT)));
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
    }

    // TODO: 监听事件
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendFileButton) {
            //打开发送文件窗口
            setFileDialog();
        } else if (e.getSource() == sendButton) {
            //发送消息
            String message = inputTF.getText();
            if (!message.equals("")) {
                inputTF.setText("");
                chatRecordsPanel.addMessage(ChatRecordsPanel.SELF, message);
                sendMessage(currentTalker.getText(), message);
            }
        } else if (e.getSource() == cancel) {
            //取消发送文件
            dialog.dispose();
        } else if (e.getSource() == selectFile) {
            //选择文件
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.showDialog(new JLabel(), "选择文件");
            File file = jFileChooser.getSelectedFile();
            filePath.setText(file.getAbsolutePath());
        } else if (e.getSource() == sendFile) {
            //发送文件
            String path = filePath.getText();
            File file = new File(path);
            if (file.exists()) {
                //文件存在
                String fileName = file.getName();
                String fileReceiver = currentTalker.getText();
                String message = "sendFile\n" + fileReceiver + "\n" + fileName + "\n";
                sendMessage("System", message);
                new FilesTransfer.SendThread(path, this).start();
                selectFile.setVisible(false);
                cancel.setVisible(false);
                sendFile.setVisible(false);
                progressBar.setVisible(true);
            } else {
                //文件不存在
                JOptionPane.showMessageDialog(null, "文件不存在", "提示", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == selectPath) {
            //选择文件存放目录
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jFileChooser.showDialog(new JLabel(), "选择目录");
            File file = jFileChooser.getSelectedFile();
            String savePath = file.getAbsolutePath() + fileName;
            rfPath.setText(file.getAbsolutePath());
        } else if (e.getSource() == receiveFile) {
            String path = rfPath.getText();
            if (path.equals("")) {
                //默认路径
                path = "E:\\talkFile\\receive";
            }
            try {
                receiveBar.setVisible(true);
                receiveFile.setVisible(false);
                selectPath.setVisible(false);
//                new FilesTransfer.ReceiveThread(path + "\\" + fileName, InetAddress.getLocalHost(), this).start();
                new FilesTransfer.ReceiveThread(path, serverAddress, this).start();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            System.out.println("Unknown error");
        }
    }

    // Private Methods
    private void setFileDialog() {
        dialog = new JDialog(this, "选择文件");
        Container container = dialog.getContentPane();
        dialog.setBounds(0, 0, 300, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());
        JPanel pathPanel = new JPanel();
        filePath = new JTextField();
        filePath.setColumns(20);
        filePath.setEditable(false);
        pathPanel.add(new JLabel("文件目录"));
        pathPanel.add(filePath);
        container.add(pathPanel, BorderLayout.NORTH);
        selectFile = new JButton("选择文件");
        JPanel selectPanel = new JPanel();
        selectPanel.add(selectFile);
        container.add(selectPanel, BorderLayout.CENTER);

        JPanel pBarandBtn = new JPanel(new BorderLayout());
        pBarandBtn.setPreferredSize(new Dimension(0, 50));
        progressBar = new JProgressBar(0, 10000);
        progressBar.setVisible(false);
        pBarandBtn.add(progressBar, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();

        cancel = new JButton("取消");
        sendFile = new JButton("发送");
        buttonPanel.add(cancel);
        buttonPanel.add(sendFile);

        pBarandBtn.add(buttonPanel, BorderLayout.SOUTH);
        container.add(pBarandBtn, BorderLayout.SOUTH);

        //添加监听事件
        selectFile.addActionListener(this);
        sendFile.addActionListener(this);
        cancel.addActionListener(this);

        dialog.setVisible(true);
    }

    void selectPathDialog(String fileName) {
        this.fileName = fileName;
        pathDialog = new JDialog(this, "选择目录");
        Container container = pathDialog.getContentPane();
        pathDialog.setBounds(0, 0, 300, 150);
        pathDialog.setLocationRelativeTo(null);
        pathDialog.setModal(true);
        pathDialog.setResizable(false);
        pathDialog.setLayout(new BorderLayout());
//        pathDialog.setDefaultCloseOperation(Window.);

        JPanel pathPanel = new JPanel();
        rfPath = new JTextField();
        rfPath.setColumns(20);
        rfPath.setEditable(false);
        rfPath.setText("E:\\talkFile\\receive");

        pathPanel.add(new JLabel("保存目录"));
        pathPanel.add(rfPath);
        container.add(pathPanel, BorderLayout.NORTH);
        selectPath = new JButton("选择目录");
        JPanel selectPanel = new JPanel();
        selectPanel.add(selectPath);
        container.add(selectPanel, BorderLayout.CENTER);

        JPanel pBarandBtn = new JPanel(new BorderLayout());
        pBarandBtn.setPreferredSize(new Dimension(0, 50));
        receiveBar = new JProgressBar(0, 10000);
        receiveBar.setVisible(false);
        pBarandBtn.add(receiveBar, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(0, 40));

        receiveFile = new JButton("接收");
        buttonPanel.add(receiveFile);

        pBarandBtn.add(buttonPanel, BorderLayout.SOUTH);
        container.add(pBarandBtn, BorderLayout.SOUTH);

        //添加监听事件
        selectPath.addActionListener(this);
        receiveFile.addActionListener(this);
        pathDialog.setVisible(true);
    }

    public void dialogDispose() {
        dialog.dispose();
    }

    public void receiveDialogDispose() {
        pathDialog.dispose();
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public DefaultListModel<String> getFriends() {
        return friends;
    }

    public HashMap<String, ChatRecordsPanel> getChatRecordsPanelHashMap() {
        return chatRecordsPanelHashMap;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JProgressBar getReceiveBar() {
        return receiveBar;
    }

    public MyRenderer getMyRenderer() {
        return myRenderer;
    }

    public MainFrame(String username, InetAddress serverAddress, DatagramSocket socket) {
        this.friends =  new DefaultListModel<>();
        this.socket = socket;
        this.username = username;
        this.serverAddress = serverAddress;
        initMainFrame();
        setUserListPanel();
        setChatPanel();

        add(usersPanel, BorderLayout.WEST);
        add(chatPanel, BorderLayout.CENTER);

        new Thread(new Receive(this)).start();
    }
}

class Receive implements Runnable {
    private byte[] buf = new byte[256];
    private MainFrame mainFrame;
    private DatagramSocket socket;
    private DefaultListModel<String> friends;
    private HashMap<String, ChatRecordsPanel> chatRecordsPanelHashMap;

    public Receive(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        socket = mainFrame.getSocket();
        friends = mainFrame.getFriends();
        chatRecordsPanelHashMap = mainFrame.getChatRecordsPanelHashMap();
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(0);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());
                BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData()), "utf-8"));
                String sender = in.readLine();
                String receiver = in.readLine();
                if (sender.equals("System")) {
                    //系统消息
                    String cmd = in.readLine();
                    if (cmd.equals("PleaseReceiveFile")) {
                        //请求接收文件
                        String fileSender = in.readLine();
                        String fileName = in.readLine();
                        System.out.println("fileSender: " + fileSender + " fileName: " + fileName);
                        String string = fileSender + "发送文件：" + fileName;
                        if (JOptionPane.showConfirmDialog(null, string, "是否接收", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {

                            String message = "receiveFile\n" + fileName + "\n";
                            mainFrame.sendMessage("System", message);
                        }
                    } else if (cmd.equals("CanReceiveFile")) {
                        System.out.println("CanReceiveFile");
                        String fileName = in.readLine();
                        mainFrame.selectPathDialog(fileName);
                    }
                } else {
                    StringBuffer stringBuffer = new StringBuffer();
                    String line = in.readLine();
                    while (line != null) {
                        stringBuffer.append(line);
                        line = in.readLine();
                        if (line != null) {
                            stringBuffer.append("\n");
                        }
                    }
                    chatRecordsPanelHashMap.get(sender).addMessage(ChatRecordsPanel.OTHER, stringBuffer.toString());
                    int index = 0;
                    while (index < friends.size() && !friends.getElementAt(index).equals(sender)) { ++index; }
                    if (friends.getElementAt(index).equals(sender)) {
                        friends.remove(index);
                        friends.add(0, sender);
                        mainFrame.getMyRenderer().setColor(index);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
