package cn.qixqi.pan.model;

import java.util.Date;

public class FileShare {

    private String shareId;
    private String sharePass;
    private String uid;
    private String rootId;
    private Date createTime;
    private Integer activeTime;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getSharePass() {
        return sharePass;
    }

    public void setSharePass(String sharePass) {
        this.sharePass = sharePass;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Integer activeTime) {
        this.activeTime = activeTime;
    }

    @Override
    public String toString() {
        return "FileShare{" +
                "shareId='" + shareId + '\'' +
                ", sharePass='" + sharePass + '\'' +
                ", uid='" + uid + '\'' +
                ", rootId='" + rootId + '\'' +
                ", createTime=" + createTime +
                ", activeTime=" + activeTime +
                '}';
    }
}
