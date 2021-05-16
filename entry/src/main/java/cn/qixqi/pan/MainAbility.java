package cn.qixqi.pan;

import cn.qixqi.pan.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());

        startAuthAbility();
    }

    private void startAuthAbility(){
        Intent intent = new Intent();
        // 构造 Operation 对象
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("cn.qixqi.pan")
                .withAbilityName("cn.qixqi.pan.AuthAbility")
                .build();
        // intent 设置 Operation
        intent.setOperation(operation);
        startAbility(intent);
    }




























}
