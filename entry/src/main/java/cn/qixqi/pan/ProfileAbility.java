package cn.qixqi.pan;

import cn.qixqi.pan.slice.ProfileAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class ProfileAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(ProfileAbilitySlice.class.getName());
    }
}
