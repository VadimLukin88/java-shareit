package ru.practicum.shareit.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentRequestDto;
import ru.practicum.shareit.comment.dto.CommentShortDto;
import ru.practicum.shareit.comment.model.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentRequestDto mapCommentToDto(Comment comment) {
        return new CommentRequestDto(comment.getId(),
            comment.getText(),
            comment.getItem().getId(),
            comment.getAuthor().getId(),
            comment.getCreated());
    }

    public static Comment mapDtoToComment(CommentRequestDto commentRequestDto) {
        return new Comment(null,
            commentRequestDto.getText(),
            null,
            null,
            LocalDateTime.now());
    }

    public static CommentShortDto mapCommentToShort(Comment comment) {
        return new CommentShortDto(comment.getId(),
            comment.getText(),
            comment.getAuthor().getName(),
            comment.getCreated());
    }
}
