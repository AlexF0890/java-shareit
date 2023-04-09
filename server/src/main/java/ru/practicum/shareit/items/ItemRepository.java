package ru.practicum.shareit.items;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("select i from Item i where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    List<Item> findByIsAvailableIsTrue(String search);

    List<Item> findAllByRequestIdIn(List<Long> requestId);

    List<Item> findAllByRequestId(Long requestId);

    @Query("select i from Item i where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) and i.available = true")
    Page<Item> findPageByIsAvailableIsTrue(String search, Pageable pageable);

    Page<Item> findPageByOwnerId(Long ownerId, Pageable page);
}
