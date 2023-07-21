package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@Validated
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }


    // добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("HTTP_POST: Получен запрос на создание запроса на предмет от пользователя с Id = {} ", requestorId);
        return itemRequestClient.createItemRequest(requestorId, itemRequestDto);
    }

    //    получить список своих запросов вместе с данными об ответах на них.
    @GetMapping
    public ResponseEntity<Object> getOwnItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("HTTP_POST: Получен запрос на собственные реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestClient.getOwnItemRequest(requestorId);
    }

    //получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                            @PositiveOrZero(message = "Индекс страницы не может быть отрицательным!")
                                            @RequestParam(defaultValue = "0") int from,
                                            @Positive(message = "Размер страницы должен быть больше 0!")
                                            @RequestParam(defaultValue = "5") int size) {
        log.info("HTTP_POST: Получен запрос на все реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestClient.getAllItemRequest(from, size, requestorId);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте GET /requests.
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                             @PathVariable Long requestId) {
        log.info("HTTP_POST: Получен запрос на реквесты с Id = {} от пользователя с Id = {} ", requestId, requestorId);
        return itemRequestClient.getItemRequestById(requestId, requestorId);
    }

}
