package md.mirrerror.prlab3.raft;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Node {

    public enum NodeState {
        LEADER,
        CANDIDATE,
        FOLLOWER
    }

    private static final int BASE_TIMEOUT = 1000;
    private static final int TIMEOUT_VARIANCE = 1500;
    private static final int HEARTBEAT_INTERVAL = 3000;

    private static int clusterSize;

    private NodeState currentState;
    private DatagramSocket socket;
    private final AtomicBoolean leaderAlive;

    private final ScheduledExecutorService executor;
    private final ScheduledExecutorService electionExecutor;
    private ScheduledExecutorService heartbeatExecutor;
    private final ScheduledExecutorService heartbeatTimeoutExecutor;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartbeatTimeoutTask;
    private final Object timerLock;

    private final int nodeId;
    private final int udpPort;
    private final int springPort;
    private final int[] peerPorts;
    private final List<Integer> availablePeerPorts;
    private final Map<Integer, String> addressMap;
    private final int managementServerPort;

    private int currentTerm;
    private int votesGranted;

    public Node(int nodeId, int udpPort, int[] peerPorts, Map<Integer, String> addressMap, int managementServerPort, int springPort) {
        this.nodeId = nodeId;
        this.udpPort = udpPort;
        this.peerPorts = peerPorts;
        clusterSize = peerPorts.length + 1;
        availablePeerPorts = new ArrayList<>();
        this.addressMap = addressMap;
        this.managementServerPort = managementServerPort;
        this.springPort = springPort;
        this.leaderAlive = new AtomicBoolean(false);

        this.executor = Executors.newScheduledThreadPool(1);
        this.electionExecutor = Executors.newScheduledThreadPool(1);
        this.heartbeatExecutor = Executors.newScheduledThreadPool(1);
        this.heartbeatTimeoutExecutor = Executors.newSingleThreadScheduledExecutor();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.heartbeatTimeoutTask = null;
        this.timerLock = new Object();

        this.currentTerm = 0;
        this.votesGranted = 0;

        try {
            this.socket = new DatagramSocket(udpPort);

            startUdpListener();
            Thread.sleep(1000);
            findActiveNodes();
            Thread.sleep(1000);

            System.out.println("Node " + nodeId + " has been initialized with the port " + udpPort + " and " + clusterSize + " peers. Available peer ports: " + availablePeerPorts);

            if (availablePeerPorts.isEmpty()) {
                currentState = NodeState.LEADER;
                leaderAlive.set(true);
                sendHeartbeats();
            } else {
                startElection();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void startUdpListener() {
        new Thread(() -> {
            System.out.println("Node " + nodeId + " has just started listening for UDP packets on the port " + udpPort);
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    handleMessage(message, packet.getPort());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startElection() {
        synchronized (timerLock) {

            if (currentState == NodeState.LEADER) {
                System.out.println("Node " + nodeId + ": Already a leader, skipping election");
                return;
            }
            if (leaderAlive.get()) {
                System.out.println("Node " + nodeId + ": Election canceled as leader is active");
                return;
            }

            currentState = NodeState.CANDIDATE;
            currentTerm++;
            votesGranted = 0;
            leaderAlive.set(false);

            System.out.println("Node " + nodeId + " starting election for term " + currentTerm + "...");

            int randomPort = peerPorts[new Random().nextInt(peerPorts.length)];
            sendUdpMessage("VOTE for " + (randomPort - 9000) + " term " + currentTerm + " from " + nodeId + " with port " + udpPort, randomPort);
            startOrRestartHeartbeatTimeout();
            electionExecutor.schedule(() -> {
                if (votesGranted > clusterSize / 2) {
                    becomeLeader();
                } else {
                    startOrRestartHeartbeatTimeout();
                    currentState = NodeState.FOLLOWER;
                }
            }, BASE_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    private void becomeLeader() {
        currentState = NodeState.LEADER;
        leaderAlive.set(true);
        heartbeatTimeoutExecutor.shutdownNow();
        System.out.println("Node " + nodeId + " is now the leader for term " + currentTerm + "!");

        for (int otherPort : peerPorts) {
            sendUdpMessage("LEADER_ANNOUNCE " + nodeId + " " + currentTerm, otherPort);
        }

        sendUdpMessage("LEADER " + addressMap.get(udpPort) + ":" + springPort, managementServerPort);

        sendHeartbeats();
    }

    private void sendHeartbeats() {
        AtomicInteger sentHeartbeats = new AtomicInteger(0);
        heartbeatExecutor.scheduleAtFixedRate(() -> {

            if (sentHeartbeats.get() >= 3) {
                System.out.println("Node " + nodeId + " is no longer the leader for term " + currentTerm + "!");
                leaderAlive.set(false);
                heartbeatExecutor.shutdown();
                heartbeatExecutor = new ScheduledThreadPoolExecutor(1);
                return;
            }

            startOrRestartHeartbeatTimeout();

            sentHeartbeats.set(sentHeartbeats.get() + 1);
            for (int otherPort : peerPorts) {
                if (otherPort == udpPort) continue;
                sendUdpMessage("HEARTBEAT term: " + currentTerm + " nodeId: " + nodeId, otherPort);
            }

        }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void handleMessage(String message, int senderPort) {
        String[] parts = message.split(" ");
        String type = parts[0];

        if (!message.startsWith("PING") && !message.startsWith("PONG")) {
            System.out.println("Node " + nodeId + " has just received a message: " + message);
        }

        switch (type) {
            case "HEARTBEAT":
                int leaderTerm = Integer.parseInt(parts[2]);
                if (leaderTerm >= currentTerm) {
                    leaderAlive.set(true);
                    currentState = NodeState.FOLLOWER;
                    currentTerm = leaderTerm;
                    startOrRestartHeartbeatTimeout();
                }
                break;
            case "VOTE":
                int term = Integer.parseInt(parts[4]);

                if (term == currentTerm && currentState == NodeState.CANDIDATE) {
                    votesGranted++;
                    if (votesGranted >= clusterSize / 2) {
                        becomeLeader();
                    }
                }
                break;
            case "LEADER_ANNOUNCE":
                int newLeaderId = Integer.parseInt(parts[1]);
                int newLeaderTerm = Integer.parseInt(parts[2]);
                if (newLeaderTerm >= currentTerm) {
                    leaderAlive.set(true);
                    currentState = NodeState.FOLLOWER;
                    currentTerm = newLeaderTerm;
                    resetElectionTimer();
                    System.out.println("Node " + nodeId + ": Recognized new leader " + newLeaderId + " for term " + newLeaderTerm);
                }
                break;
            case "PING":
                sendUdpMessage("PONG " + nodeId, senderPort);
                break;
            case "PONG":
                availablePeerPorts.add(senderPort);
                break;
            default:
                System.out.println("Unknown message type: " + message);
                break;
        }
    }

    private void sendUdpMessage(String message, int targetPort) {
        executor.submit(() -> {
            try {
                byte[] buffer = message.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(addressMap.get(targetPort)), targetPort);
                socket.send(packet);
                if (message.startsWith("PING") || message.startsWith("PONG") || message.startsWith("VOTE") || message.startsWith("LEADER_ANNOUNCE") || message.startsWith("HEARTBEAT"))
                    return;

                System.out.println("Node " + nodeId + " has just sent the message: \"" + message + "\" to the port: " + targetPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void resetElectionTimer() {
        executor.schedule(() -> {
            if (!leaderAlive.get() && currentState != NodeState.LEADER) {
                startElection();
            }
        }, BASE_TIMEOUT + new Random().nextInt(TIMEOUT_VARIANCE), TimeUnit.MILLISECONDS);
    }

    public void findActiveNodes() {
        executor.submit(() -> {
            System.out.println("Node " + nodeId + " on the port " + udpPort + ": Pinging all the other nodes...");
            for (int targetPort : peerPorts) {
                sendUdpMessage("PING " + nodeId, targetPort);
            }
        });
    }

    private void startOrRestartHeartbeatTimeout() {
        synchronized (timerLock) {
            if (heartbeatTimeoutTask != null && !heartbeatTimeoutTask.isDone()) {
                heartbeatTimeoutTask.cancel(false);
            }

            heartbeatTimeoutTask = scheduler.schedule(() -> {
                System.out.println("Node " + nodeId + ": Heartbeat timeout! Starting election...");
                leaderAlive.set(false);

                startElection();
            }, HEARTBEAT_INTERVAL * 2, TimeUnit.MILLISECONDS);
        }
    }

}
