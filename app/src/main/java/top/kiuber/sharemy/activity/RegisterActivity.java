package top.kiuber.sharemy.activity;

/**
 * Created by Administrator on 2016/4/27.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import top.kiuber.sharemy.MainActivity;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.AppInformation;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.SharedUtils;


/**
 * @author Kiuber 二宝子
 *         <p/>
 *         最后编辑时间：2016-3-19 下午12:31:17
 *         <p/>
 *         Tel：18454837733,17853522796
 */
public class RegisterActivity extends AppCompatActivity implements OnClickListener {
    private EventHandler eventHandler;
    public String phString, dataString;
    private EditText phone, verification, password, password_again;
    private Button send_verification, sumbit_verification, set_password;
    private CountTimer countTimer;
    private TextView tv_register_dialog;
    private TextView tv_register_treaty;

    private void init() {
        SMSSDK.initSDK(this, "10009e339d827",
                "9afe6c2f5d0c284d8b97cbc85c021200");
        phone = (EditText) findViewById(R.id.et_register_phone);
        verification = (EditText) findViewById(R.id.et_register_verification);
        send_verification = (Button) findViewById(R.id.btn_register_send_verification);
        sumbit_verification = (Button) findViewById(R.id.btn_register_sumbit_verification);
        set_password = (Button) findViewById(R.id.btn_register_set_password);
        set_password.setOnClickListener(this);
        send_verification.setOnClickListener(this);
        sumbit_verification.setOnClickListener(this);
        password = (EditText) findViewById(R.id.et_register_password);
        countTimer = new CountTimer(60000, 1000);
        tv_register_dialog = (TextView) findViewById(R.id.tv_register_dialog);
        password_again = (EditText) findViewById(R.id.et_register_password_again);
        tv_register_treaty = (TextView) findViewById(R.id.tv_register_treaty);
        tv_register_treaty.setOnClickListener(this);
        tv_register_treaty.setText(Html.fromHtml("注册完成即代表您同意" + "<u>"
                + "ShareMy用户使用协议" + "</u>"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_send_verification:
                String check_phoneString = phone.getText().toString();
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(check_phoneString)) {
                    return;
                } else {
                    // 2. 通过sdk发送短信验证
                    cloudQuery(check_phoneString);
                }
                break;
            case R.id.btn_register_sumbit_verification:
                if (TextUtils.isEmpty(verification.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.submitVerificationCode("86", phone.getText().toString(),
                            verification.getText().toString());
                }
                break;
            case R.id.btn_register_set_password:
                if (TextUtils.equals(password.getText().toString(), "")) {
                    AppTools.myToast(getApplicationContext(), "请输入密码", 1);
                } else {
                    if (password.getText().toString().length() < 7) {
                        AppTools.myToast(getApplicationContext(), "请设置六位数以上密码", 1);
                    } else {
                        if (TextUtils.equals(password_again.getText().toString(),
                                password.getText().toString())) {
                            sumbitUserInformation();
                        } else {
                            AppTools.myToast(getApplicationContext(), "请输入相同的密码", 1);
                        }
                    }
                }
                break;
            case R.id.tv_register_treaty:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                // Uri content_url =
                // Uri.parse("content://com.android.htmlfileprovider/sdcard/help.html");
                Uri content_url = Uri.parse(SharedUtils.getSharePreference(
                        getApplicationContext(), "others", Context.MODE_PRIVATE,
                        "user_treaty"));
                intent.setData(content_url);
                intent.setClassName("com.android.browser",
                        "com.android.browser.BrowserActivity");
                startActivity(intent);
                // AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // builder.setTitle("ShareMy使用协议");
                // builder.setMessage("一、首先您使用、注册本App就代表您同意以下使用协议，当您在使用过程中违反了下列协议我们有权对您的账户做出一定行为，当您在使用过程中违反了国家法律或者泄露国家密码等对国家、社会或者全人类不利的事情时，我们有权把您移交司法机关。");
                // builder.create().show();
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            dataString = data.toString();
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    tv_register_dialog.setVisibility(View.VISIBLE);
                    tv_register_dialog.setText("我们已经验证码发送到\n+86"
                            + phone.getText().toString());
                    // Toast.makeText(getApplicationContext(), "验证码发送成功",
                    // Toast.LENGTH_SHORT).show();
                    phone.setVisibility(View.GONE);
                    verification.setVisibility(View.VISIBLE);
                    sumbit_verification.setVisibility(View.VISIBLE);
                    countTimer.start();
                }
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    verification.setVisibility(View.GONE);
                    send_verification.setVisibility(View.GONE);
                    sumbit_verification.setVisibility(View.GONE);

                    password.setVisibility(View.VISIBLE);
                    password_again.setVisibility(View.VISIBLE);
                    set_password.setVisibility(View.VISIBLE);

                    tv_register_dialog.setVisibility(View.GONE);
                }
            }
            if (result == SMSSDK.RESULT_ERROR) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(),
                            "验证码发送失败,请换一个手机号或者稍后重试", Toast.LENGTH_SHORT).show();
                }
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码错误，请检查验证码",
                            Toast.LENGTH_SHORT).show();
                    // AppToolss.myToast(getApplicationContext(), phone.getText()
                    // .toString(), 1);
                }
                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    Toast.makeText(getApplicationContext(), "请原谅，不支持本手机号注册",
                            Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    public void cloudQuery(final String check_phoneString) {
        boolean a = false;
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("user_phone", check_phoneString);
        query.findObjects(this, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> arg0) {
                if (arg0.size() == 0) {
                    SMSSDK.getVerificationCode("86", check_phoneString);
                } else {
                    AppTools.myToast(getApplicationContext(), "手机号码已经被注册", 1);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                AppTools.myToast(getApplicationContext(), arg1, 1);
            }
        });
    }

    // 每隔一分钟可点击一次验证码
    public class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    时间间隔是多长时间。
         * @param countDownInterval 回调OnTick方法，每隔多久执行一次。
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO 自动生成的构造函数存根
        }

        // 间隔时间结束时调用的方法
        @Override
        public void onFinish() {
            // 更新页面组件
            send_verification.setClickable(true);
            send_verification.setText("重新发送验证码");
        }

        // 间隔时间内执行的操作
        @Override
        public void onTick(long millisUntilFinished) {
            // 更新页面组件
            send_verification.setClickable(false);
            send_verification.setText(millisUntilFinished / 1000 + "秒后重发");
        }
    }

    /**
     * 监听返回键按下事件,方法2: 注意: 返回值表示:是否能完全处理该事件 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void sumbitUserInformation() {
        User user = new User();
        user.setUser_phone(phone.getText().toString());
        user.setUser_password(password.getText().toString());
        user.setUser_register_location(SharedUtils.getSharePreference(
                getApplicationContext(), "others", Context.MODE_PRIVATE,
                "user_location"));
        user.setUser_autograph("这家伙很懒，什么都没留下、");
        AppInformation appinformation = new AppInformation();
        appinformation.increment("user_num");
        appinformation.save(getApplicationContext(), new SaveListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(getApplicationContext(), arg1.toString(), 1);
            }
        });

        user.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                AppTools.myToast(getApplicationContext(),
                        "恭喜，注册成功\n昵称为随机产生，可以到“我”→“昵称”中修改", 1);
                startActivity(new Intent(getApplicationContext(),
                        MainActivity.class));
                // 保存ObjectId到本地
                BmobQuery<User> query = new BmobQuery<User>();
                query.addWhereEqualTo("user_phone", phone.getText().toString());
                query.findObjects(getApplicationContext(),
                        new FindListener<User>() {

                            @Override
                            public void onSuccess(List<User> arg0) {
                                for (User user : arg0) {
                                    // 更新用户名字
                                    user.setUser_name("新用户 "
                                            + user.getObjectId().toString());
                                    user.setUser_holder("头像默认");
                                    user.update(getApplicationContext(), user
                                                    .getObjectId().toString(),
                                            new UpdateListener() {

                                                @Override
                                                public void onSuccess() {
                                                }

                                                @Override
                                                public void onFailure(int arg0,
                                                                      String arg1) {
                                                }
                                            });
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(), "others",
                                            Context.MODE_PRIVATE, "ObjectId",
                                            user.getObjectId());
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(), "others",
                                            Context.MODE_PRIVATE,
                                            "login_status", "true");
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(),
                                            "user_information",
                                            Context.MODE_PRIVATE, "ObjectId",
                                            user.getObjectId());
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(),
                                            "user_information",
                                            Context.MODE_PRIVATE, "user_phone",
                                            phone.getText().toString());
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(),
                                            "user_information",
                                            Context.MODE_PRIVATE, "user_name",
                                            "新用户 "
                                                    + user.getObjectId()
                                                    .toString());
                                    // SharedUtils.saveSharePreference(
                                    // getApplicationContext(),
                                    // "user_information",
                                    // Context.MODE_PRIVATE,
                                    // "user_holder", "头像默认");
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(),
                                            "user_information",
                                            Context.MODE_PRIVATE,
                                            "user_autograph", user
                                                    .getUser_autograph()
                                                    .toString());
                                    SharedUtils.saveSharePreference(
                                            getApplicationContext(),
                                            "user_information",
                                            Context.MODE_PRIVATE,
                                            "user_password", user
                                                    .getUser_password()
                                                    .toString());
                                    finish();
                                }
                            }

                            @Override
                            public void onError(int arg0, String arg1) {
                            }
                        });
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(getApplicationContext(), "抱歉，注册失败", 1);
            }
        });
    }
}
