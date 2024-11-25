package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ElectionService {

    private static final Logger LOGGER = Logger.getLogger(ElectionService.class.getName());
    private static final int MANAGER_SERVER_PORT = 8999;

    private final Node node;
    private final AtomicInteger votesReceived = new AtomicInteger(0);

    @Autowired
    public ElectionService(Node node) {
        this.node = node;
    }

    @Scheduled(fixedRate = 100)
    public void checkElectionTimeout() {
        long timeSinceLastHeartbeat = Instant.now().toEpochMilli() - node.getLastHeartbeat();
        if (node.getState() == NodeState.FOLLOWER && timeSinceLastHeartbeat > node.getElectionTimeout()) {
            startElection();
        }
    }

    public synchronized void startElection() {
        node.setState(NodeState.CANDIDATE);
        node.incrementTerm();
        node.setLastHeartbeat(Instant.now().toEpochMilli());
        votesReceived.set(1); // Vote for itself
        LOGGER.log(Level.INFO, "Node {0} started an election for term {1}", new Object[]{node.getId(), node.getCurrentTerm()});
        requestVotes();
    }

    private void requestVotes() {
        for (Integer peerId : node.getPeers()) {
            sendVoteRequest(peerId);
        }
    }

    private void sendVoteRequest(int peerId) {
        String message = "RequestVote:" + node.getCurrentTerm() + ":" + node.getId();
        CommunicationService.sendMessage(message, peerId);
        LOGGER.log(Level.INFO, "Vote request sent to peer {0}", peerId);
    }

    public synchronized void receiveVoteResponse(boolean voteGranted) {
        if (node.getState() == NodeState.CANDIDATE && voteGranted) {
            int totalVotes = votesReceived.incrementAndGet();
            if (totalVotes > node.getPeers().size() / 2) {
                becomeLeader();
            }
        }
    }

    private synchronized void becomeLeader() {
        node.setState(NodeState.LEADER);
        notifyManagerServer();
        LOGGER.log(Level.INFO, "Node {0} became the leader for term {1}", new Object[]{node.getId(), node.getCurrentTerm()});
    }

    private void notifyManagerServer() {
        String message = "Leader:" + node.getPort();
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, MANAGER_SERVER_PORT);
            socket.send(packet);
            LOGGER.log(Level.INFO, "Leader notification sent to manager server.");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to notify manager server.", e);
        }
    }
}
