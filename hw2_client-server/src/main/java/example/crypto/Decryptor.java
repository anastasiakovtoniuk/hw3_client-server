package example.crypto;

import example.model.Message;

public interface Decryptor {
    Message decrypt(byte[] data);
}