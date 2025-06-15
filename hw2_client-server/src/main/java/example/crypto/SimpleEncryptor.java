package example.crypto;

public class SimpleEncryptor implements Encryptor {
    @Override
    public byte[] encrypt(String response) {
        return response.getBytes();
    }
}