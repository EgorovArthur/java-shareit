package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptoins.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private final UserDto userDto = UserDto.builder().id(1L).name("User").email("user@mail.ru").build();
    private final UserUpdateDto userUpdateDto = UserUpdateDto.builder()
            .name("UpdateUser").email("newUser@mail.ru").build();

    @Test
    void testAddUser() {
        User user = UserMapper.toUser(userDto);
        when(userRepository.save(any())).thenReturn(user);
        UserDto result = userService.addUser(userDto);

        assertEquals(result, userDto);
        verify(userRepository).save(any());
    }

    @Test
    void testGetUserById() {
        Long userId = 0L;
        User user = UserMapper.toUser(userDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto actualUserDto = userService.getUserById(userId);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void testGetUserById_whenUserNotFound() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        User user = UserMapper.toUser(userDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto updateUserDto = userService.updateUser(userId, userUpdateDto);

        assertEquals(updateUserDto.getName(), "UpdateUser");
        assertEquals(updateUserDto.getEmail(), "newUser@mail.ru");

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_whenUserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userUpdateDto));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);
        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void testGetUsers() {
        when(userRepository.findAll()).thenReturn(List.of(UserMapper.toUser(userDto)));

        Collection<UserDto> userDtos = userService.getUsers();

        assertFalse(userDtos.isEmpty());
        assertEquals(1, userDtos.size());
        assertTrue(userDtos.contains(userDto));
    }


    @Test
    void testGetUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> userDtos = userService.getUsers();

        assertTrue(userDtos.isEmpty());
    }
}