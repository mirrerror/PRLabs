package md.mirrerror.prlab3intermediateserver.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import md.mirrerror.prlab3intermediateserver.FileUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @RabbitListener(queues = "app_queue")
    public void receiveMessage(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            File file = new File("received_message.json");
            objectMapper.writeValue(file, rootNode);

            System.out.println("Received message: " + message);
            uploadFileToServer();
        } catch (IOException ignored) {
            System.err.println("Invalid JSON message: " + message);
        }
    }

    private void uploadFileToServer() {
        File file = new File("received_message.json");

        if (!file.exists()) {
            System.err.println("File does not exist: " + file.getAbsolutePath());
            return;
        }

        FileUtils.uploadFileToServer(file);
    }

}