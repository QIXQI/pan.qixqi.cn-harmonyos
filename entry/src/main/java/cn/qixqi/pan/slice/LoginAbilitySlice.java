package cn.qixqi.pan.slice;

import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.util.ElementUtil;
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

public class LoginAbilitySlice extends AbilitySlice {

    private static final String VALID_MAIL = "123@163.com";
    private static final int LOGIN_SUCCESS = 1000;
    private static final int LOGIN_FAIL = 1001;

    private ScrollView loginScroll;
    private Text validAuthInfo;
    private Text validPassword;
    private TextField authInfoText;
    private TextField passwordText;
    private Button loginBtn;
    private Text registerText;
    private Text retrievePassText;
    private CommonDialog commonDialog;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_login);

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        initView();
        initListener();
    }

    /**
     * 初始化布局文件中的组件
     */
    private void initView(){
        loginScroll = (ScrollView) findComponentById(ResourceTable.Id_loginScroll);
        authInfoText = (TextField) findComponentById(ResourceTable.Id_authInfoText);
        passwordText = (TextField) findComponentById(ResourceTable.Id_passwordText);
        validAuthInfo = (Text) findComponentById(ResourceTable.Id_validAuthInfo);
        validPassword = (Text) findComponentById(ResourceTable.Id_validPassword);
        loginBtn = (Button) findComponentById(ResourceTable.Id_loginBtn);
        loginBtn.setEnabled(false);
        registerText = (Text) findComponentById(ResourceTable.Id_toRegister);
        retrievePassText = (Text) findComponentById(ResourceTable.Id_toRetrievePass);
    }

    /**
     * 初始化组件的监听器
     */
    private void initListener(){
        authInfoText.addTextObserver(
                // authInfoText 文本更新时触发
                (text, var, i1, i2) -> {
                    validAuthInfo.setVisibility(Component.HIDE);
                    validPassword.setVisibility(Component.HIDE);
                });
        // 使用自定义的 onPassTextUpdated 方法，充当监听器事件
        passwordText.addTextObserver(this::onPassTextUpdated);
        loginBtn.setClickedListener(component -> login(
                authInfoText.getText(), passwordText.getText()
        ));
        /* registerText.setClickedListener(component ->
                Toast.makeToast(LoginAbilitySlice.this,
                        getString(ResourceTable.String_clickedRegister),
                        Toast.TOAST_SHORT).show()); */
        registerText.setClickedListener(component -> present(new RegisterAbilitySlice(), new Intent()));
        /* retrievePassText.setClickedListener(component ->
                Toast.makeToast(LoginAbilitySlice.this,
                        getString(ResourceTable.String_clickedRetrievePass),
                        Toast.TOAST_SHORT).show()); */
        retrievePassText.setClickedListener(component -> present(new RetrievePassAbilitySlice(), new Intent()));
    }

    /**
     * 根据passwordText是否为空，改变loginBtn背景
     * 当passwordText改变时，隐藏validPassword组件和validAuthInfo组件
     * @param text: Text组件的text值，这里是passwordText
     * @param var
     * @param i1
     * @param i2
     */
    private void onPassTextUpdated(String text, int var, int i1, int i2){
        if (text != null && !text.isEmpty()){
            // 当 passwordText 内容不为空时
            loginBtn.setEnabled(true);
            loginBtn.setBackground(new ShapeElement(this, ResourceTable.Graphic_background_auth_btn_can));
        } else {
            // 当 passwordText 内容为空时
            loginBtn.setEnabled(false);
            loginBtn.setBackground(new ShapeElement(this, ResourceTable.Graphic_background_auth_btn));
        }
        validAuthInfo.setVisibility(Component.HIDE);
        validPassword.setVisibility(Component.HIDE);
    }

    /**
     * 检查 authInfo 格式
     * @param authInfo：可以是手机号、邮箱、用户名
     * @return
     */
    private boolean authInfoValid(String authInfo){
        if (authInfo != null && !authInfo.isEmpty()){
            // authInfo 不为空
            return true;
        }
        return false;
    }

    /**
     * 检查 password 格式
     * @param password
     * @return
     */
    private boolean passwordValid(String password){
        return password.length() >= 6;
    }

    /**
     * loginBtn 点击时触发
     * 首先，检查 authInfo 和 passowrd 格式
     * 其次，显示登录进度对话框
     * 接着，开一个线程模拟验证登录
     * 最后，返回登录成功或失败的事件
     * @param authInfo
     * @param password
     */
    private void login(final String authInfo, final String password){
        validAuthInfo.setVisibility(Component.HIDE);
        validPassword.setVisibility(Component.HIDE);
        if (!authInfoValid(authInfo)) {
            // authInfo 格式不正确
            validAuthInfo.setVisibility(Component.VISIBLE);
        } else if (!passwordValid(password)) {
            // password 格式不正确
            validPassword.setVisibility(Component.VISIBLE);
        } else {
            // 显示登录对话框
            showProgress(true);
            // 开一个线程模拟验证登录
            getGlobalTaskDispatcher(TaskPriority.DEFAULT)
                    .asyncDispatch(
                            () -> {
                               try {
                                   Thread.sleep(3000);
                               } catch (InterruptedException e) {
                                   Logger.getLogger(ElementUtil.class.getName()).log(Level.SEVERE, e.getMessage());
                               }

                               // 返回登录成功或失败的事件
                                if (authInfo.equals(VALID_MAIL)) {
                                    loginEventHandler.sendEvent(LOGIN_SUCCESS);
                                } else {
                                    loginEventHandler.sendEvent(LOGIN_FAIL);
                                }
                            });
        }
    }

    /**
     * 当点击 loginBtn 时，此对话框显示进度
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

    // 实例化 EventHandler，获取登录结果
    private final EventHandler loginEventHandler =
            new EventHandler(EventRunner.getMainEventRunner()) {
                @Override
                protected void processEvent(InnerEvent event){
                    super.processEvent(event);
                    // 关闭登录进度对话框
                    showProgress(false);
                    switch (event.eventId) {
                        case LOGIN_SUCCESS:
                            showLoginDialog(true);
                            break;
                        case LOGIN_FAIL:
                            showLoginDialog(false);
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
     * 显示登录成功与否的结果
     * 点击外部可以取消对话框
     * @param success
     */
    private void showLoginDialog(boolean success){
        // 初始化对话框
        CommonDialog loginDialog = new CommonDialog(this);
        // 从xml布局文件获取组件
        Component loginDialogComponent = LayoutScatter.getInstance(this)
                .parse(ResourceTable.Layout_auth_dialog, null, false);
        Text dialogText = (Text) loginDialogComponent.findComponentById(ResourceTable.Id_dialog_text);
        Text dialogSubText = (Text) loginDialogComponent.findComponentById(ResourceTable.Id_dialog_sub_text);

        if (success){
            dialogText.setText(ResourceTable.String_success);
            dialogSubText.setText(ResourceTable.String_loginSuccess);
        } else {
            dialogText.setText(ResourceTable.String_fail);
            dialogSubText.setText(ResourceTable.String_loginFail);
        }

        loginDialog
                .setContentCustomComponent(loginDialogComponent)
                .setTransparent(true)
                .setSize(AttrHelper.vp2px(300, this), DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                .setAutoClosable(true);
        loginDialog.show();
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
