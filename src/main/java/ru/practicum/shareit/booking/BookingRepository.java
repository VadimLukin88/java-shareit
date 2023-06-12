package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerOrderByStartDesc(User booker);

    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime after, LocalDateTime before);

    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    List<Booking> findByItem_OwnerOrderByStartDesc(User owner);

    List<Booking> findByItem_OwnerAndStartAfterAndEndBeforeOrderByStartDesc(User owner, LocalDateTime after, LocalDateTime before);

    List<Booking> findByItem_OwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findByItem_OwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findByItem_OwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

    Booking findFirstByItem_OwnerAndStartBeforeOrderByStartDesc(User owner, LocalDateTime now);

    Booking findFirstByItem_OwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime now);
}
