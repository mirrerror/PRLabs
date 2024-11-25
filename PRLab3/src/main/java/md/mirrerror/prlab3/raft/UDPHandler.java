package md.mirrerror.prlab3.raft;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPHandler implements Runnable {

    private final Node node;
    private final ElectionService electionService;
    private final HeartbeatService heartbeatService;

    public UDPHandler(Node node, ElectionService electionService, HeartbeatService heartbeatService) {
        this.node = node;
        this.electionService = electionService;
        this.heartbeatService = heartbeatService;
        run();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(9000 + node.getId());
            System.out.println("Started UDP listening on port " + (9000 + node.getId()));
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                handleMessage(message);
            }
        } catch (IOException e) {
            if (socket != null) socket.close();
            e.printStackTrace();
        }
    }

    private void handleMessage(String message) {
        String[] parts = message.split(":");
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
                boolean voteGranted = Boolean.parseBoolean(parts[3]);
                electionService.receiveVoteResponse(voteGranted);
                break;
        }
    }

    private void handleVoteRequest(int term, int candidateId) {
        boolean voteGranted = false;
        if (term > node.getCurrentTerm() || (term == node.getCurrentTerm() && (node.getVotedFor() == -1 || node.getVotedFor() == candidateId))) {
            node.setCurrentTerm(term);
            node.setVotedFor(candidateId);
            voteGranted = true;
        }
        String response = "VoteResponse:" + term + ":" + node.getId() + ":" + voteGranted;
        CommunicationService.sendMessage(response, candidateId);
    }
}