package cn.qixqi.pan.view;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.datamodel.UserInfoItemInfo;
import cn.qixqi.pan.model.User;
import cn.qixqi.pan.util.PriorityUtil;
import cn.qixqi.pan.util.StatusUtil;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserInfoItemView {

    private List<UserInfoItemInfo> userInfoItemInfos = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
    private SimpleDateFormat timeFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    // [TODO] 奇怪，这里的context，使用 MyApplication.getAppContext().getString(ResourceTable.String_male) 等没有正常解析出字符串
    public UserInfoItemView(User user, Context context) {
        if (user == null){
            return;
        }
        // userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_uid, user.getUid(), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_phoneNumber, user.getPhoneNumber(), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_uname, user.getUname(), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_password, user.getPassword(), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_email, user.getEmail(), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_priorityId, PriorityUtil.priorities.get(user.getPriorityId()), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_statusId, StatusUtil.statuses.get(user.getStatusId()), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_sex, getSex(user.getSex(), context), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_diskCapacity, String.format("%dG", user.getDiskCapacity()), ResourceTable.Media_user_edit));
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_freeDiskCapacity, String.format("%.2fG", user.getFreeDiskCapacity()), ResourceTable.Media_user_edit));
        if (user.getBirthday() != null){
            userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_birthday, dateFormat.format(user.getBirthday()), ResourceTable.Media_user_edit));
        } else {
            userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_birthday, null, ResourceTable.Media_user_edit));
        }
        userInfoItemInfos.add(new UserInfoItemInfo(ResourceTable.String_user_joinTime, timeFormat.format(user.getJoinTime()), ResourceTable.Media_user_edit));
    }

    private String getSex(char sex, Context context){
        if (sex == 'm'){
            return context.getString(ResourceTable.String_male);
        } else if (sex == 'f'){
            return context.getString(ResourceTable.String_female);
        } else {
            return context.getString(ResourceTable.String_unknown);
        }
    }

    public List<UserInfoItemInfo> getUserInfoItemInfos(){
        return this.userInfoItemInfos;
    }
}
