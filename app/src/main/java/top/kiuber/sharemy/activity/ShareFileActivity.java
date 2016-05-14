package top.kiuber.sharemy.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.ShareFiles;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.FileUtils;
import top.kiuber.sharemy.utils.SharedUtils;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ShareFileActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener {
    private Intent intent;
    private EditText et_share_file_path, et_share_file_title,
            et_share_file_content;
    private TextView tv_share_by;
    private static final int FILE_SELECT_CODE = 0x111;

    private ImageView mIvShareFileType;
    String[] music = {"mp3", "m4a", "aac", "wav", "au", "aif", "ram",
            "wma", "mmf", "amr", "flac"};

    String[] video = {"mp4", "rmvb", "avi", "mpg", "mov", "swf", ""};

    String[] doc = {"txt", "doc", "docx", "rtf", "xls", "ppt", "htm",
            "html", "wpd", "pdf", "hlp"};

    String[] apk = {"apk"};

    String[] zip = {"zip", "rar", "arj", "gz", "z"};

    String[] pic = {"bmp", "gif", "jpg", "pic", "png", "tif"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_file_index);
        findViewById(R.id.btn_share_file_share).setOnClickListener(this);
        showFileChooser();
        et_share_file_path = (EditText) findViewById(R.id.et_share_file_path);
        et_share_file_path.setOnClickListener(this);
        et_share_file_title = (EditText) findViewById(R.id.et_share_file_title);
        findViewById(R.id.btn_share_file_choose).setOnClickListener(this);
        et_share_file_content = (EditText) findViewById(R.id.et_share_file_content);
        tv_share_by = (TextView) findViewById(R.id.tv_share_by);
        tv_share_by.setText("由 "
                + SharedUtils.getSharePreference(getApplicationContext(),
                "user_information", Context.MODE_PRIVATE, "user_name")
                + " 分享");
        mIvShareFileType = (ImageView) findViewById(R.id.iv_sharefile_type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_file_share:
                if (TextUtils.equals(et_share_file_path.getText().toString(), "")) {
                    AppTools.myToast(getApplicationContext(), "请选择文件或者手动填写路径~", 1);
                } else {
                    if (TextUtils.equals(et_share_file_title.getText().toString(),
                            "")) {
                        AppTools.myToast(getApplicationContext(), "请填写标题~", 1);
                    } else {
                        shareStart();
                    }
                }
                break;
            case R.id.btn_share_file_choose:
                showFileChooser();
                break;
            default:
                break;
        }
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser() {
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            AppTools.myToast(getApplicationContext(), "请安装文件管理器", 1);
        }
    }

    /**
     * 根据返回选择的文件，来进行操作
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    final String path = FileUtils.getPath(this, uri);

                    et_share_file_path.setText(path);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("是否把文件名当作标题？");
                    builder.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    et_share_file_title.setText(getFileName(path));
                                    mIvShareFileType.setVisibility(View.VISIBLE);
                                    isShareFileType(mIvShareFileType, calShareFileSuffix(et_share_file_path.getText().toString()));
                                }
                            });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            et_share_file_title.setText(getFileName(path));
                            isShareFileType(mIvShareFileType, calShareFileSuffix(et_share_file_path.getText().toString()));
                        }
                    });
                    builder.create().show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void shareStart() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传文件\n点击空白处或者返回键可以后台上传");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        final BmobFile bmobFile = new BmobFile(new File(et_share_file_path
                .getText().toString()));
        bmobFile.uploadblock(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                saveFileInformation(
                        bmobFile.getFileUrl(getApplicationContext()),
                        bmobFile.getFilename(), progressDialog,
                        et_share_file_path.getText().toString());
            }

            @Override
            public void onProgress(Integer arg0) {
                progressDialog.setProgress(arg0);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                progressDialog.dismiss();
                if (arg1.contains("suffix")) {
                    AppTools.myToast(getApplicationContext(), "请选择带有后缀名的文件", 1);
                } else {
                    AppTools.myToast(getApplicationContext(), arg1, 1);
                }
            }
        });
    }

    private String calShareFileSuffix(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if ((dot > -1) && (dot < (fileName.length() - 1))) {
        }
        String sharefilesuffix = fileName.substring(dot + 1);
        return sharefilesuffix;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.et_share_file_path:
                showFileChooser();
                break;
            default:
                break;
        }
        return false;
    }

    public void saveFileInformation(String fileUrl, String fileName,
                                    final ProgressDialog progressialog, String path) {
        final ShareFiles shareFiles = new ShareFiles();
        shareFiles.setUser_object_id(SharedUtils.getSharePreference(
                getApplicationContext(), "user_information",
                Context.MODE_PRIVATE, "ObjectId"));
        shareFiles.setUser_by(SharedUtils.getSharePreference(
                getApplicationContext(), "user_information",
                Context.MODE_PRIVATE, "user_name"));
        shareFiles.setUser_upload_file(fileUrl);
        shareFiles.setShare_title(et_share_file_title.getText().toString());
        shareFiles.setShare_content(et_share_file_content.getText().toString());
        shareFiles.setShare_name(fileName);
        shareFiles.setShare_user_location(SharedUtils.getSharePreference(
                getApplicationContext(), "others", Context.MODE_PRIVATE,
                "user_location"));
        shareFiles.setShare_file_size(FormetFileSize(new File(path)));
        shareFiles.setShare_file_download_num("0");

        isShareFileType(shareFiles, calShareFileSuffix(et_share_file_path.getText().toString()));
        shareFiles.save(getApplicationContext(), new SaveListener() {

            @Override
            public void onSuccess() {
                AppTools.myToast(getApplicationContext(), "上传成功", 1);
                progressialog.dismiss();
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(getApplicationContext(),
                        "文件信息保存失败，但是文件已经上传至服务器，可能会在下个版本开放秒传，错误码：" + arg1
                                + "\n请把错误码告诉开发者", 1);
            }
        });
    }

    public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return pathandname.substring(start + 1);
        }

    }

    public String FormetFileSize(File file) {

        try {
            int fis = new FileInputStream(file).available();

            DecimalFormat df = new DecimalFormat("#.00");
            String fileSizeString = "";
            long fileS = Long.parseLong(fis + "");
            if (fileS < 1024) {
                fileSizeString = df.format((double) fileS) + "B";
            } else if (fileS < 1048576) {
                fileSizeString = df.format((double) fileS / 1024) + "K";
            } else if (fileS < 1073741824) {
                fileSizeString = df.format((double) fileS / 1048576) + "M";
            } else {
                fileSizeString = df.format((double) fileS / 1073741824) + "G";
            }
            return fileSizeString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void isShareFileType(ImageView imageView, String sharefilesuffix) {
        if (Arrays.asList(music).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_music_phone_normal);
        } else if (Arrays.asList(video).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_video_phone_normal);
        } else if (Arrays.asList(doc).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_doc_phone_normal);
        } else if (Arrays.asList(apk).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_apk_phone_normal);
        } else if (Arrays.asList(zip).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_zip_phone_normal);
        } else if (Arrays.asList(pic).contains(sharefilesuffix)) {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_pic_phone_normal);
        } else {
            imageView.setBackgroundResource(R.mipmap.category_file_icon_fav_phone_normal);
        }
    }

    private void isShareFileType(ShareFiles shareFiles, String sharefilesuffix) {
        if (Arrays.asList(music).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("音频");
        } else if (Arrays.asList(video).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("视频");
        } else if (Arrays.asList(doc).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("文本");
        } else if (Arrays.asList(apk).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("安卓安装包");
        } else if (Arrays.asList(zip).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("压缩包");
        } else if (Arrays.asList(pic).contains(sharefilesuffix)) {
            shareFiles.setShare_file_type("图片");
        } else {
            shareFiles.setShare_file_type("其他");
        }
    }


}
