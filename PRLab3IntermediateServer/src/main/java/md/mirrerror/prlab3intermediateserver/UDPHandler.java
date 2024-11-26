package md.mirrerror.prlab3intermediateserver;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

@Component
@Data
public class UDPHandler implements Runnable {

    private String currentLeaderHostname;
    private int currentLeaderPort;

    public UDPHandler() {
        currentLeaderHostname = null;
        currentLeaderPort = -1;
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("UDPHandler started");
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(8999);
            System.out.println("Started UDP listening on port " + 8999);
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
        String[] parts = message.split(" ");
        String type = parts[0];

        switch (type) {
            case "LEADER":
                String[] fullAddress = parts[1].split(":");
                currentLeaderHostname = fullAddress[0];
                currentLeaderPort = Integer.parseInt(fullAddress[1]);
                System.out.println("New leader: " + currentLeaderHostname + ":" + currentLeaderPort);
                break;
        }
    }

}