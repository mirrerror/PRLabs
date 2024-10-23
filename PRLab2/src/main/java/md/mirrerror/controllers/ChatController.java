package md.mirrerror.controllers;

import lombok.RequiredArgsConstructor;
import md.mirrerror.models.ChatMessage;
import md.mirrerror.services.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/send-message")
    @SendTo("/topic/all")
    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        chatMessageService.saveMessage(message);
        return message;
    }

    @GetMapping("/chat/messages")
    @ResponseBody
    public List<ChatMessage> getMessages() {
        return chatMessageService.getAllMessages();
    }

    @GetMapping("/chat")
    public String chatRoom() {
        return "chat/chat";
    }

}