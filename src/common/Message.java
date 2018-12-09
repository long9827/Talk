package common;

public class Message {
//    static public String message(String mess)
    private String sender;
    private String receiver;
    private String message;

    public Message() {
    }

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return sender + "\n" + receiver + "\n" + message + "\n";
    }
}
