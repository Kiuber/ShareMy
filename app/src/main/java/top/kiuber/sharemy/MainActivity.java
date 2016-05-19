package top.kiuber.sharemy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.SMSSDK;
import top.kiuber.sharemy.activity.LoginActivity;
import top.kiuber.sharemy.activity.ShareFileActivity;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppStaticText;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.SharedUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int GO_LOGIN = 0;
    private TabLayout mTlIndex;
    private ViewPager mVpIndex;
    private ImageView mIvLogin;

    private boolean mUserLoginStatus;
    private boolean mOldVersionAppisExist;

    @Override
    protected void onStart() {
        super.onStart();
        showUninstallOldAppDialog(MainActivity.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (AppTools.isFirstStart(MainActivity.this)) {
            showUpdateLogDialog();
            SharedUtils.saveSharePreference(getApplicationContext(), "others", MODE_PRIVATE, "isFirstStart", "false");
        }

        initConfig();

        AppTools appTools = new AppTools(MainActivity.this);
        appTools.tablayoutSet(MainActivity.this, getSupportFragmentManager());
    }

    private void showUninstallOldAppDialog(Context context) {
        mOldVersionAppisExist = AppTools.oldVersionIsExist(context);
        // user's login status
        if (mOldVersionAppisExist) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.uninstall_old_version_title));
            builder.setMessage(getString(R.string.uninstall_old_version_message));
            builder.setPositiveButton(getString(R.string.uninstall_old_version_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // go uninstall old version app
                    AppTools appTools = new AppTools(MainActivity.this);
                    appTools.uninstallAPK(AppStaticText.OLD_VERSION_PACKAGE_NAME);
                }
            });
            builder.setNegativeButton(getString(R.string.uninstall_old_version_negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
        }
    }

    private void initConfig() {
        // initialization MOB SMS SDK
        SMSSDK.initSDK(this, "1005fa08822a6",
                "2a59162cf7bd58d16ba4452b39596f38");
        // initialization BMOB Android SDK
        Bmob.initialize(this, "11a8ac07a1ed728a55a1a8243b1ffa36");

        (findViewById(R.id.fab_main_share)).setOnClickListener(this);

        mUserLoginStatus = AppTools.isLogin(MainActivity.this);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mUserLoginStatus) {
            TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_main_name);
            name.setText(SharedUtils.getSharePreference(MainActivity.this, "user_information", MODE_PRIVATE, "user_name"));
        }

        mIvLogin = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_main_go_login);
        mIvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserLoginStatus) {
                } else {
                    // User don't login,now start activity to login
                    goLogin();
                }
            }
        });
    }

    private void showUpdateLogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.update_log_title));
        builder.setMessage(getString(R.string.update_log_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.update_log_positive), null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_about:
                // User selects about button.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.about_title));
                builder.setMessage(getString(R.string.about_message));
                builder.setPositiveButton(getString(R.string.about_positive), null);
                builder.create().show();
                break;
            case R.id.action_join:
                // join QQ group
                AppTools.joinQQGroup(MainActivity.this, "cc84qhON5QFtjgPnOBtbFplqHmyPKEf7");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_name) {
            if (mUserLoginStatus) {
                showDialogName();
            } else {
                goLogin();
            }
        } else if (id == R.id.nav_password) {
            if (mUserLoginStatus) {
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View view = layoutInflater.inflate(R.layout.dialog_password, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                builder.create().show();

                final TextInputLayout EtOldPwd = (TextInputLayout) view.findViewById(R.id.til_dialog_password_old_pwd);
                final TextInputLayout EtNewPwd = (TextInputLayout) view.findViewById(R.id.til_dialog_password_new_pwd);
                TextInputLayout EtNewAgainPwd = (TextInputLayout) view.findViewById(R.id.til_dialog_password_new_again_pwd);
                Button BtnChange = (Button) view.findViewById(R.id.btn_dialog_password_change);
                Button BtnCancel = (Button) view.findViewById(R.id.btn_dialog_password_cancel);


                BtnChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String strOldPwd = EtOldPwd.getEditText().getText().toString();
                        String strNewPwd = EtOldPwd.getEditText().getText().toString();
                        String strNewAgainPwd = EtOldPwd.getEditText().getText().toString();

                        if (strOldPwd.equals("")) {
                            AppTools.myToast(MainActivity.this, "请输入旧密码", Toast.LENGTH_SHORT);
                        } else if (!strNewPwd.equals(strNewAgainPwd)) {
                            AppTools.myToast(MainActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT);
                        } else {
                            changePwd(strOldPwd, strNewPwd);
                        }
                    }
                });
            } else {
                goLogin();
            }

        } else if (id == R.id.nav_share) {
            AppTools.myToast(MainActivity.this, getString(R.string.share_toast_text), Toast.LENGTH_SHORT);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_extra_content));
            startActivity(intent.createChooser(intent, getString(R.string.share_choose_title)));
        } else if (id == R.id.nav_login_out) {
            AppTools.exitLogin();
            mUserLoginStatus = false;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePwd(final String EtOldPwd, final String EtNewPwd) {
        BmobQuery<User> userBmobQuery = new BmobQuery<>();
        userBmobQuery.addWhereEqualTo("ObjectId", SharedUtils.getUserInformation(MainActivity.this, "ObjectId"));
        userBmobQuery.findObjects(MainActivity.this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                for (User user : list) {
                    if (user.getUser_password().equals(EtOldPwd)) {
                        final User user1 = new User();
                        user1.setUser_password(EtNewPwd);
                        user1.update(MainActivity.this, SharedUtils.getUserInformation(MainActivity.this, "ObjectId"), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                AppTools.myToast(MainActivity.this, "密码更改成功", Toast.LENGTH_SHORT);
                                AppTools.myToast(MainActivity.this, user1.getObjectId(), Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(int i, String s) {

                            }
                        });
                    } else {
                        AppTools.myToast(MainActivity.this, "旧密码错误", Toast.LENGTH_SHORT);
                    }
                    AppTools.myToast(MainActivity.this, user.getUser_password(), Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onError(int i, String s) {
                AppTools.myToast(MainActivity.this, s, Toast.LENGTH_SHORT);
            }
        });

    }

    private void showDialogName() {
        LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = mInflater.inflate(R.layout.dialog_name, null);
        TextView textView = (TextView) v.findViewById(R.id.tv_dialog_name_old_name);
        textView.setText(SharedUtils.getSharePreference(MainActivity.this, "user_information", MODE_PRIVATE, "user_name"));
        builder.setView(v);
        final Dialog dialog = builder.show();

        final TextInputLayout mTilNewName = (TextInputLayout) v.findViewById(R.id.til_name_new_name);

        Button mBtnChange = (Button) v.findViewById(R.id.btn_dialog_name_change_name);
        mBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mStrNewName = mTilNewName.getEditText().getText().toString();
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("正在修改");
                progressDialog.setCancelable(false);
                progressDialog.show();
                User user = new User();
                user.setUser_name(mStrNewName);

                user.update(MainActivity.this, SharedUtils.getUserInformation(getApplicationContext(), "ObjectId"), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        progressDialog.dismiss();
                        SharedUtils.putUserInformation(getApplicationContext(), "user_name", mStrNewName);
                        AppTools.myToast(getApplicationContext(), "修改成功~", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        dialog.dismiss();
                        progressDialog.dismiss();
                        AppTools.myToast(getApplicationContext(), s, Toast.LENGTH_SHORT);
                    }
                });
            }
        });
        Button mBtnClose = (Button) v.findViewById(R.id.btn_dialog_name_cancel);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_main_share:
                goShare();
                break;
            default:
                break;
        }
    }

    public void goShare() {
        if (mUserLoginStatus) {
            startActivity(new Intent(
                    getApplicationContext(),
                    ShareFileActivity.class));
        } else {
            goLogin();
        }
    }


    public void goLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.is_login_message));
        builder.setNegativeButton(getString(R.string.is_login_negative), null);
        builder.setPositiveButton("登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), GO_LOGIN);
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_LOGIN) {
            if (resultCode == RESULT_OK) {
                mUserLoginStatus = true;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(getString(R.string.sign_out_title));
            builder.setPositiveButton(getString(R.string.sign_out_positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton(getString(R.string.sign_out_negative), null);
            builder.create().show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
