package com.example.jwtdemo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UserForm {
    private String kaptcha;
    private String username;
    private String password;

}
