package com.jornah.jpa.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author licong
 * @date 2022/10/11 22:14
 */
@Data
public class UserDto {
    String username;
    Long count;
    LocalDateTime time;

    public UserDto(String username, Long count,LocalDateTime time) {
        this.username = username;
        this.count = count;
        this.time = time;
    }
}
