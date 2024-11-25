package md.mirrerror.prlab3.raft;

import lombok.Getter;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommunicationService {

    private static final Logger LOGGER = Logger.getLogger(CommunicationService.class.getName());
    private static final int BASE_PORT = 9000;
    private static final int MAX_PACKET_SIZE = 4096;

    @Getter
    private static final Map<Integer, CountDownLatch> responseLatches = new HashMap<>();

    public static void sendMessage(String message, int peerId) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            int port = BASE_PORT + peerId;

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);

            LOGGER.log(Level.INFO, "Message sent to peer {0} on port {1}: {2}", new Object[]{peerId, port, message});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send message to peer " + peerId, e);
        }
    }

    public static String receiveMessage(int peerId) {
        try (DatagramSocket socket = new DatagramSocket(BASE_PORT + peerId)) {
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            LOGGER.log(Level.INFO, "Listening for messages on port {0}", BASE_PORT + peerId);
            socket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());
            LOGGER.log(Level.INFO, "Message received from {0}: {1}", new Object[]{packet.getAddress(), message});

            return message;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to receive message on port " + (BASE_PORT + peerId), e);
        }
        return null;
    }

    public static boolean waitForResponse(int peerId) {
        CountDownLatch latch = responseLatches.computeIfAbsent(peerId, k -> new CountDownLatch(1));

        try {
            return latch.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return false;
    }


}
