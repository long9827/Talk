package common;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.NumberFormat;

public class FilesTransfer {
    private static int n = 1024;

    // TODO: 发送文件
    public static boolean sendFile(String filePath, String receiver) {
        try {
            FileInputStream rf = null;
            File file = new File(filePath);
            try {
                rf = new FileInputStream(filePath);
            } catch (Exception e) {
                System.out.println("Can not open the file");
                e.printStackTrace();
                return false;
            }
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(5000);
            } catch (Exception e) {
                System.out.println("Can not listen to: 5000");
                e.printStackTrace();
                return false;
            }
            Socket socket = null;
            try {
                //等待接收方连接
                socket = serverSocket.accept();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            PrintStream os = new PrintStream(socket.getOutputStream());

            os.println(file.getName());
            os.flush();
            int fileSize =  rf.available();
            String string = String.valueOf(fileSize);
            os.println(string);
            os.flush();
            System.out.println(fileSize);
//            os.close();
            byte[] buffer = new byte[n];
            OutputStream outputStream = socket.getOutputStream();


            int length;
            int i = 0;
            double fenmu = new Double(Integer.valueOf(fileSize))/(100*n);
            while ((length = rf.read(buffer, 0, n)) != -1) {
//                System.out.println(buffer);
                System.out.println("已完成" + String.format("%.2f", ++i/fenmu) + "%");
                outputStream.write(buffer, 0, length);
                outputStream.flush();
            }
            outputStream.close();
            socket.close();
            serverSocket.close();
            System.out.println("发送成功");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // TODO: 接收文件
    public static boolean receiveFile(String filePath, InetAddress address) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
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
            String fileSize = is.readLine();
            System.out.println(fileName);
            System.out.println(fileSize);
//            is.close();
            byte[] buffer = new byte[n];
            InputStream inputStream = socket.getInputStream();
            int length;
            int i=0;
            double fenmu = new Double(Integer.valueOf(fileSize))/(100*n);
            while ((length = inputStream.read(buffer, 0, n)) != -1) {
//                System.out.println(buffer);
                System.out.println("已完成" + String.format("%.2f", ++i/fenmu) + "%");
                fileOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            socket.close();
            System.out.println("接收成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
