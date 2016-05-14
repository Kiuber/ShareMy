package top.kiuber.sharemy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import top.kiuber.sharemy.javabeans.ShareFiles;

/**
 * Created by Administrator on 2016/4/28.
 */
public class DataQuery {
    private Context mContext;
    private String m_where;
    private ListView m_listView;
    private ProgressDialog progressDialog;

    public DataQuery(Context context, String where, ListView view) {
        mContext = context;
        m_where = where;
        m_listView = view;
    }

    public void bmobQuery(final ProgressDialog progressDialog) {


        BmobQuery<ShareFiles> query = new BmobQuery<>();
        query.addWhereEqualTo("share_file_type", m_where);
        query.order("-createdAt");//降序排列
        query.findObjects(mContext, new FindListener<ShareFiles>() {
            @Override
            public void onSuccess(List<ShareFiles> list) {
                progressDialog.dismiss();
                if (!(list == null)) {
                    m_listView.setAdapter(new ViewPagerListViewAdapter(mContext, list.size(), list));
                }

            }

            @Override
            public void onError(int i, String s) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(s);
                builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            }
        });
    }
}
