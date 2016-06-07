package top.kiuber.sharemy.adapter;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.ShareFiles;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppTools;

/**
 * Created by Administrator on 2016/4/28.
 */
public class ViewPagerListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private int m_count;
    private List<ShareFiles> m_shareFiles;
    private long downloadId = 0;


    public ViewPagerListViewAdapter(Context context, int count, List<ShareFiles> shareFiles) {
        this.mContext = context;
        this.m_count = count;
        mInflater = LayoutInflater.from(context);
        m_shareFiles = shareFiles;
    }

    @Override
    public int getCount() {
        return m_count;
    }

    @Override
    public ShareFiles getItem(int position) {
        ShareFiles item = m_shareFiles.get(position);
        return item;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.viewpager_listview_items, parent, false);
            viewHolder.mTvShareFileUserBy = (TextView) convertView.findViewById(R.id.tv_sharefile_user_by);
            viewHolder.mTvShareFileTime = (TextView) convertView.findViewById(R.id.tv_sharefile_time);
            viewHolder.mTvShareFileTitle = (TextView) convertView.findViewById(R.id.tv_sharefile_title);
            viewHolder.mTvShareFiledDownloadNum = (TextView) convertView.findViewById(R.id.tv_sharefile_download_num);
            viewHolder.mIvShareFileType = (ImageView) convertView.findViewById(R.id.iv_sharefile_type);
            viewHolder.mIvShareFileShareNum = (TextView) convertView.findViewById(R.id.tv_sharefile_share);
            viewHolder.mIvShareFileSize = (TextView) convertView.findViewById(R.id.tv_sharefile_file_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ShareFiles shareFiles = getItem(position);

//        viewHolder.mTvShareFileUserBy.setText(shareFiles.getUser_by());
        queryShareBy(viewHolder.mTvShareFileUserBy, shareFiles.getUser_object_id());

        viewHolder.mTvShareFileTime.setText(shareFiles.getCreatedAt());
        viewHolder.mTvShareFileTitle.setText(shareFiles.getShare_title());
        viewHolder.mTvShareFiledDownloadNum.setText(shareFiles.getShare_file_download_num());
        viewHolder.mIvShareFileSize.setText(shareFiles.getShare_file_size());
        viewHolder.mIvShareFileShareNum.setText(shareFiles.getShare_file_share_num());

        isFileType(shareFiles, viewHolder.mIvShareFileType);

        convertViewSetOnClick(convertView, viewHolder, shareFiles);

        final ViewHolder finalViewHolder1 = viewHolder;
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mIvShareFileShareNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, finalViewHolder1.mTvShareFileTitle.getText().toString()+"\n"+ shareFiles.getUser_upload_file());
               mContext.startActivity(intent.createChooser(intent,finalViewHolder1.mTvShareFileTitle.getText().toString()));
                updateCloudShareNumDaDta(shareFiles, finalViewHolder);
                finalViewHolder.mIvShareFileShareNum.setText(Integer.parseInt(finalViewHolder.mIvShareFileShareNum.getText().toString())+1+"");
            }
        });

        return convertView;
    }

    class ViewHolder {
        public TextView mTvShareFileUserBy;
        public TextView mTvShareFileTime;
        public TextView mTvShareFileTitle;
        public TextView mTvShareFiledDownloadNum;
        public ImageView mIvShareFileType;
        public TextView mIvShareFileShareNum;
        public TextView mIvShareFileSize;
    }

    private void isFileType(ShareFiles shareFiles, ImageView imageView) {

        switch (shareFiles.getShare_file_type()) {
            case "音频":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_music_phone_normal);
                break;
            case "安卓安装包":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_apk_phone_normal);
                break;
            case "图片":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_pic_phone_normal);
                break;
            case "视频":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_video_phone_normal);
                break;
            case "压缩包":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_zip_phone_normal);
                break;
            case "文档":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_doc_phone_normal);
                break;
            case "其他":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_fav_phone_normal);
                break;
        }
    }

    private void queryShareBy(final TextView textView, String objectId) {
        BmobQuery<User> userBmobQuery = new BmobQuery();
        userBmobQuery.addWhereEqualTo("objectId", objectId);
        userBmobQuery.findObjects(mContext, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                for (User user : list) {
                    textView.setText(user.getUser_name());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void convertViewSetOnClick(View convertView, final ViewHolder viewHolder, final ShareFiles shareFiles) {
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                View view = layoutInflater.inflate(R.layout.dialog_share_detail, null);

                ImageView mIvShareFileClose = (ImageView) view.findViewById(R.id.iv_dialog_share_detail_close);
                TextView mTvShareFileUserBy = (TextView) view.findViewById(R.id.tv_dialog_share_detail_user_by);
                TextView mTvShareFileFileName = (TextView) view.findViewById(R.id.tv_dialog_share_detail_file_title);
                TextView mTvShareFileFileTime = (TextView) view.findViewById(R.id.tv_dialog_share_detail_file_time);
                ImageView mIvShareFileDownload = (ImageView) view.findViewById(R.id.iv_dialog_share_detail_file_download);
                ImageView mIvShareFileLook = (ImageView) view.findViewById(R.id.iv_dialog_share_detail_file_look);

                mTvShareFileUserBy.setText(viewHolder.mTvShareFileUserBy.getText().toString());
                mTvShareFileFileName.setText(viewHolder.mTvShareFileTitle.getText().toString());
                mTvShareFileFileTime.setText(viewHolder.mTvShareFileTime.getText().toString());

                mIvShareFileDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadShareFile(viewHolder, shareFiles);

                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(view);
                final Dialog dialog = builder.show();

                mIvShareFileClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mIvShareFileLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    }
                });
            }
        });
    }

    private void downloadShareFile(ViewHolder viewHolder, ShareFiles shareFiles) {

        DownloadManager downloadManager = (DownloadManager) mContext
                .getSystemService(mContext.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(shareFiles.getUser_upload_file()));

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (!cursor.moveToFirst()) {
            // 没有记录
            request.setDestinationInExternalPublicDir("ShareMy", shareFiles.getShare_name());
            downloadId = downloadManager.enqueue(request);
            updateCloudDownloadNumDaDta(shareFiles,viewHolder);
            viewHolder.mTvShareFiledDownloadNum.setText(Integer.parseInt(viewHolder.mTvShareFiledDownloadNum.getText().toString())+1+"");


            AppTools.myToast(mContext, "正在下载" + viewHolder.mTvShareFileTitle.getText().toString(), 1);
        } else {
            Toast.makeText(mContext, "已经加入到下载队列", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCloudDownloadNumDaDta(ShareFiles shareFiles,ViewHolder viewHolder){
        shareFiles
                .setShare_file_download_num((Integer.parseInt(viewHolder.mTvShareFiledDownloadNum.getText().toString()) + 1) + "");
        shareFiles.update(mContext, shareFiles.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(mContext, arg1, 1);
            }
        });
    }

    private void updateCloudShareNumDaDta(ShareFiles shareFiles, ViewHolder viewHolder) {
        shareFiles
                .setShare_file_share_num((Integer.parseInt(viewHolder.mIvShareFileShareNum.getText().toString()) + 1) + "");
        shareFiles.update(mContext, shareFiles.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int arg0, String arg1) {
                AppTools.myToast(mContext, arg1, 1);
            }
        });
    }
}

