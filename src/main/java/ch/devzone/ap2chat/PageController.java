package ch.devzone.ap2chat;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String chatPage(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "chat";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
