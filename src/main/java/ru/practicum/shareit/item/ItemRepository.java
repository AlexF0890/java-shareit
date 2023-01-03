package ru.practicum.shareit.item;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Getter
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idItem = 0;

    public void increaseNumber() {
        idItem++;
    }

    public Item getItemId(Long id) {
        return items.get(id);
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    public Item addItem(Item item) {
        increaseNumber();
        item.setId(idItem);
        items.put(idItem, item);
        return item;
    }

    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }
}
