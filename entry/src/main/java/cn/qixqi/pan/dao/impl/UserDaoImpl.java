package cn.qixqi.pan.dao.impl;

import cn.qixqi.pan.dao.UserDao;
import cn.qixqi.pan.model.User;
import cn.qixqi.pan.model.UserBase;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLogLabel;

import java.util.Date;


public class UserDaoImpl implements UserDao {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, UserDaoImpl.class.getName());
    private DatabaseHelper helper;
    private Preferences preferences;
    private final String FILENAME = "user";

    public UserDaoImpl(Context context){
        this.helper = new DatabaseHelper(context);
        this.preferences = this.helper.getPreferences(FILENAME);
    }

    /**
     * 写入数据
     * flush() 立即修改内存中数据，但异步写入磁盘
     * @param user
     */
    @Override
    public void save(User user) {
        preferences.putString("uid", user.getUid());
        preferences.putString("phoneNumber", user.getPhoneNumber());
        preferences.putString("uname", user.getUname());
        preferences.putString("password", user.getPassword());
        preferences.putString("email", user.getEmail());
        preferences.putInt("priorityId", user.getPriorityId());
        preferences.putInt("statusId", user.getStatusId());
        preferences.putString("sex", String.valueOf(user.getSex()));
        preferences.putInt("diskCapacity", user.getDiskCapacity());
        preferences.putFloat("freeDiskCapacity", (float)user.getFreeDiskCapacity());
        // 解决 birthday 默认为空带来的bug
        if (user.getBirthday() == null){
            preferences.putLong("birthday", -1);
        } else {
            preferences.putLong("birthday", user.getBirthday().getTime());
        }
        preferences.putLong("joinTime", user.getJoinTime().getTime());
        preferences.putString("avatar", user.getAvatar());
        preferences.flush();
    }

    @Override
    public void delete() {
        helper.deletePreferences(FILENAME);
        preferences.clear();
    }

    @Override
    public User get() {
        String uid = preferences.getString("uid", null);
        String phoneNumber = preferences.getString("phoneNumber", null);
        String uname = preferences.getString("uname", null);
        String password = preferences.getString("password", null);
        String email = preferences.getString("email", null);
        Integer priorityId = preferences.getInt("priorityId", 0);
        Integer statusId = preferences.getInt("statusId", 0);
        char sex = preferences.getString("sex", "").charAt(0);
        Integer diskCapacity = preferences.getInt("diskCapacity", 0);
        double freeDiskCapacity = preferences.getFloat("freeDiskCapacity", 0);
        // 解析 birthday
        long birthdayTime = preferences.getLong("birthday", 0);
        Date birthday = null;
        if (birthdayTime != -1){
            birthday = new Date(birthdayTime);
        }
        Date joinTime = new Date(preferences.getLong("joinTime", 0));
        String avatar = preferences.getString("avatar", null);
        UserBase userBase = new UserBase()
                .withUid(uid)
                .withPhoneNumber(phoneNumber)
                .withUname(uname)
                .withPassword(password)
                .withEmail(email)
                .withPriorityId(priorityId)
                .withStatusId(statusId)
                .withSex(sex)
                .withDiskCapacity(diskCapacity)
                .withFreeDiskCapacity(freeDiskCapacity)
                .withBirthday(birthday)
                .withJoinTime(joinTime);
        return new User()
                .withUserBase(userBase)
                .withAvatar(avatar);
    }
}
