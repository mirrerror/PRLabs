package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HeartbeatService {

    private final Node node;

    @Autowired
    public HeartbeatService(Node node) {
        this.node = node;
    }

    @Scheduled(fixedRate = 1000)
    public void sendHeartbeats() {
        if (node.getState() == NodeState.LEADER) {
            for (Integer peerId : node.getPeers()) {
                sendHeartbeat(peerId);
            }
        }
    }

    private void sendHeartbeat(int peerId) {
        String message = "Heartbeat:" + node.getCurrentTerm() + ":" + node.getId();
        CommunicationService.sendMessage(message, peerId);
    }

    public void handleHeartbeat(int term, int leaderId) {
        if (term >= node.getCurrentTerm()) {
            node.setState(NodeState.FOLLOWER);
            node.setLastHeartbeat(Instant.now().toEpochMilli());
        }
    }
}

