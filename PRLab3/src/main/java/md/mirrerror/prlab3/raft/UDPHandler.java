package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class UDPHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(UDPHandler.class.getName());
    private static final int BUFFER_SIZE = 2048;

    private final Node node;
    private final ElectionService electionService;
    private final HeartbeatService heartbeatService;

    @Autowired
    public UDPHandler(Node node, ElectionService electionService, HeartbeatService heartbeatService) {
        this.node = node;
        this.electionService = electionService;
        this.heartbeatService = heartbeatService;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(node.getUdpPort())) {
            LOGGER.log(Level.INFO, "Started UDP listening on port {0}", node.getUdpPort());
            byte[] buffer = new byte[BUFFER_SIZE];
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    handleMessage(message);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while receiving packet: {0}", e.getMessage());
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to bind UDP socket on port {0}: {1}", new Object[]{node.getUdpPort(), e.getMessage()});
        }
    }

    private void handleMessage(String message) {
        try {
            LOGGER.log(Level.FINE, "Received message: {0}", message);
            String[] parts = message.split(":");
            if (parts.length < 3) {
                LOGGER.log(Level.WARNING, "Malformed message: {0}", message);
                return;
            }

            String type = parts[0];
            int term = Integer.parseInt(parts[1]);
            int senderId = Integer.parseInt(parts[2]);

            switch (type) {
                case "Heartbeat":
                    heartbeatService.handleHeartbeat(term, senderId);
                    break;
                case "RequestVote":
                    handleVoteRequest(term, senderId);
                    break;
                case "VoteResponse":
                    if (parts.length >= 4) {
                        boolean voteGranted = Boolean.parseBoolean(parts[3]);
                        electionService.receiveVoteResponse(voteGranted);
                    } else {
                        LOGGER.log(Level.WARNING, "Malformed VoteResponse message: {0}", message);
                    }
                    break;
                case "READY":
                    CommunicationService.sendMessage("READY_ACK", senderId);
                    break;
                case "READY_ACK":
                    CommunicationService.getResponseLatches().computeIfAbsent(senderId, id -> new CountDownLatch(1)).countDown();
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Unknown message type: {0}", type);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while handling message: {0}", e.getMessage());
        }
    }

    private void handleVoteRequest(int term, int candidateId) {
        boolean voteGranted = false;
        synchronized (node) {
            if (term > node.getCurrentTerm() ||
                    (term == node.getCurrentTerm() && (node.getVotedFor() == -1 || node.getVotedFor() == candidateId))) {
                node.setCurrentTerm(term);
                node.setVotedFor(candidateId);
                voteGranted = true;
            }
        }
        String response = "VoteResponse:" + term + ":" + node.getId() + ":" + voteGranted;
        CommunicationService.sendMessage(response, candidateId);
    }
}
