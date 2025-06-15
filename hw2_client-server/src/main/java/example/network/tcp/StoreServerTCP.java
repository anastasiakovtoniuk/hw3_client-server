package example.network.tcp;

import example.model.Message;
import example.processor.Processor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class StoreServerTCP {
    private static final int PORT = 7755;

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                new ClientHandler(clientSocket).start();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final Processor processor = new Processor();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
            ) {
                Message request;
                while ((request = (Message) in.readObject()) != null) {
                    System.out.println("Received from [" + clientSocket.getRemoteSocketAddress() + "]: " + request);
                    Message response = processor.process(request);
                    out.writeObject(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " disconnected.");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) { /* ignore */ }
            }
        }
    }
}