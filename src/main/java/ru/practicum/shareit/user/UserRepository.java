package ru.practicum.shareit.user;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserEmailNotNull;

import java.util.*;

@Repository
@Getter
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private  long idUser = 0;

    private void increaseNumber() {
        idUser++;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserId(Long id) {
        return users.get(id);
    }

    public User addUser(User user) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            increaseNumber();
            user.setId(idUser);
            users.put(idUser, user);
            return user;
        } else {
            throw new UserEmailNotNull("Почта не должна быть пустой или должна содержать @");
        }
    }

    public void deleteUser(Long id) {
        users.remove(id);
    }

    public User updateUser(User user) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserEmailNotNull("Почта не должна быть пустой или должна содержать @");
        }
    }
}
