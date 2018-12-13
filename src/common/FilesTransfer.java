package common;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class FilesTransfer {

    // TODO: 发送文件
    public static void sendFile(String filePath, InetAddress address) {
        try {
            FileInputStream rf = null;
            try {
                rf = new FileInputStream(filePath);
            } catch (Exception e) {
                System.out.println("Can not open the file");
            }
            //连接接收方
            Socket sender = new Socket(address, 5000);
            int n = 512;
//            int c = 0;
            byte[] buffer = new byte[n];
            OutputStream outputStream = sender.getOutputStream();
            int length = 0;
            while ((length = rf.read(buffer, 0, n)) != -1) {
//                System.out.println();
                System.out.println(buffer);
                outputStream.write(buffer, 0, length);
                outputStream.flush();
            }
            outputStream.close();
            sender.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // TODO: 接收文件
    public static void receiveFile(String fileName) {
        try {
            ServerSocket receiver = null;
            try {
                System.out.println("ok1");
                receiver = new ServerSocket(5000);
                System.out.println("ok2");
            } catch (Exception e) {
                System.out.println("Can not listen to: " + e);
            }
            Socket socket = null;
            try {
                socket = receiver.accept();
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            int n = 512;
            byte[] buffer = new byte[n];
            InputStream inputStream = socket.getInputStream();
            int length = 0;
            while ((length = inputStream.read(buffer, 0, n)) != -1) {
                System.out.println(buffer);
                fileOutputStream.write(buffer, 0, length);
//                if () {
//                    break;
//                }
            }
//            fileOutputStream.close();
            inputStream.close();
            receiver.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public static void main(String[] args) {
//        try {
//            System.out.print("Input: ");
//            int count, n=512;
//            byte[] buffer = new byte[n];
//            count = System.in.read(buffer);
//            FileOutputStream w
//
//        }
//    }

}
