package com.example.accesstoken;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author penelope
 */
public class AccessToken implements Serializable {
    private static final long serialVersionUID = 4759692267927548118L;
    /**
     * AccessToken字符串
     */
    private String token ;
    /**
     * 时间戳(用于验证token和重新获取token)
     */
    private Timestamp timestamp;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public long getLongTime(){
        if (timestamp != null) {
            return timestamp.getTime();
        }
        return 0;
    }







    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
