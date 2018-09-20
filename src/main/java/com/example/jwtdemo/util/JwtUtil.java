package com.example.jwtdemo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 *     使用jjwt.jar实现的jwt token
 * </p>
 * @author penelope
 */
@ConditionalOnProperty(prefix = "jwt")
public class JwtUtil {
    private Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    /**
     * 存储token
     */
    private String header;
    /**
     * 秘钥
     */
    private String secret;
    /**
     * 有效期限（秒）
     */
    private int expire;

    /**
     * 生成token
     * @param userId
     * @return
     */
    public String generateToken(long userId){
        Date nowDate = new Date();
        return Jwts.builder()
                .setHeaderParam("typ","JWT")
                .setSubject(String.valueOf(userId))
                .setIssuedAt(DateUtils.addDays(nowDate,getExpire()))
                .signWith(SignatureAlgorithm.HS256,getSecret())
                .compact();
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public Claims getClaimByToken(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            return null;
        }
    }

    /**
     * token是否过期
     * @param expiration
     * @return
     */
    public boolean isTokenExpired(Date expiration){
        return expiration.before(new Date());
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
