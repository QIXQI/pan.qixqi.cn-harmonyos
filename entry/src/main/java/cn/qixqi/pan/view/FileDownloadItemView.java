package cn.qixqi.pan.view;

import cn.qixqi.pan.datamodel.FileDownloadItemInfo;
import cn.qixqi.pan.model.FileDownload;
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

public class FileDownloadItemView {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileDownloadItemView.class.getName());

    private List<FileDownloadItemInfo> fileDownloadItemInfos = new ArrayList<>();
    private static final String DOWNLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileDownloadDataAbility";
    private static final String DOWNLOAD_DATA_PATH = "/fileDownload";
    private DataAbilityHelper helper;

    public FileDownloadItemView(Context context, String selectDownloadStatus){
        super();
        helper = DataAbilityHelper.creator(context);
        query_fileDownload(selectDownloadStatus);
    }

    public List<FileDownloadItemInfo> getFileDownloadItemInfos() {
        return fileDownloadItemInfos;
    }

    /**
     * 本地查询下载记录
     * @param selectDownloadStatus
     */
    private void query_fileDownload(String selectDownloadStatus){
        // 查询字段
        String[] columns = new String[] {
                "downloadId", "linkId", "linkName", "fileId", "fileType", "fileSize", "downloadFinishTime", "downloadStatus" };
        // 查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        predicates.equalTo("downloadStatus", selectDownloadStatus);
        try {
            ResultSet resultSet = helper.query(Uri.parse(DOWNLOAD_BASE_URI + DOWNLOAD_DATA_PATH), columns, predicates);
            if (resultSet == null){
                HiLog.debug(LOG_LABEL, "query: resultSet is null");
                return;
            } else if (resultSet.getRowCount() == 0){
                HiLog.debug(LOG_LABEL, "query: resultSet is no result found");
                return;
            }
            resultSet.goToFirstRow();
            do {
                int downloadId = resultSet.getInt(resultSet.getColumnIndexForName("downloadId"));
                String linkId = resultSet.getString(resultSet.getColumnIndexForName("linkId"));
                String linkName = resultSet.getString(resultSet.getColumnIndexForName("linkName"));
                String fileId = resultSet.getString(resultSet.getColumnIndexForName("fileId"));
                String fileType = resultSet.getString(resultSet.getColumnIndexForName("fileType"));
                long fileSize = resultSet.getLong(resultSet.getColumnIndexForName("fileSize"));
                long downloadFinishTime = resultSet.getLong(resultSet.getColumnIndexForName("downloadFinishTime"));
                String downloadStatus = resultSet.getString(resultSet.getColumnIndexForName("downloadStatus"));
                HiLog.debug(LOG_LABEL, String.format("downloadId: %d, linkId: %s, linkName: %s, fileId: %s, fileType: %s, " +
                                "fileSize: %d, downloadFinishTime: %d, downloadStatus: %s", downloadId, linkId, linkName, fileId,
                        fileType, fileSize, downloadFinishTime, downloadStatus));
                FileDownload fileDownload = new FileDownload(downloadId, linkId, linkName, fileId, fileType, fileSize,
                        downloadFinishTime, downloadStatus);
                this.fileDownloadItemInfos.add(
                        new FileDownloadItemInfo()
                                .withFileDownload(fileDownload)
                                .withChecked(false));
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询下载记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询下载记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (NullPointerException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询下载记录发生NullPointerException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }
}
