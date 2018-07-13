package com.example.jwtdemo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author penelope
 */
public class JwtToken {
    /**
     * 公用秘钥，保存在服务器，客户端不会知道秘钥，以防被攻击
     */
    public static String SECRET = "penelopeWu";

    /**
     * 生成token
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createToken() throws UnsupportedEncodingException {
        //签发时间
        Date iatDate = new Date();
        //过期时间:1分钟过期
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE,1);
        Date expiresDate = nowTime.getTime();

        Map<String,Object> header = new HashMap<>(2);
        header.put("alg","HS256");
        header.put("typ","JWT");
        String token = JWT.create()
                .withHeader(header)
                .withClaim("name","penelopeWu")
                .withClaim("age",24)
                .withClaim("org","alibaba")
                .withExpiresAt(expiresDate)
                .withIssuedAt(iatDate)
                .sign(Algorithm.HMAC256(SECRET));
        return token;
    }

    public static Map<String, Claim> verifyToken(String token) throws UnsupportedEncodingException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT jwt = null;
        try {
            jwt = verifier.verify(token);
        }catch (Exception e){
            throw new RuntimeException("登录凭证已过期，请重新登录");
        }
        return jwt.getClaims();
    }



}
