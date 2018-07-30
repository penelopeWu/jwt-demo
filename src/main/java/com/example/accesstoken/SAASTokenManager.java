package com.example.accesstoken;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SAAS Token管理工具
 *
 * @author pengjunlin
 * @packge com.wlyd.wmscloud.util.SAASTokenManager
 * @date 2016年5月6日 上午10:20:01
 * @comment
 * @update
 */
public class SAASTokenManager {


    /**
     * Token存储对象，保持5000个并发容量(K-useraccount@corCode,V-token)
     */
    public static Map<String, Object> map = new ConcurrentHashMap<String, Object>(
            5000);

    /**
     * 获取用户Token
     *
     * @param key
     * @return
     * @throws
     * @MethodName: getToken
     * @Description:
     */
    public static AccessToken getToken(String key) {
        if (map.containsKey(key)) {
            return (AccessToken) map.get(key);
        }
        return null;
    }

    /**
     * 添加用户token
     *
     * @param key         useraccount@corCode
     * @param accessToken
     * @throws
     * @MethodName: putToken
     * @Description:
     */
    public static void putToken(String key, AccessToken accessToken) {
        map.put(key, accessToken);
    }

    /**
     * 移除token
     *
     * @param key useraccount@corCode
     * @throws
     * @MethodName: removeToken
     * @Description:
     */
    public static void removeToken(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

    /**
     * 验证Token是否过期
     *
     * @param key useraccount@corCode
     * @return
     * @throws
     * @MethodName: isValidateToken
     * @Description:
     */
    public static boolean isValidateToken(String key) {
        if (map.containsKey(key)) {
            AccessToken accessToken = (AccessToken) map.get(key);
            long currentTimestamp = System.currentTimeMillis();
            // 有效时间两小时
            if (accessToken.getLongTime() - currentTimestamp > 2 * 3600 * 1000) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 更新Token
     *
     * @param key         useraccount@corCode
     * @param accessToken
     * @return
     * @throws
     * @MethodName: reputToken
     * @Description:
     */
    public static void reputToken(String key, AccessToken accessToken) {
        if (map.containsKey(key)) {
            putToken(key, accessToken);
        }
    }

    /**
     * 更新Token
     *
     * @param key      useraccount@corCode
     * @param tokenStr
     * @return
     * @throws
     * @MethodName: reputToken
     * @Description:
     */
    public static void reputToken(String key, String tokenStr) {
        if (map.containsKey(key)) {
            AccessToken accessToken = new AccessToken();
            accessToken.setToken(tokenStr);
            accessToken.setTimestamp(new Timestamp(System.currentTimeMillis()));
            putToken(key, accessToken);
        }
    }

    /**
     * 是否包含用户token
     *
     * @param key useraccount@corCode
     * @return
     * @throws
     * @MethodName: iscontainKey
     * @Description:
     */
    public static boolean iscontainKey(String key) {
        return map.containsKey(key);
    }

    /**
     * 生成RSA加密 Token
     *
     * @param platformCode
     * @param tenantCode
     * @return
     * @throws
     * @MethodName: generateToken
     * @Description:
     */
    public static String generateToken(String publicKey, String platformCode, String tenantCode) {
        String str = platformCode + tenantCode + System.currentTimeMillis();
        try {
            byte[] bytes = RSAUtils.encryptByPublicKey(str.getBytes(), publicKey);
            return new String(bytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param args
     * @throws Exception 测试函数入口
     * @throws
     * @MethodName: main
     * @Description:
     */
    public static void main(String[] args) throws Exception {
//        System.out.println(Md5.getMD5Str("123456"));
        String key = "wmsadmin@10000";
        AccessToken accessToken = new AccessToken();
        accessToken.setToken("token==xxjisifdihfifdds");
        accessToken.setTimestamp(new Timestamp(System.currentTimeMillis()));
        putToken(key, accessToken);
        AccessToken accessToken2 = getToken(key);
        System.out.println("token:" + accessToken2.getToken());
        System.out.println("isValidate:" + isValidateToken(key));

        Map<String, Object> keyMap = RSAUtils.genKeyPair();
        String publicKey = RSAUtils.getPublicKey(keyMap);
        String privateKey = RSAUtils.getPrivateKey(keyMap);

        String token = generateToken(publicKey, "abcdefghijklmnopqrstuvwxyz", "10000");

        System.out.println("RSA Token:" + token);

        System.out.println("加密:" + new String(RSAUtils.encryptByPublicKey("this is data".getBytes(), publicKey), "UTF-8"));
        System.out.println("明文:" + new String(RSAUtils.decryptByPrivateKey(RSAUtils.encryptByPublicKey("this is data".getBytes(), publicKey), privateKey)));
    }

}

