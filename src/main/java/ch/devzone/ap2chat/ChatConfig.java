package ch.devzone.ap2chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ChatConfig {

    @Bean
    ChatClient chatClient(
            ChatClient.Builder builder,
            ChatMemory chatMemory,
            @Value("classpath:/prompts/system-prompt.st") Resource systemPrompt) {
        return builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
