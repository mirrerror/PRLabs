package md.mirrerror.prlab3.config;

import jakarta.annotation.PostConstruct;
import md.mirrerror.prlab3.raft.Node;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class NodeStarterConfig {

    @PostConstruct
    public void startNode() {
        int nodeId = Integer.parseInt(System.getenv("NODE_ID"));
        int udpPort = 9000 + nodeId;

        String[] addresses = System.getenv("NODE_PEERS").split(",");
        String[] peerHostnames = Arrays.stream(addresses).map(address -> address.split(":")[0]).toArray(String[]::new);
        int[] peerPorts = Arrays.stream(addresses).mapToInt(address -> Integer.parseInt(address.split(":")[1])).toArray();

        Map<Integer, String> addressMap = new HashMap<>();
        for (int i = 0; i < addresses.length; i++) addressMap.put(peerPorts[i], peerHostnames[i]);
        addressMap.put(udpPort, System.getenv("NODE_ADDRESS"));

        String[] managementServerAddress = System.getenv("MANAGEMENT_SERVER_ADDRESS").split(":");
        int managementServerPort = Integer.parseInt(managementServerAddress[1]);
        addressMap.put(managementServerPort, managementServerAddress[0]);

        int springPort = Integer.parseInt(System.getenv("SPRING_PORT"));

        new Thread(() -> new Node(nodeId, udpPort, peerPorts, addressMap, managementServerPort, springPort)).start();
    }

}
