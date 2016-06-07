package top.kiuber.sharemy.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import top.kiuber.sharemy.R;

/**
 * Created by Administrator on 2016/5/23.
 */
public class showLoading {

    private Context mContext;

    public showLoading(Context context) {
        this.mContext = context;
    }

    public Dialog showLoadingDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_loading, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(view);
        final Dialog dialog = builder.show();

        TextView mTvDoBackstage = (TextView) view.findViewById(R.id.tv_dialog_loading_backstage);
        mTvDoBackstage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        return dialog;
    }

    public Dialog showLoadingDialog(String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_loading, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(view);
        final Dialog dialog = builder.show();

        TextView mTvMessage = (TextView) view.findViewById(R.id.tv_dialog_loading_message);
        mTvMessage.setText(message);

        TextView mTvDoBackstage = (TextView) view.findViewById(R.id.tv_dialog_loading_backstage);
        mTvDoBackstage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        return dialog;
    }

    public Dialog showLoadingDialog(String title,String message) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.dialog_loading, null);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setView(view);
        final Dialog dialog = builder.show();

        TextView mTvTitle = (TextView) view.findViewById(R.id.tv_dialog_loading_title);
        mTvTitle.setText(title);

        TextView mTvMessage = (TextView) view.findViewById(R.id.tv_dialog_loading_message);
        mTvMessage.setText(message);

        TextView mTvDoBackstage = (TextView) view.findViewById(R.id.tv_dialog_loading_backstage);
        mTvDoBackstage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        return dialog;
    }
}
