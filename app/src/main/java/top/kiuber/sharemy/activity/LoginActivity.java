package top.kiuber.sharemy.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.SharedUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private TextInputLayout mTilTel, mTilPwd;
    private EditText mEtTel, mEtPwd;
    private Button mBtnLogin, mBtnRegister;
    private String mStrTel, mStrPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mTilTel = (TextInputLayout) findViewById(R.id.tel_wrapper);
        mTilPwd = (TextInputLayout) findViewById(R.id.pwd_wrapper);

        mEtTel = (EditText) findViewById(R.id.tel);
        mEtPwd = (EditText) findViewById(R.id.pwd);


        mBtnLogin = (Button) findViewById(R.id.btn_login_login);
        mBtnRegister = (Button) findViewById(R.id.btn_login_register);

        mBtnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStrTel = mEtTel.getText().toString();
                mStrPwd = mEtPwd.getText().toString();

                mTilTel.setErrorEnabled(false);
                mTilPwd.setErrorEnabled(false);

                if (mStrTel.isEmpty()) {
                    mTilTel.setErrorEnabled(true);
                    mTilTel.setError("请输入手机号");
                    mEtTel.setFocusable(true);
                } else if (mStrPwd.isEmpty()) {
                    mTilPwd.setErrorEnabled(false);
                    mTilPwd.setError("请输入密码");
                    mEtPwd.setFocusable(true);
                } else {
                    isRegister(mStrTel);
                }
            }
        });
    }

    /**
     * 判断手机号是否注册
     */
    public void isRegister(final String tel) {
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("user_phone", tel);
        query.findObjects(getApplicationContext(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                if (TextUtils.equals(list.size() + "", "0")) {
                    mTilTel.setErrorEnabled(true);
                    mTilTel.setError("账号未注册");
                } else {
                    login(list);
                }
            }

            @Override
            public void onError(int i, String s) {
                AppTools.myToast(LoginActivity.this, s, Toast.LENGTH_SHORT);
            }
        });
    }

    public void login(List<User> list) {
        for (User user : list) {
            if (TextUtils.equals(mStrPwd, user.getUser_password())) {
                AppTools.myToast(getApplicationContext(), "登陆成功", 0);

                SharedUtils.saveSharePreference(
                        getApplicationContext(), "others",
                        Context.MODE_PRIVATE, "ObjectId",
                        user.getObjectId());
                SharedUtils.saveSharePreference(
                        getApplicationContext(), "others",
                        Context.MODE_PRIVATE, "login_status",
                        "true");
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "ObjectId", user.getObjectId());
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "user_phone", mStrTel);
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "user_name", user.getUser_name());
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "user_autograph", user.getUser_autograph()
                                .toString());
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "user_autograph", user.getUser_autograph()
                                .toString());
                SharedUtils.saveSharePreference(
                        getApplicationContext(),
                        "user_information", Context.MODE_PRIVATE,
                        "user_password", user.getUser_password()
                                .toString());
                // if (TextUtils.equals(user.getUser_holder(),
                // "头像默认")) {
                // SharedUtils.saveSharePreference(
                // getApplicationContext(),
                // "user_information",
                // Context.MODE_PRIVATE, "user_holder",
                // "头像默认");
                // } else {
                //
                // }

                Intent intent = new Intent();
                intent.putExtra("home_login_status_tv", "我的");
                setResult(1, intent);
                finish();
            } else {
                mTilPwd.setErrorEnabled(true);
                mTilPwd.setError("密码错误");
            }
        }
    }
}

