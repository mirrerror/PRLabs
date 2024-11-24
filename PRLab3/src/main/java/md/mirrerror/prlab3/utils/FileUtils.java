package md.mirrerror.prlab3.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@UtilityClass
public class FileUtils {

    public static void uploadFileToServer(File file) {
        try {
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
