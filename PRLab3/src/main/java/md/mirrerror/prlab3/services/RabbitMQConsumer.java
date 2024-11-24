package md.mirrerror.prlab3.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        try {
            File file = new File("received_message.json");
            if (!file.exists()) {
                System.err.println("File does not exist: " + file.getAbsolutePath());
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String uploadUrl = "http://localhost:8080/products/upload";

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to upload the file.");
        }
    }

}