package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class HeartbeatService {

    private static final Logger LOGGER = Logger.getLogger(HeartbeatService.class.getName());
    private static final int HEARTBEAT_INTERVAL_MS = 1000;

    private final Node node;

    @Autowired
    public HeartbeatService(Node node) {
        this.node = node;
    }

    @Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
    public void sendHeartbeats() {
        if (node.getState() == NodeState.LEADER) {
            for (Integer peerId : node.getPeers()) {
                sendHeartbeat(peerId);
            }
        }
    }

    private void sendHeartbeat(int peerId) {
        String message = "Heartbeat:" + node.getCurrentTerm() + ":" + node.getId();
        try {
            CommunicationService.sendMessage(message, peerId);
            LOGGER.log(Level.INFO, "Heartbeat sent to peer {0} for term {1}.", new Object[]{peerId, node.getCurrentTerm()});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send heartbeat to peer " + peerId, e);
        }
    }

    public synchronized void handleHeartbeat(int term, int leaderId) {
        if (term > node.getCurrentTerm()) {
            node.setCurrentTerm(term);
            node.setState(NodeState.FOLLOWER);
            LOGGER.log(Level.INFO, "Updated term to {0} and transitioned to FOLLOWER due to heartbeat from leader {1}.",
                    new Object[]{term, leaderId});
        } else if (term == node.getCurrentTerm() && node.getState() != NodeState.FOLLOWER) {
            node.setState(NodeState.FOLLOWER);
            LOGGER.log(Level.INFO, "Transitioned to FOLLOWER for term {0} due to heartbeat from leader {1}.",
                    new Object[]{term, leaderId});
        }

        node.setLastHeartbeat(Instant.now().toEpochMilli());
        LOGGER.log(Level.FINE, "Heartbeat received from leader {0} for term {1}.", new Object[]{leaderId, term});
    }
}
