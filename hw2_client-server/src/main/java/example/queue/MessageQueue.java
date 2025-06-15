package example.queue;

import example.model.Packet; // Створимо цей клас наступним
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private static final MessageQueue INSTANCE = new MessageQueue();
    private final BlockingQueue<Packet> queue = new LinkedBlockingQueue<>();

    private MessageQueue() {}

    public static MessageQueue getInstance() {
        return INSTANCE;
    }

    public void add(Packet packet) throws InterruptedException {
        queue.put(packet);
    }

    public Packet take() throws InterruptedException {
        return queue.take();
    }
}