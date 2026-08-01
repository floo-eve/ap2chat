package ch.devzone.ap2chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatApiController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatApiController(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    public record ChatRequest(String message) {
    }

    public record ChatResponse(String reply) {
    }

    @PostMapping("/api/chat")
    public ChatResponse chat(@RequestBody ChatRequest request, @RequestHeader("X-Conversation-Id") String conversationId) {
        String reply = chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(request.message())
                .call()
                .content();
        return new ChatResponse(reply);
    }

    @DeleteMapping("/api/chat")
    public void clear(@RequestHeader("X-Conversation-Id") String conversationId) {
        chatMemory.clear(conversationId);
    }
}
