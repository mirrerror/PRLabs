package md.mirrerror.prlab3.raft;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private final Node node;
    private final ElectionService electionService;
    private final HeartbeatService heartbeatService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public ApplicationRunner(Node node, ElectionService electionService, HeartbeatService heartbeatService) {
        this.node = node;
        this.electionService = electionService;
        this.heartbeatService = heartbeatService;
    }

    @Override
    public void run(String... args) {
        executorService.submit(() -> {
            try {
                UDPHandler udpHandler = new UDPHandler(node, electionService, heartbeatService);
                udpHandler.run();
            } catch (Exception e) {
                System.err.println("Error in UDPHandler: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdownNow();
        System.out.println("ApplicationRunner cleanup complete.");
    }
}
