package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);//IdOrderByCreatedDesc

    Page<ItemRequest> findAllByRequesterIdIsNot(Long requesterId, Pageable pageable);
}
