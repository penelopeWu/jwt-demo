package com.example.jwtdemo;

import com.auth0.jwt.interfaces.Claim;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.example.jwtdemo.JwtToken.verifyToken;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtDemoApplicationTests {

    @Test
    public void contextLoads() throws UnsupportedEncodingException {
            String token = JwtToken.createToken();

            System.out.println("Token"+token);

            Map<String ,Claim> claims = JwtToken.verifyToken(token);
            System.out.println(claims.get("name").asString());
            System.out.println(claims.get("age").asInt());
            System.out.println(claims.get("org")==null?null:claims.get("org").asString());;

            //使用过期后的token校验,会直接抛出异常
            String tokenExpired = "";
            Map<String, Claim> claimMap = JwtToken.verifyToken(tokenExpired);
    }

}
