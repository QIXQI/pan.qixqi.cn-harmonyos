package cn.qixqi.pan.slice;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.dao.TokenDao;
import cn.qixqi.pan.dao.impl.TokenDaoImpl;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.datamodel.FileItemInfo;
import cn.qixqi.pan.datamodel.FolderItemInfo;
import cn.qixqi.pan.filter.GifSizeFilter;
import cn.qixqi.pan.model.*;
import cn.qixqi.pan.model.File;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.FastDFSUtil;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.Toast;
import cn.qixqi.pan.view.BottomBarFSItemView;
import cn.qixqi.pan.view.BottomBarItemView;
import cn.qixqi.pan.view.FileItemView;
import cn.qixqi.pan.view.FolderItemView;
import cn.qixqi.pan.view.adapter.ChildItemProvider;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MatisseAbility;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import ohos.aafwk.ability.*;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.bundle.IBundleManager;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.Debug;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import okhttp3.Call;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.digest.DigestUtils;
import org.csource.common.MyException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.stream.IntStream;

public class FileSystemAbilitySlice extends AbilitySlice implements ChildItemProvider.Callback{

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, FileSystemAbilitySlice.class.getName());
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 520;

    // ListContainer 弹性回滚效果参数
    private static final int OVER_SCROLL_PERCENT = 40;
    private static final float OVER_SCROLL_RATE = 0.6f;
    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private static final int REQUEST_CODE_CHOOSE = 1;
    private List<Uri> mSelected;

    private TokenDao tokenDao;
    private FolderLink folderLink;
    private AbilitySlice abilitySlice;
    private DataAbilityHelper helper;
    private static final String UPLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileUploadDataAbility";
    private static final String DOWNLOAD_BASE_URI = "dataability:///cn.qixqi.pan.data.FileDownloadDataAbility";
    private static final String UPLOAD_DATA_PATH = "/fileUpload";
    private static final String DOWNLOAD_DATA_PATH = "/fileDownload";
    private String rootDir;

    private static final String GET_FOLDER_LINK_URL = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/folderLink";
    private static final String GET_FILE_ID_URL = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/fileMd5";
    private static final String ADD_FILE_URL = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/file";
    private static final String ADD_FOLDER_CHILDREN = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/folderLink/child/children";
    private static final String GET_FILE_URL = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/file/%s/url";
    private static final String SHARE_FILE = "http://ali4.qixqi.cn:5555/api/filesharing/v1/filesharing/fileShare/generator";
    private static final String DELETE_FILE = "http://ali4.qixqi.cn:5555/api/filesystem/v1/filesystem/folderLink/child/children";

    // private ListContainer foldersContainer;
    // private ListContainer filesContainer;
    // private FileItemProvider fileItemProvider;
    // private FolderItemProvider folderItemProvider;
    private ListContainer childrenContainer;
    private ChildItemProvider childItemProvider;
    private FolderChildren selectedChildren;
    private int selectedItemCount = 0;

    private Text title;
    private DirectionalLayout downloadItemLayout;
    private DirectionalLayout uploadItemLayout;

    private List<BottomBarItemInfo> bottomBarItemInfoList;
    // 子文件、子文件夹选中时的底部导航栏
    private List<BottomBarItemInfo> bottomBarFSItemInfoList;
    private Component bottomBar;
    private Component bottomBarFS;
    private boolean isItemSelected = false;     // 子文件（夹）未被选中

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_file_system);

        abilitySlice = this;
        helper = DataAbilityHelper.creator(this);
        rootDir = getFilesDir() + java.io.File.separator + "Download" + java.io.File.separator;

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        tokenDao = new TokenDaoImpl(MyApplication.getAppContext());
        initView();
        initListener();

        // 设置底部导航栏
        setBottomToolBar();

        getFolderLink();

        // 应用请求权限
        requestPermissions();
    }

    /**
     * 初始化控件和布局
     */
    private void initView(){
        // foldersContainer = (ListContainer) findComponentById(ResourceTable.Id_folders_container);
        // filesContainer = (ListContainer) findComponentById(ResourceTable.Id_files_container);
        childrenContainer = (ListContainer) findComponentById(ResourceTable.Id_children_container);
        // **********标题栏*************
        title = (Text) findComponentById(ResourceTable.Id_title);
        title.setText(ResourceTable.String_file_title);
        downloadItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_download_item_layout);
        uploadItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_upload_item_layout);
        // **********底部导航栏************
        bottomBar = (DirectionalLayout) findComponentById(ResourceTable.Id_bottom_bar);
        bottomBarFS = (DirectionalLayout) findComponentById(ResourceTable.Id_bottom_bar_selected);
    }

    /**
     * 初始化监听器
     */
    private void initListener(){
        // **********标题栏*************
        // 标题文本点击事件
        title.setClickedListener( component -> {
            Toast.makeToast(abilitySlice, "点击标题", Toast.TOAST_SHORT).show();
        });
        // downloadItemLayout 点击事件
        downloadItemLayout.setClickedListener(component -> {
            // Toast.makeToast(abilitySlice, "点击downloadItemLayout", Toast.TOAST_SHORT).show();
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("cn.qixqi.pan")
                    .withAbilityName("cn.qixqi.pan.FileHistoryAbility")
                    .build();
            intent.setOperation(operation);
            // 释放掉栈内所有的 Ability，不再返回先前页面
            intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
            startAbility(intent);
        });
        // 点击 uploadItemLayout
        uploadItemLayout.setClickedListener( component -> {
            Toast.makeToast(abilitySlice, "点击uploadItemLayout", Toast.TOAST_SHORT).show();
            /* if (verifyCallingPermission("ohos.permission.CAMERA") != IBundleManager.PERMISSION_GRANTED ||
                verifyCallingPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED ||
                verifyCallingPermission("ohos.permission.WRITE_MEDIA") != IBundleManager.PERMISSION_GRANTED) {
                // 没有权限
                HiLog.error(LOG_LABEL, "没有权限");
            } */
            if (verifySelfPermission("ohos.permission.WRITE_USER_STORAGE") != IBundleManager.PERMISSION_GRANTED ||
                    verifySelfPermission("ohos.permission.CAMERA") != IBundleManager.PERMISSION_GRANTED ||
                    verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED) {

                HiLog.debug(LOG_LABEL, "没有权限，尝试申请权限");
                // 没有权限
                if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                    // ohos.permission.READ_MEDIA 允许弹框授权
                    HiLog.debug(LOG_LABEL, "ohos.permission.READ_MEDIA 允许弹框授权");
                    requestPermissionsFromUser(
                            new String[]{"ohos.permission.READ_MEDIA",
                                    "ohos.permission.WRITE_MEDIA",
                                    "ohos.permission.MEDIA_LOCATION",
                                    "ohos.permission.CAMERA",
                                    "ohos.permission.WRITE_USER_STORAGE"
                            }, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    // ohos.permission.READ_MEDIA 不允许弹框授权
                    HiLog.warn(LOG_LABEL, "ohos.permission.READ_MEDIA 不允许弹框授权");
                }
            } else {
                // 有权限
                HiLog.debug(LOG_LABEL, "有权限，即将访问相册");
                Matisse.from(abilitySlice)
                        .choose(MimeType.ofAll())
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .countable(true)
                        .capture(true)
                        .maxSelectable(9)
                        .originalEnable(false)
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });
    }

    /**
     * 启动 Ability 返回结果处理
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    protected void onAbilityResult(int requestCode, int resultCode, Intent resultData) {
        super.onAbilityResult(requestCode, resultCode, resultData);

        switch (requestCode){
            case REQUEST_CODE_CHOOSE:
                if (resultCode == MatisseAbility.RESULT_OK){
                    mSelected = Matisse.obtainResult(resultData);
                    ArrayList<Uri> uriArrayList = resultData.getSequenceableArrayListParam(MatisseAbility.EXTRA_RESULT_SELECTION);
                    // stringArrayList 是真实文件路径列表
                    ArrayList<String> stringArrayList = resultData.getStringArrayListParam(MatisseAbility.EXTRA_RESULT_SELECTION_PATH);
                    HiLog.debug(LOG_LABEL, "mSelected: " + mSelected);
                    HiLog.debug(LOG_LABEL, "uriArrayList: " + uriArrayList);
                    HiLog.debug(LOG_LABEL, "stringArrayList: " + stringArrayList);

                    // 主线程不能进行网络请求，新开一个线程判断文件是否存在
                    getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                            .asyncDispatch( () -> {
                                try {
                                    isFileExist(uriArrayList.get(0), stringArrayList.get(0));
                                } catch (IOException ex){
                                    HiLog.error(LOG_LABEL, String.format("获取文件流发生异常，异常信息：%s", ex.getMessage()));
                                    ex.printStackTrace();
                                } catch (DataAbilityRemoteException ex){
                                    HiLog.error(LOG_LABEL, String.format("使用文件描述符打开文件%s, 异常：%s",stringArrayList.get(0), ex.getMessage()));
                                }
                            });
                } else {
                    HiLog.warn(LOG_LABEL, String.format("没有选取文件或访问相册失败，resultCode=%s, resultData=%s", String.valueOf(resultCode),
                            resultData == null ? "null" : resultData.toString()
                    ));
                }
                break;

            default:
                break;
        }
    }

    /**
     * 上传文件
     * @param fileUri
     * @param filePath
     * @param fileId
     */
    private void uploadFile(Uri fileUri, String filePath, String fileId){

        /* InputStream in = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            in = new FileInputStream(filePath);
            byte[] buf = new byte[1024];
            int length = 0;
            while ((length = in.read(buf)) != -1){
                out.write(buf, 0, length);
            }
        } catch (Exception ex){
            HiLog.error(LOG_LABEL, String.format("11111111111111111上传文件%s发生异常，异常信息：%s", filePath, ex.getMessage()));
            ex.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException ex){
                    HiLog.error(LOG_LABEL, String.format("22222222222222222222222关闭流发生异常：%s", ex.getMessage()));
                    ex.printStackTrace();
                }
            }
        }
        byte[] outByte = out.toByteArray();
        HiLog.debug(LOG_LABEL, String.format("byte: %s", outByte.toString())); */


        // 测试
        /* filePath = "assets/entry/resources/rawfile/pan/test.jpg";
        File file = new File(filePath);
        HiLog.debug(LOG_LABEL, file.getAbsolutePath());
        HiLog.debug(LOG_LABEL, file.exists() ? "存在" : "不存在");
        HiLog.debug(LOG_LABEL, file.canRead() ? "可读" : "不可读");
        HiLog.debug(LOG_LABEL, file.canWrite() ? "可写" : "不可写");
        HiLog.debug(LOG_LABEL, file.canExecute() ? "可执行" : "不可执行");

        ResourceManager resourceManager = abilitySlice.getResourceManager();
        RawFileEntry rawFileEntry = resourceManager.getRawFileEntry("resources/rawfile/pan/test.jpg"); */

        try{
            /* Resource resource = rawFileEntry.openRawFile();
            int len = resource.available();
            byte[] buffer = new byte[len];
            resource.read(buffer, 0, len);

            HiLog.debug(LOG_LABEL, FastDFSUtil.uploadBytes(buffer, 0, len, "jpg")); */


            // 获取文件扩展名
            String fileExtName = "";
            // 获取文件名
            String fileName = "";
            try {
                fileExtName = filePath.substring(filePath.lastIndexOf(".")+1);
                fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            } catch (Exception e){
                HiLog.warn(LOG_LABEL, String.format("文件%s后缀名或文件名解析发生异常：%s", filePath, e.getMessage()));
                e.printStackTrace();
                fileExtName = "unknown";
                fileName = "unknown";
            }

            FileDescriptor fd = helper.openFile(fileUri, "r");
            FileInputStream inputStream = new FileInputStream(fd);
            HiLog.debug(LOG_LABEL, String.format("上传文件前长度：%d", inputStream.available()));

            // 上传文件
            String fileUrl = FastDFSUtil.uploadByStream(fileExtName, inputStream);
            HiLog.debug(LOG_LABEL, String.format("上传成功！文件地址：%s", fileUrl));

            // 通过 FileInputStream 获取文件大小，返回int，最多获取1.9G，且是 available
            // 解决办法：通过 java.nio.*下的新工具——FileChannel
            FileChannel fileChannel = inputStream.getChannel();

            // 添加文件实体记录
            File file = new File();
            file.setFileId(fileId);
            file.setFileName(fileName);
            file.setFileType(fileExtName);
            file.setFileSize(fileChannel.size());
            file.setUrl(fileUrl);
            inputStream.close();

            addFileEntity(file);
        } catch (IOException ex){
            HiLog.error(LOG_LABEL, String.format("获取文件流发生异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (MyException ex){
            HiLog.error(LOG_LABEL, String.format("上传文件MyException，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (Exception ex){
            // [TODO] 上传文件Exception，异常信息：Attempt to invoke virtual method 'java.lang.String java.net.InetAddress.getHostAddress()' on a null object reference
            HiLog.error(LOG_LABEL, String.format("上传文件Exception，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }

        /* FastDFSFile fastDFSFile = new FastDFSFile();
        fastDFSFile.setName(filePath);
        fastDFSFile.setExt("jpg");
        try {
            HiLog.debug(LOG_LABEL, FastDFSUtil.upload(fastDFSFile));
        } catch (IOException ex){
            HiLog.error(LOG_LABEL, String.format("上传文件IOException，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (MyException ex){
            HiLog.error(LOG_LABEL, String.format("上传文件MyException，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (Exception ex){
            HiLog.error(LOG_LABEL, String.format("上传文件Exception，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }*/
    }

    /**
     * 根据文件md5值，判断文件是否已经存在
     * @param fileUri
     * @param filePath
     *      string[0] exist "yes" "no"
     *      string[1] fileId
     */
    private void isFileExist(Uri fileUri, String filePath) throws IOException, DataAbilityRemoteException {
        FileDescriptor fd = helper.openFile(fileUri, "r");
        // 使用文件描述符，获取文件流
        FileInputStream inputStream = new FileInputStream(fd);

        HiLog.debug(LOG_LABEL, String.format("读取前，输入流长度：%d", inputStream.available()));
        // inputStream.mark(0);
        // 计算文件 md5值
        String md5 = DigestUtils.md5Hex(inputStream);
        HiLog.debug(LOG_LABEL, String.format("文件md5值：%s", md5));
        // inputStream.reset();
        HiLog.debug(LOG_LABEL, String.format("读取后，输入流长度：%d", inputStream.available()));
        inputStream.close();

        String url = String.format("%s/%s", GET_FILE_ID_URL, md5);
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.get(url, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    // 解析 JSON 字符串
                    JSONObject object = JSONObject.parseObject(responseStr);
                    String exist = object.getString("exist");
                    HiLog.debug(LOG_LABEL, String.format("exist: %s", exist));

                    if ("yes".equals(exist)){
                        // 文件已存在
                        // 文件夹链接添加文件链接记录
                        String fileJson = object.getString("file");
                        File file = JSON.parseObject(fileJson, File.class);
                        addFileLinkOfFolder(file);
                    } else if ("no".equals(exist)){
                        // 文件不存在
                        // 上传文件
                        String fileId = object.getString("fileId");
                        HiLog.debug(LOG_LABEL, String.format("文件不存在，生成fileId: %s", fileId));
                        uploadFile(fileUri, filePath, fileId);
                    } else {
                        HiLog.error(LOG_LABEL, String.format("exist: %s", exist));
                    }
                } else {
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });
    }

    /**
     * 添加文件实体记录
     * @param file
     */
    private void addFileEntity(File file){
        String fileJson = JSON.toJSONString(file);
        HiLog.debug(LOG_LABEL, String.format("fileJson: %s", fileJson));

        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, fileJson);
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.post(ADD_FILE_URL, requestBody,null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    // 解析 JSON 字符串
                    File file = JSON.parseObject(responseStr, File.class);
                    HiLog.debug(LOG_LABEL, String.format("添加文件实体成功：%s", file.toString()));

                    // 文件夹链接添加文件链接记录
                    addFileLinkOfFolder(file);
                } else {
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });
    }

    /**
     * 文件夹链接添加文件链接记录
     * @param file
     */
    private void addFileLinkOfFolder(File file){
        if (file == null){
            HiLog.error(LOG_LABEL, "文件夹链接添加文件链接记录，file为空");
            return;
        }

        FileLink fileLink = new FileLink();
        fileLink.setLinkName(file.getFileName());
        fileLink.setFileId(file.getFileId());
        fileLink.setFileType(file.getFileType());
        fileLink.setFileSize(file.getFileSize());
        List<FileLink> fileLinks = new ArrayList<>();
        fileLinks.add(fileLink);

        FolderChildren children = new FolderChildren();
        children.setFiles(fileLinks);

        FolderLink folderLink1 = new FolderLink();
        folderLink1.setFolderId(folderLink.getFolderId());
        folderLink1.setUid(folderLink.getUid());
        folderLink1.setChildren(children);

        String folderLink1Json = JSON.toJSONString(folderLink1);
        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, folderLink1Json);
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);

        HttpUtil.post(ADD_FOLDER_CHILDREN, requestBody, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    // 解析 JSON 字符串
                    JSONObject object = JSONObject.parseObject(responseStr);
                    int status = object.getIntValue("status");
                    HiLog.debug(LOG_LABEL, String.format("status: %d", status));
                    if (status <= 0){
                        // 添加失败
                        HiLog.debug(LOG_LABEL, "文件夹链接添加文件链接记录，失败！");
                    } else {
                        HiLog.debug(LOG_LABEL, "文件夹链接添加文件链接记录，成功！");

                        // 解析 folderLink 添加子文件夹链接列表与子文件链接列表成功，返回的子项
                        FolderChildren children1 = object.getObject("children", FolderChildren.class);

                        // 本地添加上传记录
                        insert_fileUpload(children1.getFiles().get(0));

                        // 本地查询上传记录
                        query_fileUpload();

                        // 在主线程（UI线程）弹窗：文件上传成功
                        TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
                        uiTaskDispatcher.asyncDispatch(() -> {
                            Toast.makeToast(abilitySlice, String.format("文件%s上传成功！", file.getFileName()), Toast.TOAST_SHORT).show();
                        });

                        // 刷新当前文件夹
                        getFolderLink();
                    }
                } else {
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });

    }

    /**
     * 获取文件夹列表和文件列表后，设置 ListContainer
     */
    private void setListContainer(){
        if (folderLink == null){
            HiLog.warn(LOG_LABEL, "folderLink 为空");
            return;
        }
        // 不需要手动释放 folderItemProvider 和 fileItemProvider，JAVA 自动回收
        FolderItemView folderItemView = new FolderItemView(folderLink.getChildren().getFolders());
        // folderItemProvider = new FolderItemProvider(folderItemView.getFolderItemInfos());
        FileItemView fileItemView = new FileItemView(folderLink.getChildren().getFiles());
        // fileItemProvider = new FileItemProvider(fileItemView.getFileItemInfos());
        childItemProvider = new ChildItemProvider(folderItemView.getFolderItemInfos(), fileItemView.getFileItemInfos(),
                FileSystemAbilitySlice.this);

        // foldersContainer.setItemProvider(folderItemProvider);
        // filesContainer.setItemProvider(fileItemProvider);
        childrenContainer.setItemProvider(childItemProvider);

        // 设置 ListContainer 的事件监听器
        setListClickListener();

        // 设置文件列表回滚动画
        setListReboundAnimation();
    }

    /**
     * 设置 ListContainer 的事件监听器
     */
    private void setListClickListener(){
        // foldersContainer 子项单击事件
        /* foldersContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            FolderItemInfo folderItemInfo = (FolderItemInfo) folderItemProvider.getItem(position);
            Toast.makeToast(abilitySlice, folderItemInfo.toString(), Toast.TOAST_SHORT).show();
        }); */

        // filesContainer 子项单击事件
        /* filesContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            FileItemInfo fileItemInfo = (FileItemInfo) fileItemProvider.getItem(position);
            Toast.makeToast(abilitySlice, fileItemInfo.toString(), Toast.TOAST_SHORT).show();
        }); */

        // childrenContainer 子项单击事件
        childrenContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            Object object = childItemProvider.getItem(position);
            // Toast.makeToast(abilitySlice, object.toString(), Toast.TOAST_LONG).show();
            HiLog.debug(LOG_LABEL, String.format("position: %d, object: %s", position, object.toString()));
        });

        // childrenContainer 子项长按事件
        childrenContainer.setItemLongClickedListener( (listContainer, component, position, id) -> {
            if (childItemProvider.isFolder(position)){
                // 选中子文件夹
                FolderItemInfo folderItemInfo = (FolderItemInfo) childItemProvider.getItem(position);
                folderItemInfo.setChecked(!folderItemInfo.isChecked());
                selectedItemCount += folderItemInfo.isChecked() ? 1 : -1;
            } else {
                // 选中子文件
                FileItemInfo fileItemInfo = (FileItemInfo) childItemProvider.getItem(position);
                fileItemInfo.setChecked(!fileItemInfo.isChecked());
                selectedItemCount += fileItemInfo.isChecked() ? 1 : -1;
            }
            childItemProvider.notifyDataSetItemChanged(position);
            if (!isItemSelected){
                setSelectedBottomToolBar();
            }
            if (selectedItemCount <= 0){
                setBottomToolBar();
            }
            return false;
        });
    }

    /**
     * 设置文件列表回滚动画
     */
    private void setListReboundAnimation() {
        // foldersContainer.setReboundEffect(true);
        // foldersContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);

        // filesContainer.setReboundEffect(true);
        // filesContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);

        childrenContainer.setReboundEffect(true);
        childrenContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
    }

    /**
     * 获取当前文件夹
     */
    private void getFolderLink(){
        String url = String.format("%s/%s", GET_FOLDER_LINK_URL, "7616b8db-fa5b-48a2-a2ec-4ac839cbf4b7");
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.get(url, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    HiLog.debug(LOG_LABEL, responseStr);
                    folderLink = JSON.parseObject(responseStr, FolderLink.class);
                    HiLog.debug(LOG_LABEL, folderLink.toString());
                    HiLog.debug(LOG_LABEL, String.format("file size: %d", folderLink.getChildren().getFiles().size()));

                    // 设置 ListContainer
                    // 在主线程(UI线程)中执行
                    TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
                    uiTaskDispatcher.asyncDispatch(() -> {
                        setListContainer();
                    });
                } else {
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });
    }

    /**
     * 设置底部导航栏
     */
    private void setBottomToolBar(){
        bottomBar.setVisibility(Component.VISIBLE);
        bottomBarFS.setVisibility(Component.HIDE);
        isItemSelected = false;
        selectedItemCount = 0;

        BottomBarItemView bottomBarItemView = new BottomBarItemView();
        bottomBarItemInfoList = bottomBarItemView.getBottomBarItemInfos();

        IntStream.range(0, bottomBarItemInfoList.size()).forEach( position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavLayoutId());
            // bottomItemLayout.setVisibility(Component.VISIBLE);
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavTextId());
            if (position == 0){
                // 设为选中
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavActivatedImgSrcId());
                text.setTextColor(Color.BLUE);
            } else {
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavImgSrcId());
            }

            // 设置子项点击事件
            bottomItemLayout.setClickedListener( component -> {
                unselected();
                HiLog.debug(LOG_LABEL, "设置子项点击事件：" + position);
                image.setImageAndDecodeBounds(
                        bottomBarItemInfoList.get(position).getBnavActivatedImgSrcId());
                text.setTextColor(Color.BLUE);
                startAbilityFromBnav(position);
            });
        });
    }

    /**
     * 底部导航栏实现导航功能
     * @param position
     */
    private void startAbilityFromBnav(int position){
        if (position == 0){
            return;
        } else if (position == 1){
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("cn.qixqi.pan")
                    .withAbilityName("cn.qixqi.pan.FileSharingAbility")
                    .build();
            intent.setOperation(operation);
            // 释放掉栈内所有的 Ability，不再返回先前页面
            intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
            startAbility(intent);
        } else if (position == 2) {
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("cn.qixqi.pan")
                    .withAbilityName("cn.qixqi.pan.ProfileAbility")
                    .build();
            intent.setOperation(operation);
            // 释放掉栈内所有的 Ability，不再返回先前页面
            intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
            startAbility(intent);
        }
    }

    /**
     * 底部导航栏全部子项取消选中
     */
    private void unselected(){
        IntStream.range(0, bottomBarItemInfoList.size()).forEach( position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavLayoutId());
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavTextId());
            image.setPixelMap(bottomBarItemInfoList.get(position).getBnavImgSrcId());
            text.setTextColor(Color.BLACK);
        });
    }

    /**
     * 设置底部选中导航栏
     */
    private void setSelectedBottomToolBar(){
        bottomBar.setVisibility(Component.HIDE);
        bottomBarFS.setVisibility(Component.VISIBLE);
        // 选中子文件、子文件夹
        isItemSelected = true;

        BottomBarFSItemView bottomBarFSItemView = new BottomBarFSItemView();
        bottomBarFSItemInfoList = bottomBarFSItemView.getBottomBarItemInfos();

        IntStream.range(0, bottomBarFSItemInfoList.size()).forEach( position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarFSItemInfoList.get(position).getBnavLayoutId());
            // bottomItemLayout.setVisibility(Component.VISIBLE);
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarFSItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarFSItemInfoList.get(position).getBnavTextId());

            // 设置子项点击事件
            bottomItemLayout.setClickedListener( component -> {
                // Toast.makeToast(abilitySlice, String.format("点击：%s", text.getText()), Toast.TOAST_SHORT).show();
                clickActionFromBnav(position);
            });
        });
    }

    /**
     * 底部导航栏点击动作
     * @param position
     */
    private void clickActionFromBnav(int position){
        switch (position){
            case 0:
                HiLog.debug(LOG_LABEL, "即将开始下载文件 ...");
                selectedChildren = childItemProvider.getSelectedChildren();
                HiLog.debug(LOG_LABEL, selectedChildren.toString());
                startDownload(selectedChildren);
                break;
            case 1:
                HiLog.debug(LOG_LABEL, "即将开始分享文件 ...");
                selectedChildren = childItemProvider.getSelectedChildren();
                HiLog.debug(LOG_LABEL, selectedChildren.toString());
                // 新开启一个线程，进行网络请求
                getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                        .asyncDispatch( () -> {
                            startShare(cloneChildren(selectedChildren));
                        });
                break;
            case 2:
                HiLog.debug(LOG_LABEL, "即将开始删除文件 ...");
                selectedChildren = childItemProvider.getSelectedChildren();
                HiLog.debug(LOG_LABEL, selectedChildren.toString());
                // 新开启一个线程，进行网络请求
                getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                        .asyncDispatch( () -> {
                            startDelete(cloneChildren(selectedChildren));
                        });
                break;
            case 3:
                HiLog.debug(LOG_LABEL, "即将开始重命名文件 ...");
                selectedChildren = childItemProvider.getSelectedChildren();
                HiLog.debug(LOG_LABEL, selectedChildren.toString());
                startRename(cloneChildren(selectedChildren));
                break;
            default:
                HiLog.error(LOG_LABEL, "底部导航栏点击动作越界！");
                break;
        }
    }

    /**
     * 开始重命名文件
     * @param children
     */
    private void startRename(FolderChildren children){
        HiLog.debug(LOG_LABEL, "开始重命名文件");
        if (selectedItemCount == 1){
            HiLog.debug(LOG_LABEL, "文件或文件夹被单选");
        } else {
            HiLog.warn(LOG_LABEL, "只有单选时才可重命名");
            Toast.makeToast(abilitySlice, "只有单选时才可重命名", Toast.TOAST_SHORT).show();
        }
    }

    /**
     * 开始删除文件
     * @param children
     */
    private void startDelete(FolderChildren children){
        HiLog.debug(LOG_LABEL, "开始删除文件");
        // 文件夹
        children.getFolders().clear();
        // 文件
        FolderLink deletedFolder = new FolderLink();
        deletedFolder.setFolderId(folderLink.getFolderId());
        deletedFolder.setFolderName(folderLink.getFolderName());
        deletedFolder.setUid(folderLink.getUid());
        deletedFolder.setParent(folderLink.getParent());
        deletedFolder.setCreateTime(folderLink.getCreateTime());
        deletedFolder.setChildren(children);
        // 删除文件
        deleteFile(deletedFolder);
    }

    /**
     * 删除文件
     * @param deletedFolder
     */
    private void deleteFile(FolderLink deletedFolder){
        String deletedFolderJson = JSON.toJSONString(deletedFolder);
        HiLog.debug(LOG_LABEL, String.format("deletedFolderJson: %s", deletedFolderJson));

        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, deletedFolderJson);
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.delete(DELETE_FILE, requestBody, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                JSONObject object = JSONObject.parseObject(responseStr);
                long status = object.getLongValue("status");
                if (status > 0){
                    HiLog.debug(LOG_LABEL, "删除文件成功");
                    // 重新获取当前文件夹，并刷新页面
                    // [TODO] 重新获得数据后，刷新页面优化
                    getFolderLink();
                } else {
                    // 后台错误，或者 JSON字符串不包含 status
                    HiLog.warn(LOG_LABEL, "删除文件失败");
                }
            }
        });
    }

    /**
     * FolderChildren 克隆
     * @param children
     * @return
     */
    private FolderChildren cloneChildren(FolderChildren children){
        FolderChildren copy = new FolderChildren();
        for (SimpleFolderLink simpleFolderLink : children.getFolders()){
            copy.addFolder(new SimpleFolderLink(simpleFolderLink));
        }
        for (FileLink fileLink : children.getFiles()){
            copy.addFile(new FileLink(fileLink));
        }
        return copy;
    }

    /**
     * 开始分享文件
     * @param children
     */
    private void startShare(FolderChildren children){
        HiLog.debug(LOG_LABEL, "开始分享文件");
        // 文件夹
        children.getFolders().clear();
        // 文件
        for (FileLink fileLink : children.getFiles()){
            fileLink.setLinkId(null);
            fileLink.setCreateTime(null);
        }
        FileShareLink fileShareLink = new FileShareLink();
        fileShareLink.setChildren(children);
        HiLog.debug(LOG_LABEL, String.format("fileShareLink: %s", fileShareLink.toString()));
        // 分享文件
        shareFile(fileShareLink);
    }

    /**
     * 分享文件
     * @param fileShareLink
     */
    private void shareFile(FileShareLink fileShareLink){
        String fileShareLinkJson = JSON.toJSONString(fileShareLink);
        HiLog.debug(LOG_LABEL, String.format("fileShareLinkJson: %s", fileShareLinkJson));

        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, fileShareLinkJson);
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.post(SHARE_FILE, requestBody, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                JSONObject object = JSONObject.parseObject(responseStr);
                int status = object.getIntValue("status");
                if (status == 1) {
                    HiLog.debug(LOG_LABEL, "分享文件成功");
                } else {
                    // 后台错误，或者 JSON字符串不包含 status
                    HiLog.warn(LOG_LABEL, "分享文件失败");
                }
            }
        });
    }

    /**
     * 开始下载文件
     * @param children
     */
    private void startDownload(FolderChildren children){
        HiLog.debug(LOG_LABEL, "****开始下载本文件夹");
        // 下载子文件
        int test = 0;
        for (FileLink fileLink : children.getFiles()){
            HiLog.debug(LOG_LABEL, String.format("*********开始下载文件：%s", fileLink.toString()));
            if (test == 0){
                // 新开启一个线程，进行网络请求
                getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                        .asyncDispatch( () -> {
                            getFileUrl(fileLink);
                        });
            }
            test ++;
        }
        // 下载子文件夹
        for (SimpleFolderLink simpleFolderLink : children.getFolders()){
            HiLog.debug(LOG_LABEL, String.format("****开始下载文件夹：%s", simpleFolderLink.toString()));
        }
    }

    /**
     * 获取文件链接对应文件实体的真实地址
     * @param fileLink
     */
    private void getFileUrl(FileLink fileLink){
        String url = String.format(GET_FILE_URL, fileLink.getFileId());
        String accessToken = tokenDao.get().getAccessToken();
        Map<String, String> addHeaders = new HashMap<>();
        String Authorization = String.format("Bearer %s", accessToken);
        addHeaders.put("Authorization", Authorization);
        HttpUtil.get(url, null, addHeaders, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                HiLog.error(LOG_LABEL, String.format("下载文件失败：%s", fileLink.toString()));
                HiLog.error(LOG_LABEL, e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    // HiLog.debug(LOG_LABEL, String.format("responseStr: %s", responseStr));
                    if (responseStr == null || "".equals(responseStr.trim())){
                        HiLog.warn(LOG_LABEL, String.format("对应文件不存在：%s", fileLink.toString()));
                    } else {
                        String fileUrl = responseStr;
                        HiLog.debug(LOG_LABEL, String.format("对应文件地址：%s", fileUrl));
                        // 新开启一个线程，下载文件
                        getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                                .asyncDispatch( () -> {
                                    downloadFile(fileUrl, fileLink);
                                });
                    }
                } else {
                    HiLog.error(LOG_LABEL, String.format("下载文件失败：%s", fileLink.toString()));
                    HiLog.error(LOG_LABEL, responseStr);
                }
            }
        });
    }

    /**
     * 下载文件
     * @param fileUrl
     * @param fileLink
     */
    private void downloadFile(String fileUrl, FileLink fileLink){
        int index = fileUrl.indexOf('/');
        String groupName = fileUrl.substring(0, index);
        String remoteFileName = fileUrl.substring(index + 1);
        HiLog.debug(LOG_LABEL, String.format("group_name: %s, remote_file_name: %s", groupName, remoteFileName));
        // 构建文件路径
        String filePath = rootDir + java.io.File.separator + fileLink.getLinkName();
        HiLog.debug(LOG_LABEL, String.format("下载文件路径：%s", filePath));
        java.io.File fileDir = new java.io.File(rootDir);
        if (!fileDir.exists()){
            // 创建文件根目录
            fileDir.mkdirs();
        }
        java.io.File file = new java.io.File(filePath);
        if (file.exists()){
            // 文件已存在
            HiLog.debug(LOG_LABEL, String.format("本地已存在文件：%s", fileLink.getLinkName()));
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)){
            // 文件不存在，下载文件
            byte[] fileBytes = FastDFSUtil.download(groupName, remoteFileName);
            outputStream.write(fileBytes);
            HiLog.debug(LOG_LABEL, String.format("下载文件%s成功，文件大小：%d", fileLink.getLinkName(), fileBytes.length));
            // 本地添加下载记录
            insert_fileDownload(fileLink);
            // 本地查询下载记录
            query_fileDownload();

            // 本地文件读取测试
            test(fileLink.getLinkName());
        } catch (IOException ex){
            HiLog.error(LOG_LABEL, String.format("下载文件发生IOException异常：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (MyException ex){
            HiLog.error(LOG_LABEL, String.format("下载文件发生MyException异常：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private void test(String linkName){
        try{
            Uri uri = Uri.parse("dataability:///cn.qixqi.pan.data.FileDataAbility/download?" + linkName);
            FileDescriptor fd = helper.openFile(uri, "r");
            FileInputStream inputStream = new FileInputStream(fd);
            HiLog.debug(LOG_LABEL, String.format("---------本地文件读取测试，size: %d", inputStream.available()));
            inputStream.close();
        } catch (Exception e){
            e.printStackTrace();
            HiLog.error(LOG_LABEL, String.format("---------本地文件读取测试异常：%s", e.getMessage()));
        }
    }

    /**
     * 本地添加下载记录
     * @param fileLink
     */
    private void insert_fileDownload(FileLink fileLink){
        ValuesBucket values = new ValuesBucket();
        values.putString("linkId", fileLink.getLinkId());
        values.putString("linkName", fileLink.getLinkName());
        values.putString("fileId", fileLink.getFileId());
        values.putString("fileType", fileLink.getFileType());
        values.putLong("fileSize", fileLink.getFileSize());
        values.putLong("downloadFinishTime", new Date().getTime());
        // [TODO] downloadStatus 使用状态码代替字符串
        values.putString("downloadStatus", "下载完成");
        try {
            int result = helper.insert(Uri.parse(DOWNLOAD_BASE_URI + DOWNLOAD_DATA_PATH), values);
            if (result != -1){
                HiLog.info(LOG_LABEL, "本地添加下载记录，成功！");
            } else {
                HiLog.warn(LOG_LABEL, "本地添加下载记录，失败！");
            }
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地添加下载记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地添加下载记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    /**
     * 本地查询下载记录
     */
    private void query_fileDownload(){
        // 查询字段
        String[] columns = new String[] {
                "downloadId", "linkId", "linkName", "fileId", "fileType", "fileSize", "downloadFinishTime", "downloadStatus" };
        // 查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
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
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询下载记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询下载记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    /**
     * 本地添加上传记录
     * @param fileLink
     */
    private void insert_fileUpload(FileLink fileLink){
        ValuesBucket values = new ValuesBucket();
        // values.putInteger("uploadId", 0);
        values.putString("linkId", fileLink.getLinkId());
        values.putString("linkName", fileLink.getLinkName());
        values.putString("fileId", fileLink.getFileId());
        values.putString("fileType", fileLink.getFileType());
        values.putLong("fileSize", fileLink.getFileSize());
        values.putLong("uploadFinishTime", new Date().getTime());
        // [TODO] uploadStatus 使用状态码代替字符串
        values.putString("uploadStatus", "上传完成");
        HiLog.debug(LOG_LABEL, fileLink.toString());
        HiLog.debug(LOG_LABEL, values.toString());
        try {
            int result = helper.insert(Uri.parse(UPLOAD_BASE_URI + UPLOAD_DATA_PATH), values);
            if (result != -1){
                HiLog.info(LOG_LABEL, "本地添加上传记录，成功！");
            } else {
                HiLog.warn(LOG_LABEL, "本地添加上传记录，失败！");
            }
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地添加上传记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地添加上传记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    /**
     * 本地查询上传记录
     */
    private void query_fileUpload(){
        // 查询字段
        String[] columns = new String[] {
                "uploadId", "linkId", "linkName", "fileId", "fileType", "fileSize", "uploadFinishTime", "uploadStatus" };
        // 查询条件
        DataAbilityPredicates predicates = new DataAbilityPredicates();
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
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询上传记录发生DataAbilityRemoteException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        } catch (IllegalStateException ex){
            HiLog.error(LOG_LABEL, String.format("本地查询上传记录发生IllegalStateException异常，异常信息：%s", ex.getMessage()));
            ex.printStackTrace();
        }
    }

    @Override
    protected void onBackPressed(){
        if (isItemSelected) {
            // 当前子文件、子文件夹被选中
            // 全部子项取消选中
            childItemProvider.cancelSelected();
            childItemProvider.notifyDataChanged();
            setBottomToolBar();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 实现 ChildItemProvider 中的Callback接口，响应 childrenContainer 内部控件点击事件
     * @param component
     */
    @Override
    public void click(Component component){
        // Toast.makeToast(abilitySlice, "test", Toast.TOAST_SHORT).show();
        switch (component.getId()){
            case ResourceTable.Id_check_layout:
                int position = (Integer) component.getTag();
                // Toast.makeToast(abilitySlice, String.format("您当前点击：%d", position), Toast.TOAST_SHORT).show();
                if (childItemProvider.isFolder(position)){
                    // 选中子文件夹
                    FolderItemInfo folderItemInfo = (FolderItemInfo) childItemProvider.getItem(position);
                    folderItemInfo.setChecked(!folderItemInfo.isChecked());
                    selectedItemCount += folderItemInfo.isChecked() ? 1 : -1;
                } else {
                    // 选中子文件
                    FileItemInfo fileItemInfo = (FileItemInfo) childItemProvider.getItem(position);
                    fileItemInfo.setChecked(!fileItemInfo.isChecked());
                    selectedItemCount += fileItemInfo.isChecked() ? 1 : -1;
                }
                childItemProvider.notifyDataSetItemChanged(position);
                if (!isItemSelected){
                    setSelectedBottomToolBar();
                }
                if (selectedItemCount <= 0){
                    setBottomToolBar();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 应用请求权限
     */
    private void requestPermissions(){
        if (verifySelfPermission("ohos.permission.WRITE_USER_STORAGE") != IBundleManager.PERMISSION_GRANTED ||
                verifySelfPermission("ohos.permission.CAMERA") != IBundleManager.PERMISSION_GRANTED ||
                verifySelfPermission("ohos.permission.READ_MEDIA") != IBundleManager.PERMISSION_GRANTED ||
                verifySelfPermission("ohos.permission.READ_USER_STORAGE") != IBundleManager.PERMISSION_GRANTED) {

            HiLog.debug(LOG_LABEL, "没有权限，尝试申请权限");
            // 没有权限
            if (canRequestPermission("ohos.permission.READ_MEDIA")) {
                // ohos.permission.READ_MEDIA 允许弹框授权
                HiLog.debug(LOG_LABEL, "ohos.permission.READ_MEDIA 允许弹框授权");
                requestPermissionsFromUser(
                        new String[]{"ohos.permission.READ_MEDIA",
                                "ohos.permission.WRITE_MEDIA",
                                "ohos.permission.MEDIA_LOCATION",
                                "ohos.permission.CAMERA",
                                "ohos.permission.READ_USER_STORAGE",
                                "ohos.permission.WRITE_USER_STORAGE"
                        }, MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                // ohos.permission.READ_MEDIA 不允许弹框授权
                HiLog.warn(LOG_LABEL, "ohos.permission.READ_MEDIA 不允许弹框授权");
            }
        } else {
            // 有权限
            HiLog.debug(LOG_LABEL, "应用已获得权限");
        }
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
















