package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = ?1 order by b.id desc")
    List<Booking> findByBookerId(Long bookerId);

    @Query("select b from Booking b")
    Page<Booking> findByBookerId(Pageable page);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp order by b.id desc")
    List<Booking> findByBookerIdAndEndDateIsBefore(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.end < current_timestamp")
    Page<Booking> findByBookerIdAndEndDateIsBefore(Long bookerId, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.id desc")
    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Page<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp order by b.id desc")
    List<Booking> findByBookerIdAndStartDateIsAfter(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and b.start > current_timestamp")
    Page<Booking> findByBookerIdAndStartDateIsAfter(Long bookerId, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 order by b.id desc")
    List<Booking> findByItemOwnerIdAndStatusEquals(Long ownerId, STATUS status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.status = ?2")
    Page<Booking> findByItemOwnerIdAndStatusEquals(Long ownerId, STATUS status, Pageable page);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2 order by b.id desc")
    List<Booking> findByBookerIdAndStatusEquals(Long bookerId, STATUS status);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2")
    Page<Booking> findByBookerIdAndStatusEquals(Long bookerId, STATUS status, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp order by b.id desc")
    List<Booking> findByItemOwnerIdAndStartDateIsAfter(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.start > current_timestamp")
    Page<Booking> findByItemOwnerIdAndStartDateIsAfter(Long ownerId, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp order by b.id desc")
    List<Booking> findByItemOwnerIdAndEndDateIsBefore(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and b.end < current_timestamp")
    Page<Booking> findByItemOwnerIdAndEndDateIsBefore(Long ownerId, Pageable page);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp " +
            "between b.start and b.end order by b.id desc")
    List<Booking> findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId);

    @Query("select b from Booking b where b.booker.id = ?1 and current_timestamp " +
            "between b.start and b.end")
    Page<Booking> findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(Long bookerId, Pageable page);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp " +
            "between b.start and b.end order by b.id desc")
    List<Booking> findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId);

    @Query("select b from Booking b where b.item.owner.id = ?1 and current_timestamp " +
            "between b.start and b.end")
    Page<Booking> findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId, Pageable page);

    Optional<Booking> findFirstByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime localDateTime);

    Optional<Booking> findFirstByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    @Query("select b from Booking b where b.booker.id = ?1 and b.item.id = ?2 " +
            "and b.end < current_timestamp order by b.id")
    Optional<Booking> findByItemId(Long userId, Long itemId);
}
