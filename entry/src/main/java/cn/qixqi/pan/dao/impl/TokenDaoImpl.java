package cn.qixqi.pan.dao.impl;

import cn.qixqi.pan.dao.TokenDao;
import cn.qixqi.pan.model.Token;
import ohos.app.Context;
import ohos.data.DatabaseHelper;
import ohos.data.preferences.Preferences;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class TokenDaoImpl implements TokenDao {

    private static final HiLogLabel LOG_LABEL = new HiLogLabel(3, 0xD001100, TokenDaoImpl.class.getName());
    private DatabaseHelper helper;
    private Preferences preferences;
    private final String FILENAME = "token";

    public TokenDaoImpl(Context context) {
        this.helper = new DatabaseHelper(context);
        this.preferences = this.helper.getPreferences(FILENAME);
    }

    @Override
    public void save(Token token) {
        preferences.putString("accessToken", token.getAccessToken());
        preferences.putString("tokenType", token.getTokenType());
        preferences.putString("refreshToken", token.getRefreshToken());
        preferences.putLong("expiresIn", token.getExpiresIn());
        preferences.putString("scope", token.getScope());
        preferences.putString("uid", token.getUid());
        preferences.putString("jti", token.getJti());
        preferences.flush();
    }

    @Override
    public void delete() {
        helper.deletePreferences(FILENAME);
        preferences.clear();
    }

    @Override
    public Token get() {
        String accessToken = preferences.getString("accessToken", null);
        String tokenType = preferences.getString("tokenType", null);
        String refreshToken = preferences.getString("refreshToken", null);
        long expiresIn = preferences.getLong("expiresIn", 0);
        String scope= preferences.getString("scope", null);
        String uid = preferences.getString("uid", null);
        String jti = preferences.getString("jti", null);
        return new Token()
                .withAccessToken(accessToken)
                .withTokenType(tokenType)
                .withRefreshToken(refreshToken)
                .withExpiresIn(expiresIn)
                .withScope(scope)
                .withUid(uid)
                .withJti(jti);
    }

    @Override
    public boolean exist(){
        HiLog.debug(LOG_LABEL, get().toString());
        String accessToken = preferences.getString("accessToken", null);
        return accessToken == null ? false : true;
    }
}
