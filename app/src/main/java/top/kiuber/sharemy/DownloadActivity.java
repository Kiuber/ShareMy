package top.kiuber.sharemy;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import cn.bmob.v3.listener.UpdateListener;
import top.kiuber.sharemy.javabeans.ShareFiles;
import top.kiuber.sharemy.utils.AppTools;

/**
 * Created by Administrator on 2016/4/27.
 */
public class DownloadActivity {
    String user_by1, title1, content1, download_url1, DOWNLOAD_SERVICE = "download", file_name1, num1, oi1;
    Context context1;

    public DownloadActivity(String user_by, String title, String content,
                            String download_url, Context context, String file_name, String num,
                            String oi) {
        user_by1 = user_by;
        title1 = title;
        download_url1 = download_url;
        content1 = content;
        context1 = context;
        file_name1 = file_name;
        num1 = num;
        oi1 = oi;
    }

    void show() {

        DownloadManager downloadManager = (DownloadManager) context1
                .getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(download_url1));
        request.setDestinationInExternalPublicDir("ShareMy", file_name1);
        long downloadId = downloadManager.enqueue(request);
        AppTools.myToast(context1, "正在下载" + file_name1, 1);
        ShareFiles shareFiles = new ShareFiles();
        shareFiles
                .setShare_file_download_num((Integer.parseInt(num1) + 1) + "");
        shareFiles.update(context1, oi1, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(context1, arg1, 1);
            }
        });
    }
}

