package cn.qixqi.pan.model;

public class UserExt {

    private String uid;
    private String avatar;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserExt withUid(String uid){
        this.setUid(uid);
        return this;
    }

    public UserExt withAvatar(String avatar){
        this.setAvatar(avatar);
        return this;
    }

    @Override
    public String toString() {
        return "UserExt{" +
                "uid='" + uid + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
