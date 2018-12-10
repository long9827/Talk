import server.QuoteServerThread;

public class ServerMain {
    public static void main(String[] args) throws java.io.IOException {
        new QuoteServerThread().start();
    }
}
