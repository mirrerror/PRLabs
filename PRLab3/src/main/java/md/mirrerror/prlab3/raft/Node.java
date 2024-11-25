package md.mirrerror.prlab3.raft;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Getter
public class Node {

    private static final Logger LOGGER = Logger.getLogger(Node.class.getName());

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
        this.id = parseEnvironmentVariable("NODE_ID", 1);
        this.peers = parsePeersEnvironmentVariable("NODE_PEERS");
        this.state = NodeState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1;
        setRandomElectionTimeout();
        this.lastHeartbeat = System.currentTimeMillis();

        LOGGER.log(Level.INFO, "Node initialized with ID: {0}, Peers: {1}, State: {2}", new Object[]{id, peers, state});
    }

    public void incrementTerm() {
        this.currentTerm++;
    }

    public void setRandomElectionTimeout() {
        this.electionTimeout = new Random().nextInt(500) + 1500;
        LOGGER.log(Level.FINE, "Random election timeout set to {0}ms", this.electionTimeout);
    }

    public int getPort() {
        return 8080 + id - 1;
    }

    public int getUdpPort() {
        return 9000 + id;
    }

    private int parseEnvironmentVariable(String variableName, int defaultValue) {
        String value = System.getenv(variableName);
        if (value == null) {
            LOGGER.log(Level.WARNING, "{0} is not set. Using default value: {1}", new Object[]{variableName, defaultValue});
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "{0} is invalid. Using default value: {1}", new Object[]{variableName, defaultValue});
            return defaultValue;
        }
    }

    private List<Integer> parsePeersEnvironmentVariable(String variableName) {
        String value = System.getenv(variableName);
        if (value == null || value.isBlank()) {
            LOGGER.log(Level.WARNING, "{0} is not set.  Using empty peer list.", new Object[]{variableName});
            return new ArrayList<>();
        }
        try {
            return Stream.of(value.split(",")).map(Integer::parseInt).toList();
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "{0} is invalid. Using empty peer list.", variableName);
            return new ArrayList<>();
        }
    }

    public void waitForClusterReadiness() {
        Set<Integer> readyPeers = new HashSet<>();
        for (int peer : peers) {
            boolean acknowledged = false;
            while (!acknowledged) {
                try {
                    CommunicationService.sendMessage("READY:" + id, peer);
                    acknowledged = CommunicationService.waitForResponse(peer);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
            readyPeers.add(peer);
        }
        System.out.println("All peers are ready: " + readyPeers);
    }

}
