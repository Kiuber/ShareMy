package top.kiuber.sharemy.activity;

import android.app.Dialog;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private Intent mIntent;
    private EditText mEtShareFilePath, mEtShareFileTitle,
            mEtShareFileContent;
    private TextView mTvShareBy;
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
        setContentView(R.layout.activity_share_file);
        findViewById(R.id.btn_share_file_share).setOnClickListener(this);
        showFileChooser();
        mEtShareFilePath = (EditText) findViewById(R.id.et_share_file_path);
        mEtShareFilePath.setOnClickListener(this);
        mEtShareFileTitle = (EditText) findViewById(R.id.et_share_file_title);
        findViewById(R.id.btn_share_file_choose).setOnClickListener(this);
        mTvShareBy = (TextView) findViewById(R.id.tv_share_by);
        mTvShareBy.setText("由 "
                + SharedUtils.getSharePreference(getApplicationContext(),
                "user_information", Context.MODE_PRIVATE, "user_name")
                + " 分享");
        mIvShareFileType = (ImageView) findViewById(R.id.iv_share_file_type);
        mIvShareFileType.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share_file_share:
                if (TextUtils.equals(mEtShareFilePath.getText().toString(), "")) {
                    AppTools.myToast(getApplicationContext(), "请选择文件或者手动填写路径~", 1);
                } else {
                    if (TextUtils.equals(mEtShareFileTitle.getText().toString(),
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
            case R.id.iv_share_file_type:

                View view = View.inflate(ShareFileActivity.this, R.layout.dialog_share_file_select_file_type, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(ShareFileActivity.this);
                builder.setView(view);
                final Dialog dialog = builder.show();

                final RadioButton mRbMusic = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_music);
                final RadioButton mRbApk = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_apk);
                final RadioButton mRbPic = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_pic);
                final RadioButton mRbVideo = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_video);
                final RadioButton mRbZip = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_zip);
                final RadioButton mRbDoc = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_doc);
                final RadioButton mRbFav = (RadioButton) view.findViewById(R.id.rb_dialog_share_file_select_file_type_fav);
                RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_dialog_share_file_select_file_type);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Log.d("checkedId", checkedId + mRbMusic.getId() + "");
                        if (checkedId == mRbMusic.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_music_phone_normal);
                        } else if (checkedId == mRbApk.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_apk_phone_normal);
                        } else if (checkedId == mRbPic.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_pic_phone_normal);
                        } else if (checkedId == mRbVideo.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_video_phone_normal);
                        } else if (checkedId == mRbZip.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_zip_phone_normal);
                        } else if (checkedId == mRbDoc.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_doc_phone_normal);
                        } else if (checkedId == mRbFav.getId()) {
                            mIvShareFileType.setBackgroundResource(R.mipmap.category_file_icon_fav_phone_normal);
                        } else {
                        }
                        dialog.dismiss();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser() {
        mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mIntent.setType("*/*");
        mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(mIntent, "选择一个文件"),
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

                    mEtShareFilePath.setText(path);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("提示");
                    builder.setMessage("是否把文件名当作标题？");
                    builder.setCancelable(false);
                    builder.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mEtShareFileTitle.setText(getFileName(path));
                                    mIvShareFileType.setVisibility(View.VISIBLE);
                                    isShareFileType(mIvShareFileType, calShareFileSuffix(mEtShareFilePath.getText().toString()));
                                }
                            });
                    builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mEtShareFileTitle.setText(getFileName(path));
                            isShareFileType(mIvShareFileType, calShareFileSuffix(mEtShareFilePath.getText().toString()));
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
        final BmobFile bmobFile = new BmobFile(new File(mEtShareFilePath
                .getText().toString()));
        bmobFile.uploadblock(ShareFileActivity.this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                saveFileInformation(bmobFile.getFileUrl(ShareFileActivity.this),bmobFile.getFilename(),progressDialog,mEtShareFilePath.getText().toString());

            }

            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
                progressDialog.setProgress(value);
            }

            @Override
            public void onFailure(int i, String s) {
                AppTools.myToast(ShareFileActivity.this, s, 1);
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
        shareFiles.setUser_object_id(SharedUtils.getUserInformation(getApplicationContext(), "ObjectId"));
        shareFiles.setUser_by(SharedUtils.getUserInformation(getApplicationContext(), "user_name"));
        shareFiles.setUser_upload_file(fileUrl);
        shareFiles.setShare_title(mEtShareFileTitle.getText().toString());
        shareFiles.setShare_name(fileName);

        shareFiles.setShare_file_size(FormetFileSize(new File(path)));
        shareFiles.setShare_file_download_num("0");
        shareFiles.setShare_file_share_num("0");

        isShareFileType(shareFiles, calShareFileSuffix(mEtShareFilePath.getText().toString()));

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
                AppTools.myToast(getApplicationContext(), arg1, 1);
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
