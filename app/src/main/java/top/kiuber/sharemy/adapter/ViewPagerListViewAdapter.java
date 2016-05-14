package top.kiuber.sharemy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import top.kiuber.sharemy.R;
import top.kiuber.sharemy.javabeans.ShareFiles;

/**
 * Created by Administrator on 2016/4/28.
 */
public class ViewPagerListViewAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private int m_count;
    private List<ShareFiles> m_shareFiles;


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
            viewHolder.mTvShareFileSizeAndDownloadNum = (TextView) convertView.findViewById(R.id.tv_sharefile_download_size_and_num);
            viewHolder.mIvShareFileType = (ImageView) convertView.findViewById(R.id.iv_sharefile_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ShareFiles shareFiles = getItem(position);

        viewHolder.mTvShareFileUserBy.setText(shareFiles.getUser_by());
        viewHolder.mTvShareFileTime.setText(shareFiles.getCreatedAt());
        viewHolder.mTvShareFileTitle.setText(shareFiles.getShare_title());
        viewHolder.mTvShareFileSizeAndDownloadNum.setText(shareFiles.getShare_file_download_num());

        isFileType(shareFiles, viewHolder.mIvShareFileType);
        return convertView;
    }

    class ViewHolder {
        public TextView mTvShareFileUserBy;
        public TextView mTvShareFileTime;
        public TextView mTvShareFileTitle;
        public TextView mTvShareFileSizeAndDownloadNum;
        public ImageView mIvShareFileType;
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
            case "教程":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_doc_phone_normal);
                break;
            case "其他":
                imageView.setBackgroundResource(R.mipmap.category_file_icon_fav_phone_normal);
                break;
        }
    }
}
