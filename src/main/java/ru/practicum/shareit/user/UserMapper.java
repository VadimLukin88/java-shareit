package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapDtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static UserShortDto mapUserToShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

}
