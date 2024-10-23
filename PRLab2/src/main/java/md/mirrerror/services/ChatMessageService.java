package md.mirrerror.services;

import lombok.RequiredArgsConstructor;
import md.mirrerror.models.ChatMessage;
import md.mirrerror.repositories.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

}
