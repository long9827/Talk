package version_1;

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
		try{
			String line;
			//由Socket对象得到输入流，并构造相应的BufferedReader对象
			BufferedReader is_1=new BufferedReader(new
					InputStreamReader(client_1.getInputStream()));
			BufferedReader is_2=new BufferedReader(new
					InputStreamReader(client_2.getInputStream()));
			//由Socket对象得到输出流，并构造PrintWriter对象
			PrintWriter os_1=new PrintWriter(client_1.getOutputStream());
			PrintWriter os_2=new PrintWriter(client_2.getOutputStream());
			//由系统标准输入设备构造BufferedReader对象
//			BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
//			//在标准输出上打印从客户端读入的字符串
//			System.out.println("Client:"+ clientnum +is.readLine());
			//从标准输入读入一字符串
			String clientName_1 = is_1.readLine();
			String clientName_2 = is_2.readLine();
//			line=sin.readLine();
			String message_1 = "hello";
			String message_2 = "hello";
			while((!message_1.equals("bye"))&&(!message_2.equals("bye"))) {//如果该字符串为 "bye"，则停止循环
				os_1.println(clientName_2 + ": " + message_2);
				os_1.flush();
				System.out.println(clientName_2 + ": " + message_2);
				message_1 = is_1.readLine();
				os_2.println(clientName_1 + ": " + message_1);
				os_2.flush();
				System.out.println(clientName_1 + ": " + message_1);
				message_2 = is_2.readLine();
			}//继续循环
			os_1.println(clientName_2 + ": " + message_2);
			os_1.flush();
			os_1.close(); //关闭Socket输出流
			os_2.close(); //关闭Socket输出流
			is_1.close(); //关闭Socket输入流
			is_2.close(); //关闭Socket输入流
			client_1.close(); //关闭Socket
			client_2.close();
		}catch(Exception e){
			System.out.println("Error:"+e);//出错，打印出错信息
		}
	}
}
