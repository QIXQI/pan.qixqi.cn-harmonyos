package cn.qixqi.pan.dao;

import cn.qixqi.pan.model.Token;

/**
 * 存储方式：轻量级偏好数据库
 * 只保存一个令牌
 */
public interface TokenDao {
    void save(Token token);
    void delete();
    Token get();
}
