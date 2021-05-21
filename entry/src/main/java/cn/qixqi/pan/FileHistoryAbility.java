package cn.qixqi.pan;

import cn.qixqi.pan.slice.FileDownloadAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class FileHistoryAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(FileDownloadAbilitySlice.class.getName());
    }
}
