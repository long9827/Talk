package version_2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread{
	Socket client_1;
	Socket client_2;

	public ServerThread(Socket client_1, Socket client_2) {
		this.client_1 = client_1;
		this.client_2 = client_2;
	}

	public void run() { //线程主体
		new Thread(new Transpond(client_1, client_2)).start();
		new Thread(new Transpond(client_2, client_1)).start();
//		while (!client_1.isClosed() && !client_2.isClosed()) {}
//		try {
//			client_1.close();
//			client_2.close();
//		} catch (Exception e) {
//			System.out.println("Error: " + e + "! ");
//		}
	}
}
