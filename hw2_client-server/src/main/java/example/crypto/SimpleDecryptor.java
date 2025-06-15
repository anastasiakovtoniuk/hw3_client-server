package example.crypto;

import example.model.Message;

public class SimpleDecryptor implements Decryptor {
    @Override
    public Message decrypt(byte[] data) {
        String[] parts = new String(data).split(":");
        return new Message(
                parts[0],
                parts[1],
                Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]),
                parts[4]
        );
    }
}