package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRespDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@Validated
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
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("HTTP_POST: Получен запрос на создание запроса на предмет от пользователя с Id = {} ", requestorId);
        return itemRequestService.createItemRequest(requestorId, itemRequestDto);
    }

//    получить список своих запросов вместе с данными об ответах на них.
//    Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
//    id вещи, название,
//    id владельца.
//    Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
//    Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    @GetMapping
    public List<ItemReqRespDto> getOwnItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("HTTP_POST: Получен запрос на собственные реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestService.getOwnItemRequest(requestorId);
    }

    //получить список запросов, созданных другими пользователями.
    // С помощью этого эндпоинта пользователи смогут просматривать существующие запросы,
    // на которые они могли бы ответить.
    // Запросы сортируются по дате создания: от более новых к более старым.
    // Результаты должны возвращаться постранично. Для этого нужно передать два параметра:
    // from — индекс первого элемента, начиная с 0, и
    // size — количество элементов для отображения.

    @GetMapping("/all")
    public List<ItemReqRespDto> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                            @PositiveOrZero(message = "Индекс страницы не может быть отрицательным!")
                                            @RequestParam(defaultValue = "0", required = false) int from,
                                            @Positive(message = "Размер страницы должен быть больше 0!")
                                            @RequestParam(defaultValue = "5", required = false) int size) {
        log.info("HTTP_POST: Получен запрос на все реквесты от пользователя с Id = {} ", requestorId);
        return itemRequestService.getAllItemRequest(from, size, requestorId);
    }

    //получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
    // что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.
    @GetMapping("/{requestId}")
    public ItemReqRespDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                             @PathVariable Long requestId) {
        log.info("HTTP_POST: Получен запрос на реквесты с Id = {} от пользователя с Id = {} ", requestId, requestorId);
        return itemRequestService.getItemRequestById(requestId, requestorId);
    }

}
