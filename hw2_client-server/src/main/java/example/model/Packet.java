package example.model;

import java.net.InetAddress;


public class Packet {
    private final byte[] data;

    private InetAddress address;
    private int port;

    private Object tcpResponseStream;

    public Packet(byte[] data) {
        this.data = data;
    }

    public Packet(byte[] data, InetAddress address, int port) {
        this.data = data;
        this.address = address;
        this.port = port;
    }

    public byte[] getData() {
        return data;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Object getTcpResponseStream() {
        return tcpResponseStream;
    }

    public void setTcpResponseStream(Object tcpResponseStream) {
        this.tcpResponseStream = tcpResponseStream;
    }
}