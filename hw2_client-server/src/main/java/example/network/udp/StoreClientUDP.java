package example.network.udp;

import example.model.Message;
import example.model.Product;

import java.io.*;
import java.net.*;
import java.io.Serializable;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class StoreClientUDP {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8855;
    private static final int TIMEOUT = 2000; // 2 секунди
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner userInput = new Scanner(System.in)) {

            socket.setSoTimeout(TIMEOUT);
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            printHelp();

            while (true) {
                System.out.print("> ");
                String line = userInput.nextLine();
                if ("exit".equalsIgnoreCase(line)) break;

                try {
                    Message requestMessage = parseCommand(line);
                    if (requestMessage == null) {
                        printHelp();
                        continue;
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(requestMessage);
                    byte[] sendData = baos.toByteArray();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);

                    boolean ackReceived = false;
                    for (int i = 0; i < MAX_RETRIES && !ackReceived; i++) {
                        socket.send(sendPacket);
                        System.out.println("Sent: " + requestMessage.getCommand() + " (Attempt " + (i + 1) + ")");

                        try {
                            byte[] receiveBuffer = new byte[4096];
                            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                            socket.receive(receivePacket);

                            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
                            ObjectInputStream ois = new ObjectInputStream(bais);
                            Message responseMessage = (Message) ois.readObject();

                            if (requestMessage.getMessageId().equals(responseMessage.getMessageId())) {
                                System.out.print("Server response: ");
                                handlePayload(responseMessage.getPayload());
                                ackReceived = true;
                            }

                        } catch (SocketTimeoutException e) {
                            System.err.println("Timeout waiting for response. Retrying...");
                        }
                    }

                    if (!ackReceived) {
                        System.err.println("Server did not respond after " + MAX_RETRIES + " attempts.");
                    }

                } catch (Exception e) {
                    System.err.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client shutting down.");
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
                    throw new IllegalArgumentException("Expected 5 arguments for ADD_PRODUCT: ADD_PRODUCT <name> <quantity> <price> <category>");
                }
                String name = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                double price = Double.parseDouble(parts[3]);
                String category = parts[4];
                Product product = new Product(name, quantity, price, category);
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
        System.out.println("Commands:");
        System.out.println("  GET_PRODUCTS");
        System.out.println("  ADD_PRODUCT <name> <quantity> <price> <category>");
        System.out.println("  exit");
    }
}