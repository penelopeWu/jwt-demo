package com.example.jwtdemo.vo;

import lombok.Data;

@Data
public class UserForm {
    private String kaptcha;
    private String username;
    private String password;

}
