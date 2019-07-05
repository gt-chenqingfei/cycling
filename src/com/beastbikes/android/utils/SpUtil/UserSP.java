package com.beastbikes.android.utils.SpUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.beastbikes.android.authentication.AVUser;

/**
 * Created by chenqingfei on 16/8/16.
 */
public class UserSP {
    private UserSP() {
    }

    private static UserSP instance = new UserSP();

    public static UserSP getInstance() {
        return instance;
    }

    /**
     * 存储用户的配置信息
     */
    private SharedPreferences userSp = null;

    /**
     * 获取 userSp的实例对象
     *
     * @return
     */
    private SharedPreferences getUserSp(Context context) {
        if (userSp == null) {
            AVUser user = AVUser.getCurrentUser();
            if (user == null) {
                throw new RuntimeException("User is null, cannot be instantiated");
            }
            userSp = context.getSharedPreferences(user.getObjectId(), Context.MODE_PRIVATE);
        }
        return userSp;
    }

    /**
     * 注册 UserSP OnSharedPreferenceChangeListener
     *
     * @param context
     * @param listener
     */
    public void registerListener(Context context,
                                 SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = getUserSp(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * 获取 Editor
     *
     * @param context
     * @return
     */
    public SharedPreferences.Editor getEditor(Context context) {
        userSp = getUserSp(context);
        return userSp.edit();
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param context
     * @param key
     * @param object
     */
    public SharedPreferences.Editor put(Context context, String key, Object object) {

        userSp = getUserSp(context);
        SharedPreferences.Editor editor = userSp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        return editor;
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = getUserSp(context);

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    public int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = getUserSp(context);
        return sp.getInt(key, defaultValue);
    }

    public String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = getUserSp(context);
        return sp.getString(key, defaultValue);
    }

    public boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = getUserSp(context);
        return sp.getBoolean(key, defaultValue);
    }

    public float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = getUserSp(context);
        return sp.getFloat(key, defaultValue);
    }

    public long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = getUserSp(context);
        return sp.getLong(key, defaultValue);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context
     * @param key
     */
    public SharedPreferences.Editor remove(Context context, String key) {
        SharedPreferences sp = getUserSp(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        return editor;
    }

    /**
     * 清除所有数据
     *
     * @param context
     */
    public SharedPreferences.Editor clear(Context context) {
        SharedPreferences sp = getUserSp(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        return editor;
    }


}
