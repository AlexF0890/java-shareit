package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idItem = 0;

    public void increaseNumber() {
        idItem++;
    }

    public Item getId(Long id) {
        return items.get(id);
    }

    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    public Item add(Item item) {
        increaseNumber();
        item.setId(idItem);
        items.put(idItem, item);
        return item;
    }

    public void delete(Long id) {
        items.remove(id);
    }

    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public boolean isExist(Long id) {
        return items.containsKey(id);
    }

    public List<Item> getItemsByUserId(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}
