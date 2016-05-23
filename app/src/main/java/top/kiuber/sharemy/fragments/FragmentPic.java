package top.kiuber.sharemy.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import top.kiuber.sharemy.R;
import top.kiuber.sharemy.adapter.DataQuery;
import top.kiuber.sharemy.utils.AppTools;
import top.kiuber.sharemy.utils.showLoading;

/**
 * Created by Administrator on 2016/4/28.
 */
public class FragmentPic extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.viewpaer_listview, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_vp);
        DataQuery dataQuery = new DataQuery(getContext(), "图片", listView);
        try {
            showLoading showLoading =new showLoading(getContext());
            Dialog dialog = showLoading.showLoadingDialog();

            dataQuery.bmobQuery(dialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
