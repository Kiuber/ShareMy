package top.kiuber.sharemy.javabeans;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/4/27.
 */
public class User extends BmobObject {
    String user_phone, user_password, user_register_location, user_name,
            user_share_sum, user_register_number, user_autograph, user_share_file, user_holder;

    public String getUser_holder() {
        return user_holder;
    }

    public void setUser_holder(String user_holder) {
        this.user_holder = user_holder;
    }

    public String getUser_share_file() {
        return user_share_file;
    }

    public void setUser_share_file(String user_share_file) {
        this.user_share_file = user_share_file;
    }

    public String getUser_autograph() {
        return user_autograph;
    }

    public void setUser_autograph(String user_autograph) {
        this.user_autograph = user_autograph;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_register_location() {
        return user_register_location;
    }

    public void setUser_register_location(String user_register_location) {
        this.user_register_location = user_register_location;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_share_sum() {
        return user_share_sum;
    }

    public void setUser_share_sum(String user_share_sum) {
        this.user_share_sum = user_share_sum;
    }

    public String getUser_register_number() {
        return user_register_number;
    }

    public void setUser_register_number(String user_register_number) {
        this.user_register_number = user_register_number;
    }
}
