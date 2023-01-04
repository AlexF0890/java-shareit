package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserEmailNotNull;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idUser = 0;

    private void increaseNumber() {
        idUser++;
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User getId(Long id) {
        return users.get(id);
    }

    public User add(User user) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            increaseNumber();
            user.setId(idUser);
            users.put(idUser, user);
            return user;
        } else {
            throw new UserEmailNotNull("Почта не должна быть пустой или должна содержать @");
        }
    }

    public void delete(Long id) {
        users.remove(id);
    }

    public User update(User user) {
        if (user.getEmail() != null && user.getEmail().contains("@")) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserEmailNotNull("Почта не должна быть пустой или должна содержать @");
        }
    }

    public boolean isExist(Long id) {
        return users.containsKey(id);
    }

    public boolean checkEmail(String email) {
        return users.values()
                .stream()
                .map(User::getEmail)
                .noneMatch(usersEmail -> usersEmail.equals(email));
    }
}
