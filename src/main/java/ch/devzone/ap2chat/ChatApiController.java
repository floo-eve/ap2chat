package ch.devzone.ap2chat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatApiController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final UserFactRepository factRepository;
    private final String basePrompt;

    public ChatApiController(
            ChatClient chatClient,
            ChatMemory chatMemory,
            UserFactRepository factRepository,
            @Value("classpath:/prompts/system-prompt.st") Resource systemPromptResource) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.factRepository = factRepository;
        this.basePrompt = readResource(systemPromptResource);
    }

    private static String readResource(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public record ChatRequest(String message) {
    }

    public record ChatResponse(String reply) {
    }

    @PostMapping("/api/chat")
    public ChatResponse chat(@RequestBody ChatRequest request, Principal principal) {
        String reply = chatClient.prompt()
                .system(buildSystemPrompt(principal.getName()))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, principal.getName()))
                .user(request.message())
                .call()
                .content();
        return new ChatResponse(reply);
    }

    @DeleteMapping("/api/chat")
    public void clear(Principal principal) {
        chatMemory.clear(principal.getName());
    }

    private String buildSystemPrompt(String username) {
        List<UserFactRepository.Fact> facts = factRepository.findByUsername(username);
        if (facts.isEmpty()) {
            return basePrompt;
        }
        StringBuilder prompt = new StringBuilder(basePrompt);
        prompt.append("\n\nKnown facts about this user:\n");
        for (UserFactRepository.Fact fact : facts) {
            prompt.append("- ").append(fact.fact()).append('\n');
        }
        return prompt.toString();
    }
}
