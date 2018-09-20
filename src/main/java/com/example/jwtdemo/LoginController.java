package com.example.jwtdemo;

import com.example.jwtdemo.util.JwtUtil;
import com.example.jwtdemo.util.ShiroUtils;
import com.example.jwtdemo.vo.UserForm;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author penelope
 */
@Controller
public class LoginController {

    private JwtUtil jwtUtils = new JwtUtil();

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserForm formInfo) {
        String kaptcha = ShiroUtils.getKaptcha(formInfo.getUsername());
        if (!formInfo.getKaptcha().equalsIgnoreCase(kaptcha)) {
            throw new RuntimeException("验证码不正确");
        }
        UsernamePasswordToken token = new UsernamePasswordToken(formInfo.getUsername(), formInfo.getPassword());
        Subject subject = ShiroUtils.getSubject();
        subject.login(token);

        //登录成功后直接返回token
        return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.generateToken(12345));
    }
}
