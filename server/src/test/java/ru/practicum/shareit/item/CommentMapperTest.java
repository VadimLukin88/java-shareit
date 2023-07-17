package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private User user;
    private Item item;
    private Comment comment;
    private CommentRequestDto reqDto;
    private CommentResponseDto respDto;

    @BeforeEach
    public void setUp() {
        user = new User(1L, "user1", "user1@email");
        item = new Item(1L, "item1", "description1", true, user, null);
        reqDto = null;
        respDto = null;
        comment = null;
    }

    @Test
    public void testMapCommentToDto() {
        LocalDateTime now = LocalDateTime.now();

        comment = new Comment(1L, "text", item, user, now);
        respDto = CommentMapper.mapCommentToDto(comment);

        assertEquals(respDto.getId(), comment.getId(), "Некорректный Id коментария");
        assertEquals(respDto.getText(), comment.getText(), "Некорректный текст коментария");
        assertEquals(respDto.getAuthorName(), comment.getAuthor().getName(), "Некорректное имя автора");
        assertEquals(respDto.getCreated(), now, "Некорректное время создания");
    }

    @Test
    public void mapDtoToComment() {
        LocalDateTime now = LocalDateTime.now();

        reqDto = new CommentRequestDto(null, "text", 1L, 1L, now);
        comment = CommentMapper.mapDtoToComment(reqDto);

        assertNull(comment.getId(), "Некорректный Id коментария (не равен null)");
        assertNull(comment.getAuthor(), "Автор != null");
        assertNull(comment.getItem(), "Item != null");
        assertEquals(comment.getText(), reqDto.getText(),"Некорректный текст коментария");
        assertEquals(comment.getCreated(), now, "Некорректное время создания");
    }

    @Test
    public void testMapCommentToShort() {
        LocalDateTime now = LocalDateTime.now();

        comment = new Comment(1L, "text", item, user, now);
        CommentShortDto shortDto = CommentMapper.mapCommentToShort(comment);

        assertEquals(shortDto.getId(), comment.getId(), "Некорректный Id коментария");
        assertEquals(shortDto.getText(), comment.getText(), "Некорректный текст коментария");
        assertEquals(shortDto.getAuthorName(), comment.getAuthor().getName(), "Некорректное имя автора");
        assertEquals(shortDto.getCreated(), now, "Некорректное время создания");
    }

}