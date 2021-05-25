package cn.qixqi.pan.data;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.FileDescriptor;

public class FileUploadDataAbility extends Ability {
    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileUploadDataAbility.class.getName());

    private static final String DB_NAME = "fileTransfer.db";
    private static final String DB_TAB_NAME = "fileUpload";
    private static final int DB_VERSION = 1;

    private StoreConfig config = StoreConfig.newDefaultConfig(DB_NAME);
    private RdbStore rdbStore;

    private RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        /**
         * 创建表 fileUpload
         * @param rdbStore
         */
        @Override
        public void onCreate(RdbStore rdbStore) {
            rdbStore.executeSql("create table if not exists " +
                    DB_TAB_NAME + " ( " +
                    "uploadId integer primary key autoincrement," +
                    "linkId text not null," +
                    "linkName text not null," +
                    "fileId text not null," +
                    "fileType text," +
                    "fileSize integer," +
                    "uploadFinishTime integer," +
                    "uploadStatus text)");

            HiLog.debug(LOG_LABEL, "创建表 fileUpload");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {
            HiLog.debug(LOG_LABEL, "更新表 fileUpload");
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        HiLog.info(LOG_LABEL, "FileUploadDataAbility onStart");

        // 初始化数据库连接
        DatabaseHelper helper = new DatabaseHelper(this);
        rdbStore = helper.getRdbStore(config, DB_VERSION, rdbOpenCallback, null);
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        HiLog.debug(LOG_LABEL, "query: dataAbility");
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        ResultSet resultSet = rdbStore.query(rdbPredicates, columns);
        if (resultSet == null){
            HiLog.info(LOG_LABEL, "query 结果为空");
        }
        return resultSet;
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        HiLog.info(LOG_LABEL, "FileUploadDataAbility insert");
        String path = uri.getLastPath();
        if (!"fileUpload".equals(path)){
            HiLog.info(LOG_LABEL, "FileUploadDataAbility insert path not matched!");
            return -1;
        }
        int index = (int) rdbStore.insert(DB_TAB_NAME, value);
        // 插入成功，通知订阅者
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.delete(rdbPredicates);
        HiLog.info(LOG_LABEL, "delete: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates, DB_TAB_NAME);
        int index = rdbStore.update(value, rdbPredicates);
        HiLog.info(LOG_LABEL, "update: " + index);
        DataAbilityHelper.creator(this, uri).notifyChange(uri);
        return index;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
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