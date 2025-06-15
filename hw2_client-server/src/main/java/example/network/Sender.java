package example.network;

import example.model.Packet;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Sender {
    private final DatagramSocket udpSocket;

    public Sender() throws IOException {
        this.udpSocket = new DatagramSocket();
    }

    public void send(Packet responsePacket) throws IOException {

        if (responsePacket.getAddress() != null) {
            DatagramPacket udpDatagram = new DatagramPacket(
                    responsePacket.getData(),
                    responsePacket.getData().length,
                    responsePacket.getAddress(),
                    responsePacket.getPort()
            );
            udpSocket.send(udpDatagram);
        }

        else if (responsePacket.getTcpResponseStream() instanceof ObjectOutputStream) {
            ObjectOutputStream out = (ObjectOutputStream) responsePacket.getTcpResponseStream();
            out.write(responsePacket.getData());
            out.flush();
        }
    }
}