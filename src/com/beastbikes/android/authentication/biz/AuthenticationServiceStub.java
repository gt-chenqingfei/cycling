package com.beastbikes.android.authentication.biz;


import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

public interface AuthenticationServiceStub extends ServiceStub {

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param type
     * @param third_nick
     * @return
     */
    @HttpPost("/signIn")
    public JSONObject signIn(@BodyParameter("username") final String username,
                             @BodyParameter("password") final String password,
                             @BodyParameter("type") final int type,
                             @BodyParameter("third_nick") final String third_nick);

    /**
     * 用户登出
     *
     * @return
     */
    @HttpPost("/signOut")
    public JSONObject signOut();

    /**
     * 用户注册
     *
     * @param nickname
     * @param username
     * @param password
     * @return
     */
    @HttpPost("/signUp")
    public JSONObject signUp(@BodyParameter("nickname") final String nickname,
                             @BodyParameter("username") final String username,
                             @BodyParameter("password") final String password,
                             @BodyParameter("vcode") final String vCode);

    /**
     * 邮箱找回密码
     *
     * @param username
     * @return
     */
    @HttpPost("/reset")
    public JSONObject sendResetEmail(@BodyParameter("username") final String username);


    /**
     * 发送手机验证码
     * <p/>
     * mobilephone	string	要验证的手机号(请尽量遵循E.614规范)
     * msgType	string	要验证的短信类型， regClub 注册, getPrize 领取奖品, bindPhone 绑定手机,
     * regPhone 注册手机号, resetPwd 重置密码,
     */
    @HttpPost("/sendSmscode")
    public JSONObject sendSmscode(@BodyParameter("mobilephone") final String mobilephone
            , @BodyParameter("msgType") final String msgType);


    /**
     * 通过手机重置密码
     * <p/>
     * mobilephone	string	手机号(手机号格式请遵循E.164编码规范)
     * vcode	string	短信验证码
     * password	string	新密码
     */
    @HttpPost("/resetPasswordByMobile")
    public JSONObject resetPasswordByMobile(@BodyParameter("mobilephone") final String mobilephone,
                                            @BodyParameter("vcode") final String vcode,
                                            @BodyParameter("password") final String password
    );


}
