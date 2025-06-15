package example.model;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 2L;

    private final String command;
    private final Serializable payload;
    private final String messageId;

    public Message(String command, Serializable payload, String messageId) {
        this.command = command;
        this.payload = payload;
        this.messageId = messageId;
    }

    public String getCommand() {
        return command;
    }

    public Serializable getPayload() {
        return payload;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "command='" + command + '\'' +
                ", payload=" + payload +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}