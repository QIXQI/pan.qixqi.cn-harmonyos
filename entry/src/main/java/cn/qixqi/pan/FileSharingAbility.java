package cn.qixqi.pan;

import cn.qixqi.pan.slice.FileSharingAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class FileSharingAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(FileSharingAbilitySlice.class.getName());
    }
}
