package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 order by b.id desc")
    List<Booking> findByBookerIdOrderByBookerId(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp order by b.id desc")
    List<Booking> findByBookerIdAndEndDateIsBefore(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.id desc")
    List<Booking> findByItemOwnerId(Long ownedId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp order by b.id desc")
    List<Booking> findByBookerIdAndStartDateIsAfter(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.id desc")
    List<Booking> findByItemOwnerIdAndStatusEquals(Long ownerId, STATUS status);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.id desc")
    List<Booking> findByBookerIdAndStatusEquals(Long bookerId, STATUS status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp order by b.id desc")
    List<Booking> findByItemOwnerIdAndStartDateIsAfter(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp order by b.id desc")
    List<Booking> findByItemOwnerIdAndEndDateIsBefore(Long ownerId);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp " +
            "between b.start and b.end order by b.id desc")
    List<Booking> findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp " +
            "between b.start and b.end order by b.id desc")
    List<Booking> findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 " +
            "and b.end < current_timestamp order by b.id")
    Optional<Booking> findByItemId(Long userId, Long itemId);
}
