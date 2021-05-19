package cn.qixqi.pan.datamodel;

public class BottomBarItemInfo {

    private int bnavLayoutId;
    private int bnavTextId;
    private int bnavImgId;
    private int bnavImgSrcId;
    private int bnavActivatedImgSrcId;

    public BottomBarItemInfo() {

    }

    public BottomBarItemInfo(int bnavLayoutId, int bnavTextId, int bnavImgId, int bnavImgSrcId, int bnavActivatedImgSrcId) {
        this.bnavLayoutId = bnavLayoutId;
        this.bnavTextId = bnavTextId;
        this.bnavImgId = bnavImgId;
        this.bnavImgSrcId = bnavImgSrcId;
        this.bnavActivatedImgSrcId = bnavActivatedImgSrcId;
    }

    public int getBnavLayoutId() {
        return bnavLayoutId;
    }

    public void setBnavLayoutId(int bnavLayoutId) {
        this.bnavLayoutId = bnavLayoutId;
    }

    public int getBnavTextId() {
        return bnavTextId;
    }

    public void setBnavTextId(int bnavTextId) {
        this.bnavTextId = bnavTextId;
    }

    public int getBnavImgId() {
        return bnavImgId;
    }

    public void setBnavImgId(int bnavImgId) {
        this.bnavImgId = bnavImgId;
    }

    public int getBnavImgSrcId() {
        return bnavImgSrcId;
    }

    public void setBnavImgSrcId(int bnavImgSrcId) {
        this.bnavImgSrcId = bnavImgSrcId;
    }

    public int getBnavActivatedImgSrcId() {
        return bnavActivatedImgSrcId;
    }

    public void setBnavActivatedImgSrcId(int bnavActivatedImgSrcId) {
        this.bnavActivatedImgSrcId = bnavActivatedImgSrcId;
    }
}
