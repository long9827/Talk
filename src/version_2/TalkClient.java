package version_2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TalkClient {
	public static void main(String args[]) {
		System.out.print("用户名：");
		String name = (new Scanner(System.in)).next();
		try{
			//向本机的4700端口发出客户请求
			Socket socket=new Socket("127.0.0.1",4700);

			new Thread(new Send(name, socket)).start();
			new Thread(new Receive(socket)).start();

//			while (!socket.isClosed()) {}
//			socket.close();

		}catch(Exception e) {
			System.out.println("TalkClient Error: "+e); //出错，则打印出错信息
		}
	}
}



class Send implements Runnable {

	private Socket server;
	private String name;

	public Send(String name, Socket server) {
		this.name = name;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter os = new PrintWriter(server.getOutputStream());
			String msg;
			String senderMsg = name + ": ";
			do {
				msg = sin.readLine();
				os.println(senderMsg + msg);
				os.flush();
			} while (!msg.equals("bye"));
			os.close();
		} catch (Exception e) {
			System.out.println("Send Error: " + e + "! ");
		}
	}
}

class Receive implements Runnable {

	private Socket server;

	public Receive(Socket server) {
		this.server = server;
	}

	@Override
	public void run() {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(server.getInputStream()));
			String msg;
			do {
				msg = is.readLine();
				System.out.println(msg);
			} while (!msg.equals("bye"));
			is.close();
		} catch (Exception e) {
			System.out.println("Receive Error: " + e + "! ");
		}
	}
}