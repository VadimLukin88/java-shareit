package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;

    public Map<String, Object> toMap() {
        Map<String, Object> user = new HashMap<>();

        user.put("id", id);
        user.put("name", name);
        user.put("email", email);
        return user;
    }
}
