package md.mirrerror.prlab3.raft;

import java.io.IOException;
import java.net.*;

public class CommunicationService {

    public static void sendMessage(String message, int peerId) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 9000 + peerId);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String receiveMessage(int peerId) {
        try (DatagramSocket socket = new DatagramSocket(9000 + peerId)) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            return new String(packet.getData(), 0, packet.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

