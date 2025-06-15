package example.processor;

import example.model.Message;
import example.model.Packet;
import example.network.Sender;
import example.queue.MessageQueue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


import example.model.Product;
import example.storage.InventoryService;


public class Processor extends Thread {
    private final Sender sender;
    private final InventoryService inventoryService;
    private final MessageQueue messageQueue = MessageQueue.getInstance();
    private volatile boolean running = true;


    public Processor(Sender sender, InventoryService inventoryService) {
        this.sender = sender;
        this.inventoryService = inventoryService;
    }

    @Override
    public void run() {
        System.out.println("Processor has started.");
        while (running) {
            try {
                Packet requestPacket = messageQueue.take();


                ByteArrayInputStream bais = new ByteArrayInputStream(requestPacket.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Message requestMessage = (Message) ois.readObject();

                System.out.println("Processor is handling command: " + requestMessage.getCommand());


                Serializable responsePayload = processCommand(requestMessage);
                Message responseMessage = new Message("RESPONSE", responsePayload, requestMessage.getMessageId());


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(responseMessage);
                byte[] responseBytes = baos.toByteArray();


                Packet responsePacket = new Packet(responseBytes, requestPacket.getAddress(), requestPacket.getPort());
                if (requestPacket.getTcpResponseStream() != null) {
                    responsePacket.setTcpResponseStream(requestPacket.getTcpResponseStream());
                }


                sender.send(responsePacket);

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Processor error: " + e.getMessage());
            }
        }
        System.out.println("Processor has stopped.");
    }


    private Serializable processCommand(Message request) {
        String command = request.getCommand().toUpperCase();
        switch (command) {
            case "GET_PRODUCTS":
                return (Serializable) inventoryService.getProducts();
            case "ADD_PRODUCT":
                if (request.getPayload() instanceof Product) {
                    return inventoryService.addProduct((Product) request.getPayload());
                }
                return "ERROR: Invalid payload for ADD_PRODUCT.";
            default:
                return "ERROR: Unknown command.";
        }
    }

    public void stopProcessing() {
        running = false;
        this.interrupt();
    }
}