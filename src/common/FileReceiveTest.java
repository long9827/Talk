package common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileReceiveTest {
    public static void main(String[] args) throws UnknownHostException {
        FilesTransfer.receiveFile("C:\\Users\\36010\\Desktop\\javaT\\416.94-notebook-win10-64bit-international-whql.exe", InetAddress.getLocalHost());
    }
}
