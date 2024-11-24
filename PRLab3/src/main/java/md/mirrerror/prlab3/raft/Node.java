package md.mirrerror.prlab3.raft;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Getter
public class Node {

    private int id;

    @Setter
    private NodeState state;

    private int currentTerm;

    private int votedFor;

    private List<Integer> peers;

    private int electionTimeout;

    @Setter
    private long lastHeartbeat;

    public Node() {
        this.state = NodeState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1;
        setRandomElectionTimeout();
        this.lastHeartbeat = System.currentTimeMillis();
    }

    @Value("${node.id}")
    public void setId(int id) {
        this.id = id;
    }

    @Value("${node.peers}")
    public void setPeers(List<Integer> peers) {
        this.peers = peers;
    }

    public void incrementTerm() {
        this.currentTerm++;
    }

    public void setRandomElectionTimeout() {
        this.electionTimeout = new Random().nextInt(500) + 1500; // 1500ms to 2000ms
    }

}

