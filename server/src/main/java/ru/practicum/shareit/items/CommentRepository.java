package ru.practicum.shareit.items;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemIdIn(List<Long> itemIds);

    List<Comment> findCommentsByItemId(Long itemId);
}
