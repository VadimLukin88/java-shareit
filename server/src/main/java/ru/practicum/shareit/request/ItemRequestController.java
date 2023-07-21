package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    // добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна.
    @PostMapping
    public ItemReqRespDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("HTTP_POST: Получен запрос на создание запроса на предмет от пользователя с Id = {} ", requestorId);
        return itemRequestService.createItemRequest(requestorId, itemRequestDto);
    }

    // получить список своих запросов вместе с данными об ответах на них.
    @GetMapping
    public List<ItemReqRespDto> getOwnItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("HTTP_POST: Получен запрос на собственные реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestService.getOwnItemRequest(requestorId);
    }

    // получить список запросов, созданных другими пользователями.
    @GetMapping("/all")
    public List<ItemReqRespDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                            @RequestParam(defaultValue = "0", required = false) int from,
                                            @RequestParam(defaultValue = "5", required = false) int size) {
        log.info("HTTP_POST: Получен запрос на все реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestService.getAllItemRequest(from, size, requestorId);
    }

    // получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public ItemReqRespDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                             @PathVariable Long requestId) {
        log.info("HTTP_POST: Получен запрос на реквесты с Id = {} от пользователя с Id = {} ", requestId, requestorId);
        return itemRequestService.getItemRequestById(requestId, requestorId);
    }

}