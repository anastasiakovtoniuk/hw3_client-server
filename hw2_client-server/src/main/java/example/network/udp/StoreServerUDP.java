package example.network.udp;

import example.crypto.Decryptor;
import example.crypto.Encryptor;
import example.crypto.SimpleDecryptor;
import example.crypto.SimpleEncryptor;
import example.model.Message;
import example.processor.Processor;

import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class StoreServerUDP {
    private static final int PORT = 8855;
    private static final int BUFFER_SIZE = 4096;
    private final Set<String> processedRequestIds = Collections.synchronizedSet(new HashSet<>());

    public void start() throws IOException {
        Processor processor = new Processor();
        Encryptor encryptor = new SimpleEncryptor();
        Decryptor decryptor = new SimpleDecryptor();

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port " + PORT);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                try {

                    byte[] encryptedData = new byte[requestPacket.getLength()];
                    System.arraycopy(requestPacket.getData(), 0, encryptedData, 0, requestPacket.getLength());
                    byte[] decryptedData = decryptor.decrypt(encryptedData);


                    ByteArrayInputStream bais = new ByteArrayInputStream(decryptedData);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Message request = (Message) ois.readObject();

                    System.out.println("Received from [" + requestPacket.getAddress() + "]: " + request.getCommand());

                    if (processedRequestIds.contains(request.getMessageId())) {
                        System.out.println("Duplicate request detected: " + request.getMessageId() + ". Ignoring.");
                        continue;
                    }

                    Message response = processor.process(request);
                    processedRequestIds.add(request.getMessageId());


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(response);
                    byte[] responseData = encryptor.encrypt(baos.toByteArray());

                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getAddress(), requestPacket.getPort());
                    socket.send(responsePacket);

                } catch (Exception e) {
                    System.err.println("Error processing packet: " + e.getMessage());
                }
            }
        }
    }
}