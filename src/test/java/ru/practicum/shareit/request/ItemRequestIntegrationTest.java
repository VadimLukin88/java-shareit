package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ItemRequestIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
}
