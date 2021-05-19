package cn.qixqi.pan;

import ohos.aafwk.ability.AbilityPackage;
import ohos.app.Context;

public class MyApplication extends AbilityPackage {

    // 全局 Context，偏好数据库使用需要
    private static Context AppContext;

    @Override
    public void onInitialize() {
        super.onInitialize();
        AppContext = this;
    }

    public static Context getAppContext(){
        return AppContext;
    }
}
