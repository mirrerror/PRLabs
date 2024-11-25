package md.mirrerror.prlab3.raft;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Component
@Getter
public class Node {

    private final int id;
    private final List<Integer> peers;

    @Setter
    private NodeState state;

    @Setter
    private int currentTerm;

    @Setter
    private int votedFor;

    private int electionTimeout;

    @Setter
    private long lastHeartbeat;

    public Node() {
        String nodeId = System.getenv("NODE_ID");
        if (nodeId == null) {
            throw new IllegalArgumentException("NODE_ID environment variable is not set");
        }

        String nodePeers = System.getenv("NODE_PEERS");
        if (nodePeers == null) {
            throw new IllegalArgumentException("NODE_PEERS environment variable is not set");
        }

        this.id = Integer.parseInt(nodeId);
        this.peers = !nodePeers.isBlank() ? Stream.of(nodePeers.split(","))
                .map(Integer::parseInt)
                .toList() : new ArrayList<>();
        this.state = NodeState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1;
        setRandomElectionTimeout();
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public void incrementTerm() {
        this.currentTerm++;
    }

    public void setRandomElectionTimeout() {
        this.electionTimeout = new Random().nextInt(500) + 1500; // 1500ms to 2000ms
    }

    public int getPort() {
        return 8080 + id - 1;
    }

    public int getUdpPort() {
        return 9000 + id;
    }
}