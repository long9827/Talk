package server;

public class QuoteServer {
    public static void main(String[] args) throws java.io.IOException {
        new QuoteServerThread().start();
    }
}
