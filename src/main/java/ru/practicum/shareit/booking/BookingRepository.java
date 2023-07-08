package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(User booker, Pageable pageable);

    List<Booking> findByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime after, LocalDateTime before, Pageable pageable);

    List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    List<Booking> findByItem_Owner(User owner, Pageable pageable);

    List<Booking> findByItem_OwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime after, LocalDateTime before, Pageable pageable);

    List<Booking> findByItem_OwnerAndEndBefore(User owner, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_OwnerAndStartAfter(User owner, LocalDateTime now, Pageable pageable);

    List<Booking> findByItem_OwnerAndStatus(User owner, BookingStatus status, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusOrderByStart(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndEndBeforeAndStatusOrderByStartDesc(Long bookerId, Long itemId, LocalDateTime now, BookingStatus status);

}
