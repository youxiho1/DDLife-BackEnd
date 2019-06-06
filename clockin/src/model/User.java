package model;

public class User {
    private String openId;
    private String session;
    private String token;
    private String tokenTime;

    public User() {
    }

    public User(String openId, String session, String token, String tokenTime) {
        this.openId = openId;
        this.session = session;
        this.token = token;
        this.tokenTime = tokenTime;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenTime() {
        return tokenTime;
    }

    public void setTokenTime(String tokenTime) {
        this.tokenTime = tokenTime;
    }
}
