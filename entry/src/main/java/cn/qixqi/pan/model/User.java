package cn.qixqi.pan.model;

public class User extends UserBase{

    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public User withAvatar(String avatar){
        this.setAvatar(avatar);
        return this;
    }

    public User withUserBase(UserBase userBase){
        this
                .withUid(userBase.getUid())
                .withPhoneNumber(userBase.getPhoneNumber())
                .withUname(userBase.getUname())
                .withPassword(userBase.getPassword())
                .withEmail(userBase.getEmail())
                .withPriorityId(userBase.getPriorityId())
                .withStatusId(userBase.getStatusId())
                .withSex(userBase.getSex())
                .withDiskCapacity(userBase.getDiskCapacity())
                .withFreeDiskCapacity(userBase.getFreeDiskCapacity())
                .withBirthday(userBase.getBirthday())
                .withJoinTime(userBase.getJoinTime());
        return this;
    }

    public User withUserExt(UserExt userExt){
        this.setAvatar(userExt.getAvatar());
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "avatar='" + avatar + '\'' +
                "} " + super.toString();
    }
}
