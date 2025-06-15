package example.network.tcp;

import example.model.Message;
import example.model.Product;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class StoreClientTCP {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 7755;
    private static final int RECONNECT_INTERVAL = 5000;

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);

        while (true) {
            try (
                    Socket socket = connectWithRetry();
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                System.out.println("Connection established!");
                printHelp();

                while (true) {
                    System.out.print("> ");
                    String line = userInput.nextLine();
                    if ("exit".equalsIgnoreCase(line)) return;

                    try {
                        Message request = parseCommand(line);
                        if (request == null) {
                            printHelp();
                            continue;
                        }

                        out.writeObject(request);
                        Message response = (Message) in.readObject();

                        System.out.print("Server response: ");

                        handlePayload(response.getPayload());

                    } catch (IllegalArgumentException e) {
                        System.err.println("Input error: " + e.getMessage());
                    } catch (Exception e) {
                        throw new IOException("Connection lost", e);
                    }
                }
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage() + ". Retrying...");
            }
        }
    }


    private static Message parseCommand(String line) {
        String[] parts = line.split(" ");
        String command = parts[0].toUpperCase();
        String messageId = UUID.randomUUID().toString();

        switch (command) {
            case "GET_PRODUCTS":
                return new Message(command, null, messageId);
            case "ADD_PRODUCT":
                if (parts.length != 5) {
                    throw new IllegalArgumentException("Expected 5 arguments for ADD_PRODUCT");
                }
                Product product = new Product(parts[1], Integer.parseInt(parts[2]), Double.parseDouble(parts[3]), parts[4]);
                return new Message(command, product, messageId);
            default:
                return null;
        }
    }


    private static void handlePayload(Serializable payload) {
        if (payload instanceof List) {
            List<?> list = (List<?>) payload;
            if (list.isEmpty()) {
                System.out.println("No products found.");
            } else {
                System.out.println("Product list:");
                list.forEach(item -> System.out.println("  - " + item));
            }
        } else if (payload != null) {
            System.out.println(payload.toString());
        } else {
            System.out.println("Received empty payload.");
        }
    }

    private static void printHelp() {
        System.out.println("Commands:\n  GET_PRODUCTS\n  ADD_PRODUCT <name> <quantity> <price> <category>\n  exit");
    }


    private static Socket connectWithRetry() throws IOException {
        while (true) {
            try {
                return new Socket(SERVER_ADDRESS, SERVER_PORT);
            } catch (IOException e) {
                System.err.println("Failed to connect. Retrying in " + RECONNECT_INTERVAL / 1000 + "s.");
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Connection interrupted", ie);
                }
            }
        }
    }
}