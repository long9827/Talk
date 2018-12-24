package common;

import client.MainFrame;
import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FilesTransfer {
    private static int n = 1024;

    //TODO: 发送文件
    public static class SendThread extends Thread {
        private String filePath;
        private MainFrame mainFrame;

        public SendThread(String filePath, MainFrame mainFrame) {
            this.filePath = filePath;
            this.mainFrame = mainFrame;
        }

        @Override
        public void run() {
            try {
                FileInputStream rf = null;
                File file = new File(filePath);
                try {
                    rf = new FileInputStream(filePath);
                } catch (Exception e) {
                    System.out.println("Can not open the file");
                    e.printStackTrace();
                }
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(5000);
                } catch (Exception e) {
                    System.out.println("Can not listen to: 5000");
                    e.printStackTrace();
                }
                Socket socket = null;
                try {
                    //等待接收方连接
                    socket = serverSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PrintStream os = new PrintStream(socket.getOutputStream());

                os.println(file.getName());
                os.flush();
                int fileSize =  rf.available();
                String string = String.valueOf(fileSize);
                os.println(string);
                os.flush();
                byte[] buffer = new byte[n];
                OutputStream outputStream = socket.getOutputStream();


                int length;
                if (mainFrame==null) {
                    while ((length = rf.read(buffer, 0, n)) != -1) {
                        outputStream.write(buffer, 0, length);
                        outputStream.flush();
                    }
                } else {
                    JProgressBar progressBar = mainFrame.getProgressBar();
                    progressBar.setValue(90);
                    int sended = 0;
                    while ((length = rf.read(buffer, 0, n)) != -1) {
                        sended +=length;
                        progressBar.setValue((int)(10000.0*sended/fileSize));
                        outputStream.write(buffer, 0, length);
                        outputStream.flush();
                    }
                mainFrame.dialogDispose();
                }
                outputStream.close();
                rf.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: 接收文件
    public static class ReceiveThread extends Thread {
        private String filePath;
        private InetAddress address;
        private MainFrame mainFrame;

        public ReceiveThread(String filePath, InetAddress address, MainFrame mainFrame) {
            this.filePath = filePath;
            this.address = address;
            this.mainFrame = mainFrame;
        }

        @Override
        public void run() {
            receiveFile(filePath, address, mainFrame);
        }
    }

    // TODO: 接收文件
    public static boolean receiveFile(String filePath, InetAddress address, MainFrame mainFrame) {
        try {
            File dir = new File(filePath);
            if(!dir.exists()) {
                dir.mkdirs();
            }
//            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            Socket socket = null;
            try {
                socket = new Socket(address, 5000);
            } catch (Exception e) {
                System.out.println("Can not connect to the sender");
                e.printStackTrace();
                    return false;
            }
            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fileName = is.readLine();

            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "\\" + fileName);
            String file_size = is.readLine();
            int fileSize = Integer.valueOf(file_size);
            byte[] buffer = new byte[n];
            InputStream inputStream = socket.getInputStream();
            int length;
            if (mainFrame==null) {
//                System.out.println("开始接收");
                while ((length = inputStream.read(buffer, 0, n)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                }
            } else {
//                System.out.println("开始接收");
                int sended = 0;
                JProgressBar progressBar = mainFrame.getReceiveBar();
                while ((length = inputStream.read(buffer, 0, n)) != -1) {
                    sended += length;
                    progressBar.setValue((int)(10000.0*sended/fileSize));
                    fileOutputStream.write(buffer, 0, length);
                }
                mainFrame.receiveDialogDispose();
            }
            inputStream.close();
            fileOutputStream.close();
            socket.close();
//            System.out.println("接收成功");
                return true;
        } catch (Exception e) {
            e.printStackTrace();
                return false;
        }
    }
}
