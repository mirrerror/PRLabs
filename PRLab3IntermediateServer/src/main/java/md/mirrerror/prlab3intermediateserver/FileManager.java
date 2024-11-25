package md.mirrerror.prlab3intermediateserver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Component
@RequiredArgsConstructor
public class FileManager {

    private final UDPHandler udpHandler;

    public void uploadFileToServer(File file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            int currentLeader = udpHandler.getCurrentLeader();

            if (currentLeader >= 0) {
                String uploadUrl = "http://localhost:" + currentLeader + "/products/upload";

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.postForEntity(uploadUrl, requestEntity, String.class);
            } else {
                System.out.println("No leader available to upload the file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to upload the file.");
        }
    }

}
