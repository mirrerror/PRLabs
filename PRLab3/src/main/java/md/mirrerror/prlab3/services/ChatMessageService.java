package md.mirrerror.prlab3.services;

import lombok.RequiredArgsConstructor;
import md.mirrerror.prlab3.models.ChatMessage;
import md.mirrerror.prlab3.repositories.ChatMessageRepository;
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
