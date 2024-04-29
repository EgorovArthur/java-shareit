package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // уникальный идентификатор пользователя
    @Column(name = "name")
    private String name; // имя или логин пользователя
    @Column(name = "email", nullable = false, unique = true)
    private String email; // адрес электронной почты (2 пользователя не могут иметь одинаковый email)
}
