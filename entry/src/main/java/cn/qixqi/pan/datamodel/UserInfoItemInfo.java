package cn.qixqi.pan.datamodel;

public class UserInfoItemInfo {

    private int keyId;
    private String value;
    private int editImgId;

    public UserInfoItemInfo() {

    }

    public UserInfoItemInfo(int keyId, String value, int editImgId) {
        this.keyId = keyId;
        this.value = value;
        this.editImgId = editImgId;
    }

    public int getKeyId() {
        return keyId;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getEditImgId() {
        return editImgId;
    }

    public void setEditImgId(int editImgId) {
        this.editImgId = editImgId;
    }

    @Override
    public String toString() {
        return "UserInfoItemInfo{" +
                "keyId=" + keyId +
                ", value='" + value + '\'' +
                ", editImgId=" + editImgId +
                '}';
    }
}
