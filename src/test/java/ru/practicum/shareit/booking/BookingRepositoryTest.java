package ru.practicum.shareit.booking;

import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    private User user;
    private Booking booking;
    private List<Booking> bookingList;

//    @BeforeEach
//    public void setUp() {
//        user = null;
//        booking = null;
//        bookingList = null;
//    }
//
//    @Test
//    public void testFindByBooker() {
//        user = userRepository.getReferenceById(1L);
//        Pageable pageable = PageRequest.of(1, 10);
//
//        bookingList = bookingRepository.findByBooker(user, pageable);
//
//        assertEquals(bookingList.size(), 5, "Некорректный размер списка");
//
//    }
}