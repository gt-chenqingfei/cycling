package com.beastbikes.android.authentication.ui;

/**
 * Created by zhangyao on 2016/2/18.
 */
public class FindPasswordMesssage {
    public static int SIGNIN= 0x123;
    public static int REGISTER_MAIL =0x124;
    public static int REGISTER_PHONE =0x125;
    private int type;
    private String phone;
    private String mail;
    private String areacode;
    private String account ;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public FindPasswordMesssage(String account ){
        this.type = SIGNIN;
        this.account  = account ;

    }

    public FindPasswordMesssage(int type , String mail){
        this.type = type;
        this.mail = mail;

    }
    public FindPasswordMesssage(int type , String areacode ,String phone){
        this.type = type;
        this.areacode = areacode;
        this.phone = phone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }
}
