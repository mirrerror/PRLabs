package md.mirrerror.prlab3.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/raft")
public class RaftController {

    @PostMapping("/leader")
    @ResponseBody
    public String leader(@RequestBody String leaderInfo) {
        System.out.println("New leader elected: " + leaderInfo);
        return "Leader information received";
    }

}