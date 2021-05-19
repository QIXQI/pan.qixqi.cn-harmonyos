package cn.qixqi.pan;

import cn.qixqi.pan.slice.FileSystemAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class FileSystemAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(FileSystemAbilitySlice.class.getName());
    }
}
