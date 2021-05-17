package cn.qixqi.pan.dao;

import cn.qixqi.pan.model.User;

/**
 * 存储方式：轻量级偏好数据库
 * 只保存一个用户
 */
public interface UserDao {
    void save(User user);
    void delete();
    User get();
}
