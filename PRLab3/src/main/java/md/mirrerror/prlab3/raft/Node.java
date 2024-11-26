package md.mirrerror.prlab3.raft;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Component
public class Node {

    private static final Logger LOGGER = Logger.getLogger(Node.class.getName());
    private static final int BASE_TIMEOUT = 1000;
    private static final int TIMEOUT_VARIANCE = 1500;
    private static final int HEARTBEAT_INTERVAL = 3000;
    public static final int BUFFER_SIZE = 2048;

    private static int clusterSize;

    private final int nodeId;
    private final String nodeAddress;
    private final List<Integer> peerNodes;
    private final List<Integer> activeNodes;
    private final Map<Integer, String> nodeAddresses;
    private NodeState currentState;
    private int term;
    private int votes;
    private int heartbeatsSent;

    private final DatagramSocket udpSocket;
    private final AtomicBoolean isLeaderAlive;

    private final ScheduledExecutorService mainExecutor;
    private final ScheduledExecutorService heartbeatTimeoutExecutor;
    private final ScheduledExecutorService electionExecutor;
    private ScheduledExecutorService heartbeatExecutor;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTimeoutTask;
    private final Object lock;

    public Node() throws SocketException {
        this.nodeId = getEnvVariable("NODE_ID", 1);
        this.nodeAddress = getEnvVariable("NODE_ADDRESS", "localhost");
        this.peerNodes = new ArrayList<>();
        this.nodeAddresses = new HashMap<>();
        getPeerNodes("NODE_PEERS");

        this.activeNodes = new ArrayList<>();

        clusterSize = peerNodes.size() + 1;

        this.currentState = NodeState.FOLLOWER;
        this.term = 0;
        this.votes = 0;
        this.heartbeatsSent = 0;

        this.udpSocket = new DatagramSocket(getUdpPort());
        this.isLeaderAlive = new AtomicBoolean(false);

        this.mainExecutor = Executors.newScheduledThreadPool(1);
        this.heartbeatTimeoutExecutor = Executors.newSingleThreadScheduledExecutor();
        this.electionExecutor = Executors.newScheduledThreadPool(1);
        this.heartbeatExecutor = Executors.newScheduledThreadPool(1);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.heartbeatTimeoutTask = null;
        this.lock = new Object();

        LOGGER.log(Level.INFO, "Node initialized with ID: {0}, Peers: {1}, State: {2}, SpringApp Port: {3}, UDP Port: {4}, Address: {5}", new Object[]{nodeId, peerNodes, currentState, getPort(), getUdpPort(), nodeAddress});

        initializeNode();
    }

    private void initializeNode() {
        try {
            startUdpListener();
            Thread.sleep(1000);
            findActiveNodes();
            Thread.sleep(1000);
            LOGGER.log(Level.INFO, "Node {0} on port {1} discovered peers: {2}", new Object[]{nodeId, getPort(), activeNodes});
            initiateElection();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getPort() {
        return 8080 + nodeId - 1;
    }

    public int getUdpPort() {
        return 9000 + nodeId;
    }

    private int getEnvVariable(String variableName, int defaultValue) {
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

    private String getEnvVariable(String variableName, String defaultValue) {
        String value = System.getenv(variableName);
        if (value == null) {
            LOGGER.log(Level.WARNING, "{0} is not set. Using default value: {1}", new Object[]{variableName, defaultValue});
            return defaultValue;
        }
        return value;
    }

    private void getPeerNodes(String variableName) {
        String value = System.getenv(variableName);
        if (value == null || value.isBlank()) {
            LOGGER.log(Level.WARNING, "{0} is not set.", variableName);
        }

        Arrays.stream(value.split(",")).forEach(address -> {
            String[] addressParts = address.split(":");
            String host = addressParts[0];
            int port = -1;

            try {
                port = Integer.parseInt(addressParts[1]);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "The port {0} is invalid. Skipping it...", new Object[]{addressParts[1]});
            }

            if (port >= 0) {
                nodeAddresses.put(port, host);
                peerNodes.add(port);
            }
        });
    }

    private void startUdpListener() {
        new Thread(() -> {
            LOGGER.log(Level.INFO, "Node {0} is listening on port {1}", new Object[]{nodeId, getUdpPort()});
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    processMessage(message, packet.getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initiateElection() {
        synchronized (lock) {
            if (currentState == NodeState.LEADER) {
                LOGGER.log(Level.INFO, "Node {0}: Already a leader, skipping election.", nodeId);
                return;
            }
            if (isLeaderAlive.get()) {
                LOGGER.log(Level.INFO, "Node {0}: Election canceled as leader is active.", nodeId);
                return;
            }

            currentState = NodeState.CANDIDATE;
            term++;
            votes = 0;
            isLeaderAlive.set(false);

            LOGGER.log(Level.INFO, "Node {0} starting election for term {1}...", new Object[]{nodeId, term});

            int randomPort = peerNodes.get(new Random().nextInt(peerNodes.size()));
            sendUdpMessage("VOTE for " + (randomPort - 8999) + " term " + term + " from " + nodeId + " with port " + getUdpPort(), randomPort);
            restartHeartbeatTimeout();
            electionExecutor.schedule(() -> {
                if (votes > clusterSize / 2) {
                    becomeLeader();
                    LOGGER.log(Level.INFO, "Node {0} received {1} votes during term {2}", new Object[]{nodeId, votes, term});
                } else {
                    restartHeartbeatTimeout();
                    currentState = NodeState.FOLLOWER;
                    LOGGER.log(Level.INFO, "Node {0} received {1} votes during term {2}", new Object[]{nodeId, votes, term});
                }
            }, BASE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    private void becomeLeader() {
        currentState = NodeState.LEADER;
        isLeaderAlive.set(true);
        heartbeatTimeoutExecutor.shutdownNow();
        LOGGER.log(Level.INFO, "Node {0} is now the leader for term {1}!", new Object[]{nodeId, term});

        for (int otherPort : peerNodes) {
            sendUdpMessage("LEADER_ANNOUNCE " + nodeId + " " + term, otherPort);
        }

        sendUdpMessage("LEADER " + getUdpPort(), 8999);

        heartbeatsSent = 0;

        sendHeartbeats();
    }

    private void sendHeartbeats() {
        AtomicInteger heartbeatsSent = new AtomicInteger(0);
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            if (heartbeatsSent.get() >= 3) {
                LOGGER.log(Level.INFO, "Node {0} is no longer the leader for term {1}!", new Object[]{nodeId, term});
                isLeaderAlive.set(false);
                heartbeatExecutor.shutdown();
                heartbeatExecutor = new ScheduledThreadPoolExecutor(1);
                return;
            }

            restartHeartbeatTimeout();

            heartbeatsSent.incrementAndGet();
            for (int otherPort : peerNodes) {
                if (otherPort == getUdpPort()) continue;
                sendUdpMessage("HEARTBEAT term: " + term + " nodeId: " + nodeId, otherPort);
            }

        }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void processMessage(String message, int senderPort) {
        String[] parts = message.split(" ");
        String type = parts[0];

        LOGGER.log(Level.INFO, "Node {0} received message: {1}", new Object[]{nodeId, message});

        switch (type) {
            case "HEARTBEAT":
                int leaderTerm = Integer.parseInt(parts[2]);
                int leaderId = Integer.parseInt(parts[4]);
                if (leaderTerm >= term) {
                    isLeaderAlive.set(true);
                    currentState = NodeState.FOLLOWER;
                    term = leaderTerm;
                    restartHeartbeatTimeout();
                    LOGGER.log(Level.INFO, "Node {0}: Recognized leader {1} for term {2}", new Object[]{nodeId, leaderId, leaderTerm});
                }
                break;

            case "VOTE":
                int voteTerm = Integer.parseInt(parts[4]);
                int voterId = Integer.parseInt(parts[6]);
                int voterPort = Integer.parseInt(parts[9]);

                if (voteTerm == term && currentState == NodeState.CANDIDATE) {
                    votes++;
                    if (votes >= clusterSize / 2) {
                        becomeLeader();
                    }
                    LOGGER.log(Level.INFO, "Node {0} received a vote from Node {1}, port: {2}", new Object[]{nodeId, voterId, voterPort});
                }
                break;

            case "LEADER_ANNOUNCE":
                int newLeaderId = Integer.parseInt(parts[1]);
                int newLeaderTerm = Integer.parseInt(parts[2]);
                if (newLeaderTerm >= term) {
                    isLeaderAlive.set(true);
                    currentState = NodeState.FOLLOWER;
                    term = newLeaderTerm;
                    resetElectionTimer();
                    LOGGER.log(Level.INFO, "Node {0}: Recognized new leader {1} for term {2}", new Object[]{nodeId, newLeaderId, newLeaderTerm});
                }
                break;

            case "PING":
                sendUdpMessage("PONG " + nodeId, senderPort);
//                LOGGER.log(Level.INFO, "Node {0} received PING from port {1}", new Object[]{nodeId, senderPort});
                break;

            case "PONG":
                activeNodes.add(senderPort);
//                LOGGER.log(Level.INFO, "Node {0} received PONG from port {1}", new Object[]{nodeId, senderPort});
                break;

            default:
                LOGGER.log(Level.WARNING, "Unknown message type: {0}", message);
        }
    }

    private void sendUdpMessage(String message, int targetPort) {
        mainExecutor.submit(() -> {
            try {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(nodeAddresses.get(targetPort)), targetPort);
                udpSocket.send(packet);
                LOGGER.log(Level.INFO, "Node {0} sent: {1} to port {2}", new Object[]{nodeId, message, targetPort});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void resetElectionTimer() {
        mainExecutor.schedule(() -> {
            if (!isLeaderAlive.get() && currentState != NodeState.LEADER) {
                initiateElection();
            }
        }, BASE_TIMEOUT + new Random().nextInt(TIMEOUT_VARIANCE), TimeUnit.MILLISECONDS);
    }

    public void findActiveNodes() {
        mainExecutor.submit(() -> {
            LOGGER.log(Level.INFO, "Node {0} on port {1}: Starting sending PING to all other nodes...", new Object[]{nodeId, getUdpPort()});
            for (int targetPort : peerNodes) {
                sendUdpMessage("PING " + nodeId, targetPort);
            }
        });
    }

    private void restartHeartbeatTimeout() {
        synchronized (lock) {
            if (heartbeatTimeoutTask != null && !heartbeatTimeoutTask.isDone()) {
                heartbeatTimeoutTask.cancel(false);
            }

            heartbeatTimeoutTask = scheduler.schedule(() -> {
                LOGGER.log(Level.INFO, "Node {0}: Heartbeat timeout! Starting an election...", nodeId);
                isLeaderAlive.set(false);

                initiateElection();
            }, HEARTBEAT_INTERVAL * 2, TimeUnit.MILLISECONDS);
        }
    }

}