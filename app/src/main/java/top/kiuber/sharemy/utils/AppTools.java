package top.kiuber.sharemy.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.SMSSDK;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.fragments.FragmentApk;
import top.kiuber.sharemy.fragments.FragmentMusic;
import top.kiuber.sharemy.fragments.FragmentOther;
import top.kiuber.sharemy.fragments.FragmentPic;
import top.kiuber.sharemy.fragments.FragmentTeach;
import top.kiuber.sharemy.fragments.FragmentVideo;
import top.kiuber.sharemy.fragments.FragmentZip;
import top.kiuber.sharemy.javabeans.User;

/**
 * Created by Administrator on 2016/4/27.
 */
public class AppTools {
    private static Context mContext;
    private TabLayout mTlIndex;
    private ViewPager mVpIndex;

    public AppTools(Context context) {
        this.mContext = context;
    }

    private void NetUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("网络提示信息");
        builder.setMessage("网络不可用，如果继续，请先设置网络！");
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    public static void myToast(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    public static boolean isFirstStart(Context context) {
        String status = SharedUtils.getSharePreference(context, "others", Context.MODE_PRIVATE, "isFirstStart");
        if (status == "true") {
            return true;
        }
        if (status == "") {
            return true;
        }
        if (status == null) {
            return true;
        }
        return false;
    }

    public static boolean oldVersionIsExist(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        String installedApp = "";
        for (int i = 0; i < packageInfos.size(); i++) {
            installedApp += packageInfos.get(i);
        }

        if (installedApp.contains(AppStaticText.OLD_VERSION_PACKAGE_NAME)) {
            return true;
        }
        return false;
    }

    // uninstall app
    public void uninstallAPK(String packageName) {
        // TODO Auto-generated method stub
        // 通过程序的报名创建URI
        Uri packageURI = Uri.parse("package:" + packageName);
        // 创建Intent意图
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(packageURI);
        //如果使用Context的startActivity方法的话，就需要开启一个新的task
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行卸载程序
        mContext.startActivity(intent);
    }

    //  user's login status
    public static boolean isLogin(Context context) {
        if (TextUtils.equals(SharedUtils.getSharePreference(
                context, "others", Context.MODE_PRIVATE,
                "login_status"), "true")) {
            return true;
        }
        return false;
    }

    //
    public static void exitLogin(final Context context) {
        if (AppTools.isLogin(context)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
            builder.setMessage("确定要退出账号吗？");
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedUtils.clearUserInformation(mContext);
                    SharedUtils
                            .saveSharePreference(mContext, "others",
                                    Context.MODE_PRIVATE, "login_status", "false");
                    Toast.makeText(context, "退出成功！", Toast.LENGTH_SHORT).show();

                }
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
        } else {
            Toast.makeText(context, "大兄弟，您还没登录呢！", Toast.LENGTH_SHORT).show();
        }
    }

    /****************
     * 发起添加群流程。群号：ShareMy(538181509) 的 key 为： cc84qhON5QFtjgPnOBtbFplqHmyPKEf7
     * 调用 joinQQGroup(cc84qhON5QFtjgPnOBtbFplqHmyPKEf7) 即可发起手Q客户端申请加群
     * ShareMy(538181509)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context context, String key) {
        AppTools.myToast(context, "正在唤醒QQ", Toast.LENGTH_SHORT);
        Intent intent = new Intent();
        intent.setData(Uri
                .parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"
                        + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public void tablayoutSet(Activity activity, FragmentManager fragmentManager) {
        mTlIndex = (TabLayout) activity.findViewById(R.id.tab_FindFragment_title);
        mVpIndex = (ViewPager) activity.findViewById(R.id.vp_FindFragment_pager);

        mVpIndex.setAdapter(new CustomAdapter(fragmentManager, mContext));
        mTlIndex.setupWithViewPager(mVpIndex);
        mTlIndex.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mVpIndex.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mVpIndex.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mVpIndex.setCurrentItem(tab.getPosition());
            }
        });
    }

    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments[] = {"音乐", "安装包", "图片", "视频", "压缩包", "教程", "其他"};

        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentMusic();
                case 1:
                    return new FragmentApk();
                case 2:
                    return new FragmentPic();
                case 3:
                    return new FragmentVideo();
                case 4:
                    return new FragmentZip();
                case 5:
                    return new FragmentTeach();
                case 6:
                    return new FragmentOther();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    public static boolean judgePhoneNums(Context context, String phoneNums) {
        if (AppTools.isMatchLength(phoneNums, 11) && AppTools.isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(context, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通） 阿里：170
		 * 小米：170 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3587]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
}
