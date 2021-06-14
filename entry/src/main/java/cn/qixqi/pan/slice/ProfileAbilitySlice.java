package cn.qixqi.pan.slice;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.dao.TokenDao;
import cn.qixqi.pan.dao.UserDao;
import cn.qixqi.pan.dao.impl.TokenDaoImpl;
import cn.qixqi.pan.dao.impl.UserDaoImpl;
import cn.qixqi.pan.datamodel.BottomBarItemInfo;
import cn.qixqi.pan.datamodel.UserInfoItemInfo;
import cn.qixqi.pan.model.User;
import cn.qixqi.pan.util.ElementUtil;
import cn.qixqi.pan.util.Toast;
import cn.qixqi.pan.view.BottomBarItemView;
import cn.qixqi.pan.view.UserInfoItemView;
import cn.qixqi.pan.view.adapter.UserInfoItemProvider;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.List;
import java.util.stream.IntStream;

public class ProfileAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, ProfileAbilitySlice.class.getName());

    // ListContainer 弹性回滚效果参数
    private static final int OVER_SCROLL_PERCENT = 40;
    private static final float OVER_SCROLL_RATE = 0.6f;
    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private static final int LIST_ITEM_HEIGHT = 65;
    private static final int LIST_LEN = 12;
    // [TODO] 为什么 LIST_ITEM_TYPE 等于2的时候效果比较好
    private static final int LIST_ITEM_TYPE = 2;

    private TokenDao tokenDao;
    private UserDao userDao;
    private AbilitySlice abilitySlice;
    private User user;

    private Text uname;

    private ListContainer userInfoContainer;
    private UserInfoItemProvider userInfoItemProvider;

    private List<BottomBarItemInfo> bottomBarItemInfoList;
    private Component bottomBar;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_profile);

        abilitySlice = this;

        this.getWindow().setStatusBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));
        this.getWindow().setNavigationBarColor(ElementUtil.getColor(this, ResourceTable.Color_colorSubBackground));

        tokenDao = new TokenDaoImpl(MyApplication.getAppContext());
        userDao = new UserDaoImpl(MyApplication.getAppContext());
        user = userDao.get();
        // [TODO] 很神奇，该条日志不输出
        // HiLog.debug(LOG_LABEL, user.toString());

        initView();

        // 设置底部导航栏
        setBottomToolBar();
    }

    /**
     * 初始化控件和布局
     */
    private void initView(){
        // **********底部导航栏************
        bottomBar = (DirectionalLayout) findComponentById(ResourceTable.Id_bottom_bar);

        uname = (Text) findComponentById(ResourceTable.Id_uname);
        uname.setText(user.getUname());

        userInfoContainer = (ListContainer) findComponentById(ResourceTable.Id_user_info_container);
        userInfoContainer.setHeight(AttrHelper.vp2px(LIST_ITEM_HEIGHT, this) * LIST_LEN * LIST_ITEM_TYPE);
        setListContainer();
    }

    /**
     * 设置 ListContainer
     */
    private void setListContainer(){
        if (user == null){
            HiLog.error(LOG_LABEL, "user 为空");
            return;
        }
        UserInfoItemView userInfoItemView = new UserInfoItemView(user, abilitySlice);
        userInfoItemProvider = new UserInfoItemProvider(userInfoItemView.getUserInfoItemInfos(), abilitySlice);

        userInfoContainer.setItemProvider(userInfoItemProvider);

        // 设置 ListContainer 的事件监听器
        setListClickListener();

        // 设置文件分享列表回滚动画
        setListReboundAnimation();
    }

    /**
     * 设置 ListContainer 的事件监听器
     */
    private void setListClickListener(){
        // userInfoContainer 子项单击事件
        userInfoContainer.setItemClickedListener( (listContainer, component, position, id) -> {
            UserInfoItemInfo userInfoItemInfo = (UserInfoItemInfo) userInfoItemProvider.getItem(position);
            Toast.makeToast(abilitySlice, userInfoItemInfo.toString(), Toast.TOAST_LONG).show();
        });
    }

    /**
     * 设置文件列表回滚动画
     */
    private void setListReboundAnimation(){
        userInfoContainer.setReboundEffect(true);
        userInfoContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
    }

    /**
     * 设置底部导航栏
     */
    private void setBottomToolBar(){
        bottomBar.setVisibility(Component.VISIBLE);

        BottomBarItemView bottomBarItemView = new BottomBarItemView();
        bottomBarItemInfoList = bottomBarItemView.getBottomBarItemInfos();

        IntStream.range(0, bottomBarItemInfoList.size()).forEach(position -> {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavLayoutId());
            // bottomItemLayout.setVisibility(Component.VISIBLE);
            Image image = (Image) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavImgId());
            Text text = (Text) bottomItemLayout.findComponentById(
                    bottomBarItemInfoList.get(position).getBnavTextId());
            if (position == 2){
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
            Intent intent = new Intent();
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("cn.qixqi.pan")
                    .withAbilityName("cn.qixqi.pan.FileSystemAbility")
                    .build();
            intent.setOperation(operation);
            // 释放掉栈内所有的 Ability，不再返回先前页面
            intent.setFlags(Intent.FLAG_ABILITY_CLEAR_MISSION | Intent.FLAG_ABILITY_NEW_MISSION);
            startAbility(intent);
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
            return;
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

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
