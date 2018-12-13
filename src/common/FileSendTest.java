package common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileSendTest {
    public static void main(String[] args) throws UnknownHostException {
        FilesTransfer.sendFile("C:\\Users\\Administrator\\IdeaProjects\\Talk\\src\\text.txt", InetAddress.getLocalHost());
    }
}
