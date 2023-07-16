package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.OnCreate;

import javax.validation.Valid;

@Slf4j
@Controller
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("HTTP_POST: Получен запрос на создание предмета " + itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> modifyItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("HTTP_PATCH: Получен запрос на изменение предмета Id = " + itemId + " от пользователя Id = " + userId
                  + ". Обновляемые данные: " + itemDto);
        return itemClient.modifyItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("HTTP_GET: Получен запрос на получение предмета " + itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestParam String text) {
        log.info("HTTP_GET: Получен запрос на поиск предмета. Поисковый запрос: " + text);
        return itemClient.findItems(text);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("HTTP_GET: Получен запрос на получение всех предметов пользователя Id = " + userId);
        return itemClient.getAllUserItems(userId);
    }

    @PostMapping("/{itemId}/comment")
    @Validated(OnCreate.class)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody CommentRequestDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
