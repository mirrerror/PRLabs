package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ElectionService {

    private final Node node;

    private int votesReceived;

    @Autowired
    public ElectionService(Node node) {
        this.node = node;
        this.votesReceived = 0;
    }

    @Scheduled(fixedRate = 100)
    public void checkElectionTimeout() {
        if (node.getState() == NodeState.FOLLOWER &&
                (Instant.now().toEpochMilli() - node.getLastHeartbeat()) > node.getElectionTimeout()) {
            startElection();
        }
    }

    public void startElection() {
        node.setState(NodeState.CANDIDATE);
        node.incrementTerm();
        node.setLastHeartbeat(Instant.now().toEpochMilli());
        votesReceived = 1; // Vote for itself
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
    }

    public void receiveVoteResponse(boolean voteGranted) {
        if (node.getState() == NodeState.CANDIDATE && voteGranted) {
            votesReceived++;
            if (votesReceived > node.getPeers().size() / 2) {
                becomeLeader();
            }
        }
    }

    private void becomeLeader() {
        node.setState(NodeState.LEADER);
        System.out.println("Node " + node.getId() + " became the leader!");
    }
}

