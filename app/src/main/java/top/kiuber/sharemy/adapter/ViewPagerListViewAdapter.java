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
        convertView = mInflater.inflate(R.layout.viewpager_listview_items, parent, false);
        TextView user_by = (TextView) convertView.findViewById(R.id.tv_sharefile_user_by);
        TextView time = (TextView) convertView.findViewById(R.id.tv_sharefile_time);
        TextView title = (TextView) convertView.findViewById(R.id.tv_sharefile_title);
        TextView size_and_download_num = (TextView) convertView.findViewById(R.id.tv_sharefile_download_size_and_num);
        ImageView type = (ImageView) convertView.findViewById(R.id.iv_sharefile_type);

        ShareFiles shareFiles = getItem(position);
        user_by.setText(shareFiles.getUser_by());
        time.setText(shareFiles.getCreatedAt());
        title.setText(shareFiles.getShare_title());
        size_and_download_num.setText(shareFiles.getShare_file_size()+"，"+shareFiles.getShare_file_download_num());
        isFileType(shareFiles, type);

        return convertView;
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
