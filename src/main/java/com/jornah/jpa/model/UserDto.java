package com.jornah.jpa.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author licong
 * @date 2022/10/11 22:14
 */
@Data
@NoArgsConstructor
public class UserDto {
    Long id;
    private String username;
    private Long count;
    private LocalDateTime time;

    public UserDto(String username, Long count,LocalDateTime time) {
        this.username = username;
        this.count = count;
        this.time = time;
    }
}
