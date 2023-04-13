package ru.practicum.shareit.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserControllerGateway {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @NotNull UserDtoGateway userDtoGateway) {
        return userClient.add(userDtoGateway);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long userId, @RequestBody UserDtoGateway userDtoGateway) {
        return userClient.update(userId, userDtoGateway);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getId(@PathVariable("userId") Long userId) {
        return userClient.getId(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") Long userId) {
        userClient.delete(userId);
    }
}
