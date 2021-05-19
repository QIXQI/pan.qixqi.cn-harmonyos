package cn.qixqi.pan.slice;

import cn.qixqi.pan.MyApplication;
import cn.qixqi.pan.ResourceTable;
import cn.qixqi.pan.dao.TokenDao;
import cn.qixqi.pan.dao.UserDao;
import cn.qixqi.pan.dao.impl.TokenDaoImpl;
import cn.qixqi.pan.dao.impl.UserDaoImpl;
import cn.qixqi.pan.model.User;
import cn.qixqi.pan.util.HttpUtil;
import cn.qixqi.pan.util.Toast;
import com.alibaba.fastjson.JSON;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Text;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

public class MainAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, MainAbilitySlice.class.getName());
    private UserDao userDao;
    private TokenDao tokenDao;
    private Text text;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        text = (Text) findComponentById(ResourceTable.Id_text_helloworld);
        userDao = new UserDaoImpl(MyApplication.getAppContext());
        // testOkHttp3();

        tokenDao = new TokenDaoImpl(MyApplication.getAppContext());
        if (tokenDao.exist()){
            HiLog.debug(LOG_LABEL, "令牌存在，跳转到文件系统页面");
            startFileSystemAbility();
        } else {
            // 不存在令牌，跳转到登录页面
            HiLog.debug(LOG_LABEL, "不存在令牌，跳转到登录页面");
            startAuthAbility();
        }
    }

    private void startAuthAbility(){
        Intent intent = new Intent();
        // 构造 Operation 对象
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("cn.qixqi.pan")
                .withAbilityName("cn.qixqi.pan.AuthAbility")
                .build();
        // intent 设置 Operation
        intent.setOperation(operation);
        startAbility(intent);
    }

    private void startFileSystemAbility(){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("cn.qixqi.pan")
                .withAbilityName("cn.qixqi.pan.FileSystemAbility")
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }

    // java.lang.IllegalStateException: Attempt to update UI in non-UI thread
    private void testOkHttp3(){
        String url = "http://ali4.qixqi.cn:5555/api/user/v1/user/test";
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e){
                showResponse("fail: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException{
                String responseStr = response.body().string();
                if (response.isSuccessful()){
                    User user = JSON.parseObject(responseStr, User.class);
                    user
                            .withBirthday(new Date())
                            .withJoinTime(new Date());
                    HiLog.info(LOG_LABEL, responseStr);
                    HiLog.info(LOG_LABEL, user.toString());
                    userDao.save(user);
                    User user2 = userDao.get();
                    HiLog.debug(LOG_LABEL, user2.toString());
                    userDao.delete();
                    // User user3 = userDao.get();
                    // HiLog.debug(LOG_LABEL, user3.toString());
                }
                showResponse(responseStr);
            }
        });
    }

    // 处理网络请求
    private void showResponse(final String response){
        TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
        uiTaskDispatcher.asyncDispatch(() ->
                text.setText(response));
    }

    // android.os.NetworkOnMainThreadException
    /* private void testHttpURLConnection(){
        try {
            URL url = new URL("http://ali4.qixqi.cn:5555/api/user/v1/user/test");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5*1000);
            connection.setRequestProperty("Content-type","application/x-javascript->json");
            connection.addRequestProperty("Charset","UTF-8");
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK){
                Toast.makeToast(this, "OK", Toast.TOAST_SHORT).show();
                InputStream inputStream = connection.getInputStream();
                byte[] data = new byte[1024];
                StringBuffer sb = new StringBuffer();
                int length = 0;
                while ((length = inputStream.read(data)) != -1){
                    String s = new String(data, Charset.forName("utf-8"));
                    sb.append(s);
                }
                text.setText(sb.toString());
                inputStream.close();
            }
            connection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
            text.setText(e.getMessage());
            Toast.makeToast(this, "Error", Toast.TOAST_SHORT).show();
        }
    }*/

    /* private void testNetwork(){
        String url = "http://ali4.qixqi.cn:5555/api/user/v1/user/test";
        // String url = "http://qixqi.cn";
        ZZRHttp.get(url, new ZZRCallBack.CallBackString() {
            @Override
            public void onFailure(int i, String s) {
                text.setText(String.format("i=%s, s=%s", i, s));
            }

            @Override
            public void onResponse(String s) {
                text.setText(s);
            }
        });
    }*/

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
