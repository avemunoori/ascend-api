package com.ascend.user;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String password;
}
