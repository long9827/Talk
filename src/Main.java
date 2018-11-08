import version_2.Transpond;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        new Thread(new Test()).start();
        Scanner in = new Scanner(System.in);
        String msg;
        while (true) {
            msg = in.nextLine();
            System.out.println(msg);
        }
    }
}

class Test implements Runnable {

    @Override
    public void run() {
        int i=0;
        while (true) {
            System.out.println(++i);
        }
    }
}