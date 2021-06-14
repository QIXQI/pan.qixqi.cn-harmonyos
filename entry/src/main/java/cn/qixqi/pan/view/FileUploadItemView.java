package cn.qixqi.pan.view;

import cn.qixqi.pan.datamodel.FileUploadItemInfo;
import cn.qixqi.pan.model.FileUpload;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class FileUploadItemView {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileUploadItemView.class.getName());

    private List<FileUploadItemInfo> fileUploadItemInfos = new ArrayList<>();
    private DataAbilityHelper helper;
    private static final String UPLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileUploadDataAbility";
    private static final String UPLOAD_DATA_PATH = "/fileUpload";

    public FileUploadItemView(Context context, String selectUploadStatus) {
        super();
        helper = DataAbilityHelper.creator(context);
        query_fileUpload(selectUploadStatus);
    }

    public List<FileUploadItemInfo> getFileUploadItemInfos(){
        return this.fileUploadItemInfos;
    }

    /**
     * 本地查询上传记录
     * @param selectUploadStatus
     */
    private void query_fileUpload(String selectUploadStatus){
        // 查询字段
        String[] columns = new String[] {
                "uploadId", "linkId", "linkName", "fileId", "fileType", "fileSize", "uploadFinishTime", "uploadStatus" };
        // 查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo("uploadStatus", selectUploadStatus);
        try {
            ResultSet resultSet = helper.query(Uri.parse(UPLOAD_BASE_URI + UPLOAD_DATA_PATH), columns, predicates);
            if (resultSet == null){
                HiLog.debug(LOG_LABEL, "query: resultSet is null");
                return;
            } else if (resultSet.getRowCount() == 0){
                HiLog.debug(LOG_LABEL, "query: resultSet is no result found");
                return;
            }
            resultSet.goToFirstRow();
            do {
                int uploadId = resultSet.getInt(resultSet.getColumnIndexForName("uploadId"));
                String linkId = resultSet.getString(resultSet.getColumnIndexForName("linkId"));
                String linkName = resultSet.getString(resultSet.getColumnIndexForName("linkName"));
                String fileId = resultSet.getString(resultSet.getColumnIndexForName("fileId"));
                String fileType = resultSet.getString(resultSet.getColumnIndexForName("fileType"));
                long fileSize = resultSet.getLong(resultSet.getColumnIndexForName("fileSize"));
                long uploadFinishTime = resultSet.getLong(resultSet.getColumnIndexForName("uploadFinishTime"));
                String uploadStatus = resultSet.getString(resultSet.getColumnIndexForName("uploadStatus"));
                HiLog.debug(LOG_LABEL, String.format("uploadId: %d, linkId: %s, linkName: %s, fileId: %s, fileType: %s, " +
                                "fileSize: %d, uploadFinishTime: %d, uploadStatus: %s", uploadId, linkId, linkName, fileId,
                        fileType, fileSize, uploadFinishTime, uploadStatus));
                FileUpload fileUpload = new FileUpload(uploadId, linkId, linkName, fileId, fileType, fileSize, uploadFinishTime, uploadStatus);
                this.fileUploadItemInfos.add(
                        new FileUploadItemInfo()
                                .withFileUpload(fileUpload)
                                .withChecked(false));
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询上传记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询上传记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (NullPointerException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询上传记录发生NullPointerException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
