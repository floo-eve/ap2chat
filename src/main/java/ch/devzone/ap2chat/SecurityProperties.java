package ch.devzone.ap2chat;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(List<DemoUser> users) {

    public record DemoUser(String username, String password) {
    }
}
