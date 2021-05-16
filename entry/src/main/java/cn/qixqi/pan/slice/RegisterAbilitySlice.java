package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;


public class RegisterAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent){
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_register);
    }

    @Override
    public void onActive(){
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent){
        super.onForeground(intent);
    }
}
