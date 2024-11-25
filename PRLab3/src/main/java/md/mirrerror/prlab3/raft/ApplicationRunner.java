package md.mirrerror.prlab3.raft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private final Node node;
    private final ElectionService electionService;
    private final HeartbeatService heartbeatService;

    @Autowired
    public ApplicationRunner(Node node, ElectionService electionService, HeartbeatService heartbeatService) {
        this.node = node;
        this.electionService = electionService;
        this.heartbeatService = heartbeatService;
//        run();
    }

    @Override
    public void run(String... args) {
        new Thread(() -> new UDPHandler(node, electionService, heartbeatService)).start();
    }

}