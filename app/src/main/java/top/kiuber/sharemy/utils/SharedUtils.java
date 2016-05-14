package top.kiuber.sharemy.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SharedUtils {
    private static final String START_FILE_NAME = "file_welcome";
    private static final String START_MODE_NAME = "welcome";

    private static final String USER_INFORMATION_FILE_NAME = "user_information";

    // 判断是否是首次启动，获取boolean类型的值
    public static boolean getWelcomeBoolean(Context context) {
        return context.getSharedPreferences(START_FILE_NAME,
                Context.MODE_PRIVATE).getBoolean(START_MODE_NAME, false);
    }

    // 判断是否是首次启动，写入boolean类型的值
    public static void putWelcomeBoolean(Context context, Boolean isFirst) {
        SharedPreferences.Editor editor = context.getSharedPreferences(START_FILE_NAME,
                Context.MODE_APPEND).edit();
        editor.putBoolean(START_MODE_NAME, isFirst);
        editor.commit();
    }

    //判断登录状态，获取boolean类型值
    public static boolean getLoginStatusBoolean(Context context) {
        return context.getSharedPreferences(START_FILE_NAME,
                Context.MODE_PRIVATE).getBoolean("login_status", false);
    }

    //判断登录状态，写入boolean类型值
    public static void putLoginStatusBoolean(Context context, Boolean isFirst) {
        SharedPreferences.Editor editor = context.getSharedPreferences(START_FILE_NAME,
                Context.MODE_APPEND).edit();
        editor.putBoolean("login_status", isFirst);
        editor.commit();
    }

    /**
     * 把USER信息储存
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void putUserInformation(Context context, String key,
                                          String value) {
        context.getSharedPreferences(USER_INFORMATION_FILE_NAME,
                Context.MODE_PRIVATE).edit().putString(key, value)
                .commit();
    }

    /**
     * 获取USER信息
     *
     * @param context 上下文对象
     * @param key     键
     * @return
     */
    public static String getUserInformation(Context context, String key) {
        String returnString = context.getSharedPreferences(USER_INFORMATION_FILE_NAME,
                Context.MODE_PRIVATE).getString(key, "");
        return returnString;
    }

    //更改USER登录状态
    public static void changeLoginStatus(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(START_FILE_NAME,
                Context.MODE_APPEND).edit();
        editor.putBoolean("login_status", false);
        editor.commit();
    }

    //删除USER信息
    public static void clearUserInformation(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFORMATION_FILE_NAME,
                Context.MODE_APPEND).edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 封装存储SharePreference
     *
     * @param context  上下文对象
     * @param filename 文件名
     * @param mode     储存模式
     * @param key      键
     * @param value    值
     */
    public static void saveSharePreference(Context context, String filename, int mode, String key, String value) {
        context.getSharedPreferences(filename, mode).edit().putString(key, value).commit();
    }

    /**
     * 读取储存SharePreference
     *
     * @param context  上下文对象
     * @param filename 文件名
     * @param mode     储存模式
     * @param key      键
     * @return result 返回值
     */
    public static String getSharePreference(Context context, String filename, int mode, String key) {
        String result = context.getSharedPreferences(filename, mode).getString(key, "");
        return result;
    }
}
