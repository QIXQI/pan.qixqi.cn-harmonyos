package cn.qixqi.pan.model;

import com.alibaba.fastjson.annotation.JSONField;

public class Token {

    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "token_type")
    private String tokenType;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    @JSONField(name = "expires_in")
    private long expiresIn;
    @JSONField(name = "scope")
    private String scope;
    @JSONField(name = "uid")
    private String uid;
    @JSONField(name = "jti")
    private String jti;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public Token withAccessToken(String accessToken){
        this.setAccessToken(accessToken);
        return this;
    }

    public Token withTokenType(String tokenType){
        this.setTokenType(tokenType);
        return this;
    }

    public Token withRefreshToken(String refreshToken){
        this.setRefreshToken(refreshToken);
        return this;
    }

    public Token withExpiresIn(long expiresIn){
        this.setExpiresIn(expiresIn);
        return this;
    }

    public Token withScope(String scope){
        this.setScope(scope);
        return this;
    }

    public Token withUid(String uid){
        this.setUid(uid);
        return this;
    }

    public Token withJti(String jti){
        this.setJti(jti);
        return this;
    }

    @Override
    public String toString() {
        return "Token{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope='" + scope + '\'' +
                ", uid='" + uid + '\'' +
                ", jti='" + jti + '\'' +
                '}';
    }
}
