package top.kiuber.sharemy;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import top.kiuber.sharemy.javabeans.ShareFiles;
import top.kiuber.sharemy.javabeans.User;
import top.kiuber.sharemy.utils.AppTools;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ListViewActivity extends BaseAdapter {
    LayoutInflater mInflater;
    public List<ShareFiles> arg1;
    public Context context;

    public ListViewActivity(Context applicationContext, List<ShareFiles> arg0) {
        mInflater = LayoutInflater.from(applicationContext);
        arg1 = arg0;
        context = applicationContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arg1.size();
    }

    @Override
    public ShareFiles getItem(int position) {
        // TODO Auto-generated method stub
        ShareFiles item = arg1.get(position);
        return item;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    // 然后重写getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater
                    .inflate(R.layout.share_listview_items, null);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.tv_share_listview_title);
            viewHolder.user_by = (TextView) convertView
                    .findViewById(R.id.tv_share_listview_user_name);
            viewHolder.time = (TextView) convertView
                    .findViewById(R.id.tv_share_listview_time);
            // viewHolder.content = (TextView) convertView
            // .findViewById(R.id.tv_share_listview_content);
            viewHolder.type = (ImageView) convertView
                    .findViewById(R.id.iv_share_listview_file_type);
            // viewHolder.holder = (ImageView) convertView
            // .findViewById(R.id.tv_share_listview_user_holder);
            viewHolder.size = (TextView) convertView
                    .findViewById(R.id.tv_share_listview_file_size);
            viewHolder.download_num = (TextView) convertView
                    .findViewById(R.id.tv_share_listview_file_download_num);
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ShareFiles ss = getItem(position);
        viewHolder.title.setText(ss.getShare_title());

        // viewHolder.content.setText(ss.getShare_content());
        viewHolder.time.setText(ss.getUpdatedAt());

        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("objectId", ss.getUser_object_id());
        query.findObjects(context, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> p1) {
                // TODO: Implement this method
                for (User user : p1) {
                    viewHolder.user_by.setText(user.getUser_name());
                }
            }

            @Override
            public void onError(int p1, String p2) {
                AppTools.myToast(context, p2, 1);
            }

        });
        if (ss.getShare_file_type() == null) {
            viewHolder.type
                    .setBackgroundResource(R.mipmap.category_file_icon_fav_phone_normal);
        } else {
            if (TextUtils.equals(ss.getShare_file_type().toString(), "音频")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_music_phone_normal);
            } else if (TextUtils.equals(ss.getShare_file_type().toString(),
                    "视频")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_video_phone_normal);
            } else if (TextUtils.equals(ss.getShare_file_type().toString(),
                    "文本")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_doc_phone_normal);
            } else if (TextUtils.equals(ss.getShare_file_type().toString(),
                    "安卓安装包")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_apk_phone_normal);
            } else if (TextUtils.equals(ss.getShare_file_type().toString(),
                    "压缩包")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_zip_phone_normal);
            } else if (TextUtils.equals(ss.getShare_file_type().toString(),
                    "图片")) {
                viewHolder.type
                        .setBackgroundResource(R.mipmap.category_file_icon_pic_phone_normal);
            }
        }
        // viewHolder.holder.setBackgroundResource(R.drawable.account_mibi_icon_holder);
        viewHolder.size.setText(ss.getShare_file_size());
        viewHolder.download_num.setText("下载次数："
                + ss.getShare_file_download_num());
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppTools.myToast(context, "长按即可下载哦~", 1);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                DownloadActivity downloadActivity = new DownloadActivity(ss
                        .getUser_by(), ss.getShare_title(), ss
                        .getShare_content(), ss.getUser_upload_file(), context,
                        ss.getShare_name(), ss.getShare_file_download_num(), ss
                        .getObjectId());
                int aa = Integer.parseInt(ss.getShare_file_download_num()) + 1;
                viewHolder.download_num.setText("下载次数：" + aa);
                downloadActivity.show();
                // viewHolder.download_num.setText("下载次数："
                // + Integer.parseInt(ss.getShare_file_download_num()) + 1);
                return false;
            }
        });
        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView user_by;
        // public TextView content;
        public TextView time;
        public ImageView type;
        // public ImageView holder;
        public TextView size;
        public TextView download_num;
    }
}

