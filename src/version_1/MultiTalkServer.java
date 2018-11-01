package version_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiTalkServer{
	static int clientnum=0; //静态成员变量，记录当前客户的个数
	public static void main(String args[]) throws IOException {
		ServerSocket serverSocket=null;
		boolean listening=true;
		try{
			//创建一个ServerSocket在端口4700监听客户请求
			serverSocket=new ServerSocket(4700);
		}catch(IOException e) {
			System.out.println("Could not listen on port:4700.");
			//出错，打印出错信息
			System.exit(-1); //退出
		}
		while(listening){ //循环监听
			Socket client_1 = serverSocket.accept();
			Socket client_2 = serverSocket.accept();
			new ServerThread(client_1, client_2).start();
			clientnum+=2; //增加客户计数
		}
		serverSocket.close(); //关闭ServerSocket
	}
}
