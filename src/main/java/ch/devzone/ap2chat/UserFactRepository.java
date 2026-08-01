package ch.devzone.ap2chat;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class UserFactRepository {

    private final JdbcClient jdbcClient;

    public UserFactRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public record Fact(long id, String fact) {
    }

    public List<Fact> findByUsername(String username) {
        return jdbcClient.sql("SELECT id, fact FROM user_facts WHERE username = :username ORDER BY id")
                .param("username", username)
                .query((rs, rowNum) -> new Fact(rs.getLong("id"), rs.getString("fact")))
                .list();
    }

    public long add(String username, String fact) {
        return jdbcClient.sql("INSERT INTO user_facts (username, fact) VALUES (:username, :fact) RETURNING id")
                .param("username", username)
                .param("fact", fact)
                .query(Long.class)
                .single();
    }

    public void delete(String username, long id) {
        jdbcClient.sql("DELETE FROM user_facts WHERE username = :username AND id = :id")
                .param("username", username)
                .param("id", id)
                .update();
    }
}
