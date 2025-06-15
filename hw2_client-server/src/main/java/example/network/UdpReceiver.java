package example.network;

import example.model.Packet;
import example.queue.MessageQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpReceiver implements Receiver {
    private final int port;
    private final MessageQueue queue = MessageQueue.getInstance();
    private volatile boolean running = true;
    private DatagramSocket socket;

    public UdpReceiver(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("UDP Receiver is starting on port " + port);
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[4096];
            while(running) {
                DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(udpPacket);

                byte[] data = new byte[udpPacket.getLength()];
                System.arraycopy(udpPacket.getData(), 0, data, 0, udpPacket.getLength());


                Packet packet = new Packet(data, udpPacket.getAddress(), udpPacket.getPort());
                queue.add(packet);
            }
        } catch (Exception e) {
            if(running) {
                System.err.println("UDP Receiver error: " + e.getMessage());
            }
        } finally {
            if(socket != null) socket.close();
            System.out.println("UDP Receiver has stopped.");
        }
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }
}