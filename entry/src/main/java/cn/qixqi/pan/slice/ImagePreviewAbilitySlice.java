package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.window.service.WindowManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.ImageSource;
import ohos.media.image.PixelMap;
import ohos.utils.net.Uri;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImagePreviewAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, ImagePreviewAbilitySlice.class.getName());

    private AbilitySlice abilitySlice;
    private DataAbilityHelper helper;

    private String imageName;
    private Uri imageUri;

    private Image image;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        abilitySlice = this;
        helper = DataAbilityHelper.creator(this);

        // 隐藏状态栏
        this.getWindow().addFlags(WindowManager.LayoutConfig.MARK_ALLOW_EXTEND_LAYOUT);
        this.getWindow().addFlags(WindowManager.LayoutConfig.MARK_FULL_SCREEN);

        super.setUIContent(ResourceTable.Layout_ability_preview_image);

        // 解析 intent
        parseIntent(intent);
        // 初始化视图
        initView();
        // 显示图片
        showImage();
    }

    /**
     * 解析 intent
     * @param intent
     */
    private void parseIntent(Intent intent){
        if (intent != null){
            imageName = intent.getStringParam("imageName");
            String imageUriStr = intent.getStringParam("imageUriStr");
            HiLog.debug(LOG_LABEL, String.format("imageName: %s, imageUriStr: %s", imageName, imageUriStr));
            imageUri = Uri.parse(imageUriStr);
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        image = (Image) findComponentById(ResourceTable.Id_image);
    }

    /**
     * 显示图片
     */
    private void showImage(){
        HiLog.debug(LOG_LABEL, String.format("显示图片：%s", imageName));
        FileInputStream inputStream = null;
        ImageSource imageSource = null;
        try {
            FileDescriptor fd = helper.openFile(imageUri, "r");
            // 使用文件描述符封装的文件流，进行操作
            inputStream = new FileInputStream(fd);
            imageSource = ImageSource.create(inputStream, null);
            PixelMap pixelMap = imageSource.createPixelmap(null);
            image.setPixelMap(pixelMap);
        } catch (DataAbilityRemoteException ex){
            HiLog.error(LOG_LABEL, String.format("显示图片%s发生DataAbilityRemoteException异常：%s", imageName, ex.getMessage()));
            ex.printStackTrace();
        } catch (FileNotFoundException ex){
            HiLog.error(LOG_LABEL, String.format("显示图片%s发生FileNotFoundException异常：%s", imageName, ex.getMessage()));
            ex.printStackTrace();
        } catch (Exception ex){
            HiLog.error(LOG_LABEL, String.format("显示图片%s发生Exception异常：%s", imageName, ex.getMessage()));
            ex.printStackTrace();
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (imageSource != null){
                    imageSource.release();
                }
            } catch (Exception ex){
                HiLog.error(LOG_LABEL, String.format("关闭资源发生Exception异常：%s", ex.getMessage()));
                ex.printStackTrace();
            }
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











