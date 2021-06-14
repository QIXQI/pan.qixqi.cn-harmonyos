package cn.qixqi.pan.data;

import cn.qixqi.pan.slice.LoginAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.data.resultset.ResultSet;
import ohos.data.rdb.ValuesBucket;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.Debug;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.MessageParcel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDataAbility extends Ability {
    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileDataAbility.class.getName());
    private String rootDir;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LOG_LABEL, "FileDataAbility onStart");

        rootDir = getFilesDir() + File.separator + "Download";
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        return null;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LOG_LABEL, "FileDataAbility insert");
        return 999;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        return 0;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        HiLog.debug(LOG_LABEL, String.format("rootDir: %s, getDecodedQuery: %s", rootDir, uri.getDecodedQuery()));
        File file = new File(rootDir, uri.getDecodedQuery());
        if (mode == null || !"rw".equals(mode)){
            boolean result = file.setReadOnly();
            HiLog.info(LOG_LABEL, String.format("设置文件只读结果：%b", result));
        }
        FileDescriptor fileDescriptor = null;
        try (FileInputStream inputStream = new FileInputStream(file)){
            fileDescriptor = inputStream.getFD();
            // 绑定文件描述符
            return MessageParcel.dupFileDescriptor(fileDescriptor);
        } catch (IOException ex){
            HiLog.error(LOG_LABEL, String.format("读取文件%s发生异常：%s", uri.getDecodedQuery(), ex.getMessage()));
            ex.printStackTrace();
        }
        return fileDescriptor;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}