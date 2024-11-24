package md.mirrerror.prlab3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FileManagerController {

    @GetMapping("/file-manager")
    public String fileManager() {
        return "fileManager/fileManager";
    }

}