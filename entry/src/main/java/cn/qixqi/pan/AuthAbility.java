package cn.qixqi.pan;

import cn.qixqi.pan.slice.LoginAbilitySlice;
import cn.qixqi.pan.slice.RegisterAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class AuthAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(LoginAbilitySlice.class.getName());
    }
}
