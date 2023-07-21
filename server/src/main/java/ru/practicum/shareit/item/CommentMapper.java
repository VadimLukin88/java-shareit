package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.model.Comment;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentResponseDto mapCommentToDto(Comment comment) {
        return new CommentResponseDto(comment.getId(),
            comment.getText(),
            comment.getAuthor().getName(),
            comment.getCreated());
    }

    public static Comment mapDtoToComment(CommentRequestDto commentRequestDto) {
        return new Comment(null,
            commentRequestDto.getText(),
            null,
            null,
            commentRequestDto.getCreated());
    }

    public static CommentShortDto mapCommentToShort(Comment comment) {
        return new CommentShortDto(comment.getId(),
            comment.getText(),
            comment.getAuthor().getName(),
            comment.getCreated());
    }
}
