package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.model.User;
import cn.qixqi.pan.model.UserBase;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.Toast;
import com.alibaba.fastjson.JSON;
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
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.Call;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RegisterAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, RegisterAbilitySlice.class.getName());

    // private static final String VALID_PHONE_NUM = "19818965587";

    private static final int REGISTER_SUCCESS = 1000;
    private static final int REGISTER_FAIL = 1001;

    private static final String REGISTER_URL = "http://ali4.qixqi.cn:5555/api/user/v1/user";

    private ScrollView registerScroll;
    private Text validUname;
    private Text validPhoneNum;
    private Text validAuthCode;
    private Text validPassword;
    private Text validPasswordConfirm;
    private TextField unameText;
    private TextField phoneNumText;
    private TextField authCodeText;
    private TextField passwordText;
    private TextField passwordConfirmText;
    private Button authCodeBtn;
    private Button registerBtn;
    private Text loginText;
    private Text retrievePassText;
    private CommonDialog commonDialog;
    private CommonDialog registerDialog;

    @Override
    public void onStart(Intent intent){
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_register);

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        initView();
        initListener();
    }

    /**
     * 初始化布局文件中的组件
     */
    private void initView(){
        registerScroll = (ScrollView) findComponentById(ResourceTable.Id_registerScroll);
        validUname = (Text) findComponentById(ResourceTable.Id_validUname);
        validPhoneNum = (Text) findComponentById(ResourceTable.Id_validPhoneNum);
        validAuthCode = (Text) findComponentById(ResourceTable.Id_validAuthCode);
        validPassword = (Text) findComponentById(ResourceTable.Id_validPassword);
        validPasswordConfirm = (Text) findComponentById(ResourceTable.Id_validPasswordConfirm);
        unameText = (TextField) findComponentById(ResourceTable.Id_unameText);
        phoneNumText = (TextField) findComponentById(ResourceTable.Id_phoneNumText);
        authCodeText = (TextField) findComponentById(ResourceTable.Id_authCodeText);
        passwordText = (TextField) findComponentById(ResourceTable.Id_passwordText);
        passwordConfirmText = (TextField) findComponentById(ResourceTable.Id_passwordConfirmText);
        authCodeBtn = (Button) findComponentById(ResourceTable.Id_authCodeBtn);
        registerBtn = (Button) findComponentById(ResourceTable.Id_registerBtn);
        loginText = (Text) findComponentById(ResourceTable.Id_toLogin);
        retrievePassText = (Text) findComponentById(ResourceTable.Id_toRetrievePass);
    }

    /**
     * 初始化组件的监听器
     */
    private void initListener(){
        // unameText 焦点变化事件
        unameText.setFocusChangedListener((component, isFocused) -> {
            if (isFocused) {
                // 获取焦点
                validUname.setVisibility(Component.HIDE);
            } else {
                // 失去焦点
                if (!unameValid(unameText.getText())){
                    validUname.setVisibility(Component.VISIBLE);
                }
            }
        });
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
            Toast.makeToast(RegisterAbilitySlice.this, getString(ResourceTable.String_clickedAuthCodeBtn),
                    Toast.TOAST_SHORT).show();
        });

        // registerBtn 点击事件
        registerBtn.setClickedListener(component -> register(
                unameText.getText(), phoneNumText.getText(), authCodeText.getText(),
                passwordText.getText(), passwordConfirmText.getText()));

        // loginText 点击事件
        loginText.setClickedListener(component -> {
            present(new LoginAbilitySlice(), new Intent());
        });

        // retrievePassText 点击事件
        retrievePassText.setClickedListener(component -> {
            present(new RetrievePassAbilitySlice(), new Intent());
        });
    }


    /**
     * 检查 uname 格式
     * @param uname
     * @return
     */
    private boolean unameValid(String uname){
        if (uname != null && !uname.isEmpty()){
            return true;
        }
        return false;
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
     * registerBtn 点击时触发
     * 首先，检查uname, phoneNum, authCode, password, passwordConfirm 格式
     * 其次，显示注册进度对话框
     * 接着，开一个线程模拟验证注册
     * 最后，返回注册成功或失败的事件
     * @param uname
     * @param phoneNum
     * @param authCode
     * @param password
     * @param passwordConfirm
     */
    private void register(final String uname, final String phoneNum, final String authCode,
                          final String password, final String passwordConfirm){
        if (!unameValid(unameText.getText())){
            validUname.setVisibility(Component.VISIBLE);
        } else if (!phoneNumValid(phoneNumText.getText())){
            validPhoneNum.setVisibility(Component.VISIBLE);
        } else if (!authCodeValid(authCodeText.getText())){
            validAuthCode.setVisibility(Component.VISIBLE);
        } else if (!passwordValid(passwordText.getText())){
            validPassword.setVisibility(Component.VISIBLE);
        } else if (!passwordConfirmValid(passwordText.getText(), passwordConfirmText.getText())){
            validPasswordConfirm.setVisibility(Component.VISIBLE);
        } else {
            // 显示注册进度对话框
            showProgress(true);
            // 开一个线程模拟验证注册
            /* getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                    .asyncDispatch( () -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e){
                            Logger.getLogger(ElementUtil.class.getName()).log(Level.SEVERE, e.getMessage());
                        }

                        // 返回注册成功或失败的事件
                        if (phoneNum.equals(VALID_PHONE_NUM)){
                            registerEventHandler.sendEvent(REGISTER_SUCCESS);
                        } else {
                            registerEventHandler.sendEvent(REGISTER_FAIL);
                        }
                    }); */
            // 访问后端，完成注册
            UserBase userBase = new UserBase()
                    .withUname(uname)
                    .withPhoneNumber(phoneNum)
                    .withPassword(password);
            String userBaseJson = JSON.toJSONString(userBase);
            HiLog.debug(LOG_LABEL, userBaseJson);

            RequestBody requestBody = RequestBody.create(HttpUtil.JSON, userBaseJson);
            HttpUtil.post(REGISTER_URL, requestBody, null, null, new okhttp3.Callback(){
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e){
                    HiLog.error(LOG_LABEL, e.getMessage());
                    registerEventHandler.sendEvent(REGISTER_FAIL);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                    String responseStr = response.body().string();
                    if (response.isSuccessful()){
                        HiLog.debug(LOG_LABEL, responseStr);
                        User user = JSON.parseObject(responseStr, User.class);
                        HiLog.debug(LOG_LABEL, user.toString());
                        registerEventHandler.sendEvent(REGISTER_SUCCESS);
                    } else {
                        HiLog.error(LOG_LABEL, responseStr);
                        registerEventHandler.sendEvent(REGISTER_FAIL);
                    }
                }
            });
        }
    }

    /**
     * 当点击 registerBtn 时，此对话框显示进度
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

    // 实例化 EventHandler，获取注册结果
    private final EventHandler registerEventHandler =
            new EventHandler(EventRunner.getMainEventRunner()) {
                @Override
                protected void processEvent(InnerEvent event){
                    super.processEvent(event);
                    // 关闭注册进度对话框
                    showProgress(false);
                    switch (event.eventId) {
                        case REGISTER_SUCCESS:
                            showRegisterDialog(true);
                            getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                                    .delayDispatch( () -> {
                                        startLoginSlice();
                                    }, 2000);
                            break;
                        case REGISTER_FAIL:
                            showRegisterDialog(false);
                            break;
                        default:
                            break;
                    }
                }
            };

    /**
     * 注册成功后，跳转到登录页面
     */
    private void startLoginSlice(){
        // 页面跳转前，释放 registerDialog
        if (registerDialog != null){
            registerDialog.destroy();
            registerDialog = null;
        }

        Intent intent = new Intent();
        // 释放掉栈内所有的 Ability，不再返回先前页面
        // [TODO] 栈内Ability没有释放成功
        intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
        present(new LoginAbilitySlice(), intent);
    }

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
     * 显示注册成功与否的结果
     * 点击外部可以取消对话框
     * @param success
     */
    private void showRegisterDialog(boolean success){
        // 初始化对话框
        registerDialog = new CommonDialog(this);
        // 从xml布局文件获取组件
        Component registerDialogComponent = LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_auth_dialog, null, false);
        Text dialogText = (Text) registerDialogComponent.findComponentById(ResourceTable.Id_dialog_text);
        Text dialogSubText = (Text) registerDialogComponent.findComponentById(ResourceTable.Id_dialog_sub_text);

        if (success){
            dialogText.setText(ResourceTable.String_success);
            dialogSubText.setText(ResourceTable.String_registerSuccess);
        } else {
            dialogText.setText(ResourceTable.String_fail);
            dialogSubText.setText(ResourceTable.String_registerFail);
        }

        registerDialog
                .setContentCustomComponent(registerDialogComponent)
                .setTransparent(true)
                .setSize(AttrHelper.vp2px(300, this), DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                .setAutoClosable(true);
        registerDialog.show();
    }




    @Override
    public void onActive(){
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent){
        super.onForeground(intent);
    }
}
