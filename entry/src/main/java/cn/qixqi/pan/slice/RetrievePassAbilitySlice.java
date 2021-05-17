package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.Toast;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.CommonDialog;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrievePassAbilitySlice extends AbilitySlice {

    private static final String VALID_PHONE_NUM = "19818965587";

    private static final int RETRIEVE_PASS_SUCCESS = 1000;
    private static final int RETRIEVE_PASS_FAIL = 1001;

    private ScrollView retrievePassScroll;
    private Text validPhoneNum;
    private Text validAuthCode;
    private Text validPassword;
    private Text validPasswordConfirm;
    private TextField phoneNumText;
    private TextField authCodeText;
    private TextField passwordText;
    private TextField passwordConfirmText;
    private Button authCodeBtn;
    private Button retrievePassBtn;
    private Text loginText;
    private Text registerText;
    private CommonDialog commonDialog;

    @Override
    public void onStart(Intent intent){
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_retrieve_pass);

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        initView();
        initListener();
    }

    /**
     * 初始化布局文件中的组件
     */
    private void initView(){
        retrievePassScroll = (ScrollView) findComponentById(ResourceTable.Id_retrievePassScroll);
        validPhoneNum = (Text) findComponentById(ResourceTable.Id_validPhoneNum);
        validAuthCode = (Text) findComponentById(ResourceTable.Id_validAuthCode);
        validPassword = (Text) findComponentById(ResourceTable.Id_validPassword);
        validPasswordConfirm = (Text) findComponentById(ResourceTable.Id_validPasswordConfirm);
        phoneNumText = (TextField) findComponentById(ResourceTable.Id_phoneNumText);
        authCodeText = (TextField) findComponentById(ResourceTable.Id_authCodeText);
        passwordText = (TextField) findComponentById(ResourceTable.Id_passwordText);
        passwordConfirmText = (TextField) findComponentById(ResourceTable.Id_passwordConfirmText);
        authCodeBtn = (Button) findComponentById(ResourceTable.Id_authCodeBtn);
        retrievePassBtn = (Button) findComponentById(ResourceTable.Id_retrievePassBtn);
        loginText = (Text) findComponentById(ResourceTable.Id_toLogin);
        registerText = (Text) findComponentById(ResourceTable.Id_toRegister);
    }

    /**
     * 初始化组件的监听器
     */
    private void initListener(){
        // phoneNumText 焦点变化事件
        phoneNumText.setFocusChangedListener((component, isFocused) -> {
            if (isFocused) {
                // 获取焦点
                validPhoneNum.setVisibility(Component.HIDE);
            } else {
                // 失去焦点
                if (!phoneNumValid(phoneNumText.getText())){
                    validPhoneNum.setVisibility(Component.VISIBLE);
                }
            }
        });
        // authCodeText 焦点变化事件
        authCodeText.setFocusChangedListener((component, isFocused) -> {
            if (isFocused){
                // 获取焦点
                validAuthCode.setVisibility(Component.HIDE);
            } else {
                // 失去焦点
                if (!authCodeValid(authCodeText.getText())){
                    validAuthCode.setVisibility(Component.VISIBLE);
                }
            }
        });
        // passwordText 焦点变化事件
        passwordText.setFocusChangedListener((component, isFocused) -> {
            if (isFocused){
                // 获取焦点
                validPassword.setVisibility(Component.HIDE);
                validPasswordConfirm.setVisibility(Component.HIDE);
            } else {
                // 失去焦点
                if (!passwordValid(passwordText.getText())){
                    validPassword.setVisibility(Component.VISIBLE);
                }
                if (!passwordConfirmValid(passwordText.getText(), passwordConfirmText.getText())){
                    validPasswordConfirm.setVisibility(Component.VISIBLE);
                }
            }
        });
        // passwordConfirmText 焦点变化事件
        passwordConfirmText.setFocusChangedListener((component, isFocused) -> {
            if (isFocused){
                // 获取焦点
                validPasswordConfirm.setVisibility(Component.HIDE);
            } else {
                // 失去焦点
                if (!passwordConfirmValid(passwordText.getText(), passwordConfirmText.getText())){
                    validPasswordConfirm.setVisibility(Component.VISIBLE);
                }
            }
        });
        // authCodeBtn 点击事件
        authCodeBtn.setClickedListener(component -> {
            Toast.makeToast(RetrievePassAbilitySlice.this, getString(ResourceTable.String_clickedAuthCodeBtn),
                    Toast.TOAST_SHORT).show();
        });
        // loginText 点击事件
        loginText.setClickedListener(component -> {
            present(new LoginAbilitySlice(), new Intent());
        });
        // registerText 点击事件
        registerText.setClickedListener(component -> {
            present(new RegisterAbilitySlice(), new Intent());
        });
        // retrievePassBtn 点击事件
        retrievePassBtn.setClickedListener(component -> retrievePass(
                phoneNumText.getText(), authCodeText.getText(),
                passwordText.getText(), passwordConfirmText.getText()
        ));
    }

    /**
     * 检查 phoneNum 格式
     * @param phoneNum：11位
     * @return
     */
    private boolean phoneNumValid(String phoneNum){
        return phoneNum.matches("^((13[0-9])|(14[0|5|6|7|9])|(15[0-3])|(15[5-9])|(16[6|7])|(17[2|3|5|6|7|8])|(18[0-9])|(19[1|8|9]))\\d{8}$");
    }

    /**
     * 检查 authCode 格式
     * @param authCode：六位数字
     * @return
     */
    private boolean authCodeValid(String authCode){
        return authCode.matches("^[0-9]{6}$");
    }

    /**
     * 检查密码格式
     * @param password
     * @return
     */
    private boolean passwordValid(String password){
        return password.length() >= 6;
    }

    /**
     * 检查两次密码是否一致
     * @param password
     * @param passwordConfirm
     * @return
     */
    private boolean passwordConfirmValid(String password, String passwordConfirm){
        return password.equals(passwordConfirm);
    }

    /**
     * retrievePassBtn 点击时触发
     * 首先，检查phoneNum, authCode, password, passwordConfirm 格式
     * 其次，显示找回密码进度对话框
     * 接着，开一个线程模拟验证找回密码
     * 最后，返回找回密码成功或失败的事件
     * @param phoneNum
     * @param authCode
     * @param password
     * @param passwordConfirm
     */
    private void retrievePass(final String phoneNum, final String authCode,
                              final String password, final String passwordConfirm){
        if (!phoneNumValid(phoneNumText.getText())){
            validPhoneNum.setVisibility(Component.VISIBLE);
        } else if (!authCodeValid(authCodeText.getText())){
            validAuthCode.setVisibility(Component.VISIBLE);
        } else if (!passwordValid(passwordText.getText())){
            validPassword.setVisibility(Component.VISIBLE);
        } else if (!passwordConfirmValid(passwordText.getText(), passwordConfirmText.getText())){
            validPasswordConfirm.setVisibility(Component.VISIBLE);
        } else {
            // 显示找回密码进度对话框
            showProgress(true);
            // 开一个线程模拟验证找回密码
            getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                    .asyncDispatch(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e){
                            Logger.getLogger(ElementUtil.class.getName()).log(Level.SEVERE, e.getMessage());
                        }

                        // 返回找回密码成功或失败的事件
                        if (phoneNum.equals(VALID_PHONE_NUM)) {
                            retrievePassEventHandler.sendEvent(RETRIEVE_PASS_SUCCESS);
                        } else {
                            retrievePassEventHandler.sendEvent(RETRIEVE_PASS_FAIL);
                        }
                    });

        }
    }

    /**
     * 当点击 retrievePassBtn 时，此对话框显示进度
     * @param show
     */
    private void showProgress(final boolean show){
        // 当 commonDialog 为空时，实例化 commonDialog
        if (commonDialog == null){
            commonDialog = new CommonDialog(this);

            // 获取进度动画
            Component circleProgress = drawCircleProgress(AttrHelper.vp2px(6, this),
                    AttrHelper.vp2px(3, this));
            commonDialog
                    .setContentCustomComponent(circleProgress)
                    .setTransparent(true)
                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT);
        }

        // 显示或隐藏对话框
        if (show){
            commonDialog.show();
        } else {
            commonDialog.destroy();
            commonDialog = null;
        }
    }

    // 实例化 EventHandler，获取找回密码结果
    private final EventHandler retrievePassEventHandler =
            new EventHandler(EventRunner.getMainEventRunner()) {
                @Override
                protected void processEvent(InnerEvent event){
                    super.processEvent(event);
                    // 关闭找回密码进度对话框
                    showProgress(false);
                    switch (event.eventId) {
                        case RETRIEVE_PASS_SUCCESS:
                            showRetrievePassDialog(true);
                            break;
                        case RETRIEVE_PASS_FAIL:
                            showRetrievePassDialog(false);
                            break;
                        default:
                            break;
                    }
                }
            };

    // 表示当前圆形进度动画的旋转量: 0~12
    private int roateNum = 0;

    /**
     * 绘制圆形进度
     * @param maxRadius：动画圆的最大半径
     * @param minRadius：动画圆的最小半径
     * @return：动画组件
     */
    private Component drawCircleProgress(int maxRadius, int minRadius){
        final int circleNum = 12;

        // 初始化画笔
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_STYLE);
        paint.setColor(Color.WHITE);

        // 初始化组件
        Component circleProgress = new Component(this);
        circleProgress.setComponentSize(AttrHelper.vp2px(100, this), AttrHelper.vp2px(100, this));
        circleProgress.setBackground(new ShapeElement(this, ResourceTable.Graphic_auth_dialog_bg));

        // 绘制动画
        circleProgress.addDrawTask(
                (component, canvas) -> {
                    // 绘制一轮后重置
                    if (roateNum == circleNum) {
                        roateNum = 0;
                    }

                    // 旋转画布
                    canvas.rotate(30 * roateNum, (float) (component.getWidth() / 2), (float) (component.getHeight() / 2));
                    roateNum ++;
                    int radius = (Math.min(component.getWidth(), component.getHeight())) / 2 - maxRadius;
                    float radiusIncrement = (float) (maxRadius - minRadius) / circleNum;
                    double angle = 2 * Math.PI / circleNum;

                    // 绘制最小圆
                    for (int i = 0; i < circleNum; i++) {
                        float x = (float) (component.getWidth() / 2 + Math.cos(i * angle) * radius);
                        float y = (float) (component.getHeight() / 2 - Math.sin(i * angle) * radius);
                        paint.setAlpha((1 - (float) i / circleNum));
                        canvas.drawCircle(x, y, maxRadius - radiusIncrement * i, paint);
                    }

                    // 延迟刷新组件
                    getUITaskDispatcher()
                            .delayDispatch(
                                    circleProgress::invalidate,
                                    150);
                });
        return circleProgress;
    }

    /**
     * 显示找回密码成功与否的结果
     * 点击外部可以取消对话框
     * @param success
     */
    private void showRetrievePassDialog(boolean success){
        // 初始化对话框
        CommonDialog retrievePassDialog = new CommonDialog(this);
        // 从xml布局文件获取组件
        Component retrievePassDialogComponent = LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_auth_dialog, null, false);
        Text dialogText = (Text) retrievePassDialogComponent.findComponentById(ResourceTable.Id_dialog_text);
        Text dialogSubText = (Text) retrievePassDialogComponent.findComponentById(ResourceTable.Id_dialog_sub_text);

        if (success){
            dialogText.setText(ResourceTable.String_success);
            dialogSubText.setText(ResourceTable.String_retrievePassSuccess);
        } else {
            dialogText.setText(ResourceTable.String_fail);
            dialogSubText.setText(ResourceTable.String_retrievePassFail);
        }

        retrievePassDialog
                .setContentCustomComponent(retrievePassDialogComponent)
                .setTransparent(true)
                .setSize(AttrHelper.vp2px(300, this), DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                .setAutoClosable(true);
        retrievePassDialog.show();
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
