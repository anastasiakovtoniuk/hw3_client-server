package example.network;

import example.model.Packet;
import example.queue.MessageQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpReceiver implements Receiver {
    private final int port;
    private final MessageQueue queue = MessageQueue.getInstance();
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public TcpReceiver(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("TCP Receiver is starting on port " + port);
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("TCP Receiver error: " + e.getMessage());
            }
        } finally {
            System.out.println("TCP Receiver has stopped.");
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                InputStream in = clientSocket.getInputStream();

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] data = new byte[bytesRead];
                System.arraycopy(buffer, 0, data, 0, bytesRead);

                Packet packet = new Packet(data);
                packet.setTcpResponseStream(out);
                queue.add(packet);
            }
        } catch (Exception e) {
            System.err.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
        }
    }

    @Override
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {

        }
    }
}