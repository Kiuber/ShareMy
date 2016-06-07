package top.kiuber.sharemy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.SharedUtils;

/**
 * Created by Administrator on 2016/5/22.
 */
public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EventHandler eventHandler;
    public String phString, dataString;
    private TextInputLayout mTilPhoneNum, mTilCode, mTilNewPassword;
    private Button mBtnSendCode, mBtnCheckCode, mBtnSetNewPassword;
    private CountTimer countTimer;
    private TextView mTvTip;
    private String mStrPhoneNum;
    private String mStrCode, mStrNewPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        initView();
    }

    private void initView() {
        mBtnSendCode = (Button) findViewById(R.id.btn_find_passwprd_send_code);
        mBtnSendCode.setOnClickListener(this);
        mBtnCheckCode = (Button) findViewById(R.id.btn_find_password_check_code);
        mBtnCheckCode.setOnClickListener(this);
        mBtnSetNewPassword = (Button) findViewById(R.id.btn_find_password_set_new_password);
        mBtnSetNewPassword.setOnClickListener(this);
        mTilPhoneNum = (TextInputLayout) findViewById(R.id.til_find_password_phone_num);
        mTilCode = (TextInputLayout) findViewById(R.id.til_find_password_code);
        mTilNewPassword = (TextInputLayout) findViewById(R.id.til_find_password_new_password);

        mTvTip = (TextView) findViewById(R.id.tv_find_password_tip);

        countTimer = new CountTimer(60000, 1000);

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
            case R.id.btn_find_passwprd_send_code:
                mStrPhoneNum = mTilPhoneNum.getEditText().getText().toString();
                if (!AppTools.judgePhoneNums(FindPasswordActivity.this, mStrPhoneNum)) {
                    return;
                } else {
                    // 2. 通过sdk发送短信验证
                    cloudQuery(mStrPhoneNum);
                }
                break;
            case R.id.btn_find_password_check_code:
                mStrCode = mTilCode.getEditText().getText().toString();
                SMSSDK.submitVerificationCode("86", mStrPhoneNum, mStrCode);
                break;
            case R.id.btn_find_password_set_new_password:
                BmobQuery<User> userBmobQuery = new BmobQuery<User>();
                userBmobQuery.addWhereEqualTo("user_phone", mStrPhoneNum);
                userBmobQuery.findObjects(this, new FindListener<User>() {
                    @Override
                    public void onSuccess(List<User> list) {
                        for (User user : list) {
                            mStrNewPassword = mTilNewPassword.getEditText().getText().toString();
                            User user1 = new User();
                            user1.setUser_password(mStrNewPassword);
                            user1.update(FindPasswordActivity.this, user.getObjectId(), new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    finish();
                                    Toast.makeText(FindPasswordActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(FindPasswordActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(FindPasswordActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    public void cloudQuery(final String check_phoneString) {
        boolean a = false;
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("user_phone", check_phoneString);
        query.findObjects(this, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> arg0) {
                if (arg0.size() == 0) {
                    AppTools.myToast(getApplicationContext(), "该手机号码未注册", 1);
                } else {
                    SMSSDK.getVerificationCode("86", check_phoneString);
                }
            }

            @Override
            public void onError(int arg0, String arg1) {
                AppTools.myToast(getApplicationContext(), arg1, 1);
            }
        });
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            dataString = data.toString();
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    mTvTip.setVisibility(View.VISIBLE);
                    mTvTip.setText("我们已经验证码发送到\n+86"
                            + mStrPhoneNum);
                    countTimer.start();
                    mTilPhoneNum.setVisibility(View.GONE);
                    mTilCode.setVisibility(View.VISIBLE);
                    mBtnCheckCode.setVisibility(View.VISIBLE);
                }
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    mTilCode.setVisibility(View.GONE);
                    mBtnSendCode.setVisibility(View.GONE);
                    mTilNewPassword.setVisibility(View.VISIBLE);
                    mTvTip.setVisibility(View.GONE);
                    mBtnSetNewPassword.setVisibility(View.VISIBLE);
                    mBtnCheckCode.setVisibility(View.GONE);
                }
            }
            if (result == SMSSDK.RESULT_ERROR) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(),
                            "验证码发送失败,请换一个手机号或者稍后重试", Toast.LENGTH_SHORT).show();
                }
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    mTilCode.setError("“" + mStrCode + "”" + "验证码错误");
                }
                if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    Toast.makeText(getApplicationContext(), "请原谅，不支持本手机号注册",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    private class CountTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnSendCode.setClickable(false);
            mBtnSendCode.setText(millisUntilFinished / 1000 + "秒后重发");
        }

        @Override
        public void onFinish() {
            mBtnSendCode.setClickable(true);
            mBtnSendCode.setText("重新发送");
        }
    }
}
