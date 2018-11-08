package version_2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Transpond implements Runnable{

    private Socket sender;
    private Socket receiver;

    public Transpond(Socket sender, Socket receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            BufferedReader is = new BufferedReader(new InputStreamReader(sender.getInputStream()));
            PrintWriter os = new PrintWriter(receiver.getOutputStream());
            String msg;
            do {
                msg = is.readLine();
                os.println(msg);
                os.flush();
            } while (!msg.equals("bye"));
            System.out.println("end");
            is.close();
            os.close();
        } catch (Exception e) {
            System.out.println("Error: " + e + "! ");
        }

    }
}
