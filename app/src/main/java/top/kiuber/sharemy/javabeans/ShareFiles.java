package top.kiuber.sharemy.javabeans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ShareFiles extends BmobObject {
    String user_object_id;
    String user_upload_file;
    String user_by;
    String share_title, share_content;

    public String getShare_file_size() {
        return share_file_size;
    }

    public void setShare_file_size(String share_file_size) {
        this.share_file_size = share_file_size;
    }

    public String getShare_file_download_num() {
        return Share_file_download_num;
    }

    public void setShare_file_download_num(String share_file_download_num) {
        Share_file_download_num = share_file_download_num;
    }

    String share_file_type;
    String share_user_location;
    String share_file_size;
    String Share_file_download_num;

    public String getShare_user_location() {
        return share_user_location;
    }

    public void setShare_user_location(String share_user_location) {
        this.share_user_location = share_user_location;
    }

    public String getShare_name() {
        return share_name;
    }

    public void setShare_name(String share_name) {
        this.share_name = share_name;
    }

    String share_name;

    public String getShare_title() {
        return share_title;
    }

    public void setShare_title(String share_title) {
        this.share_title = share_title;
    }

    public String getShare_content() {
        return share_content;
    }

    public void setShare_content(String share_content) {
        this.share_content = share_content;
    }

    public String getUser_by() {
        return user_by;
    }

    public void setUser_by(String user_by) {
        this.user_by = user_by;
    }

    public String getUser_object_id() {
        return user_object_id;
    }

    public void setUser_object_id(String user_object_id) {
        this.user_object_id = user_object_id;
    }

    public String getUser_upload_file() {
        return user_upload_file;
    }

    public void setUser_upload_file(String user_upload_file) {
        this.user_upload_file = user_upload_file;
    }

    public String getShare_file_type() {
        return share_file_type;
    }

    public void setShare_file_type(String share_file_type) {
        this.share_file_type = share_file_type;
    }
}
