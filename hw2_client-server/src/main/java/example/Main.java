package example;

import example.network.Receiver;
import example.network.Sender;
import example.network.TcpReceiver;
import example.network.UdpReceiver;
import example.processor.Processor;
import example.storage.InventoryService;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Which server to start? (tcp/udp)");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();


        Sender sender = new Sender();


        InventoryService inventoryService = InventoryService.getInstance();

        Processor processor = new Processor(sender, inventoryService);

        Receiver receiver;
        if ("tcp".equalsIgnoreCase(choice)) {
            receiver = new TcpReceiver(7755);
        } else if ("udp".equalsIgnoreCase(choice)) {
            receiver = new UdpReceiver(8855);
        } else {
            System.err.println("Invalid choice.");
            scanner.close();
            return;
        }


        processor.start();
        new Thread(receiver).start();

        scanner.close();
        System.out.printf("System started with %s receiver. Press Ctrl+C to stop.\n", choice.toUpperCase());


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received...");
            receiver.stop();
            processor.stopProcessing();
            System.out.println("System shutdown complete.");
        }));
    }
}