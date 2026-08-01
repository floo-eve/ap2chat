package ch.devzone.ap2chat;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facts")
public class FactApiController {

    private final UserFactRepository repository;

    public FactApiController(UserFactRepository repository) {
        this.repository = repository;
    }

    public record FactRequest(String fact) {
    }

    public record FactResponse(long id, String fact) {
    }

    @GetMapping
    public List<FactResponse> list(Principal principal) {
        return repository.findByUsername(principal.getName()).stream()
                .map(f -> new FactResponse(f.id(), f.fact()))
                .toList();
    }

    @PostMapping
    public FactResponse add(@RequestBody FactRequest request, Principal principal) {
        long id = repository.add(principal.getName(), request.fact());
        return new FactResponse(id, request.fact());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, Principal principal) {
        repository.delete(principal.getName(), id);
    }
}
