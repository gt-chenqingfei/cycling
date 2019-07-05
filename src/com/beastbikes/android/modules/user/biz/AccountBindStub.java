package com.beastbikes.android.modules.user.biz;

import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.annotation.BodyParameter;
import com.beastbikes.android.sphere.restful.annotation.HttpPost;

import org.json.JSONObject;

/**
 * Created by zhangyao on 2016/1/26.
 */
public interface AccountBindStub extends ServiceStub {

    /**
     * 获取绑定状态
     *
     * @return JSONObject
     */
    @HttpPost("/bindStatus")
    JSONObject bindStatus();

    /**
     * 绑定用户
     * authKey	string	能唯一表示用户的 uid 或 open id
     * authToken	string	密码
     * authType	int	登录类型 1 email, 2 mobilephone, 4 weibo, 8 qq, 16 weixin, 32 twitter, 64 facebook, 128 google plus
     * vcode	int	验证码 (可选)
     * third_nick	string	nickname
     */
    @HttpPost("/bindUser")
    JSONObject bindUser(@BodyParameter("authKey") final String authKey,
                        @BodyParameter("authToken") final String authToken,
                        @BodyParameter("authType") final int authType,
                        @BodyParameter("vcode") final int vcode,
                        @BodyParameter("third_nick") final String nickname);

    /**
     * unbindUser
     * authKey	string	能唯一表示用户的 uid 或 open id
     * authType	int	登录类型 1 email, 2 mobilephone, 4 weibo, 8 qq, 16 weixin, 32 twitter, 64 facebook, 128 google plus
     */
    @HttpPost("/unbindUser")
    JSONObject unbindUser(@BodyParameter("authKey") final String authKey,
                          @BodyParameter("authToken") final String authToken,
                          @BodyParameter("authType") final int authType);

    /**
     * bindResetPassword
     * authKey	string	能唯一表示用户的 uid 或 open id
     * authToken	string	密码
     * authType	int	登录类型 1 email, 2 mobilephone, 4 weibo, 8 qq, 16 weixin, 32
     */
    @HttpPost("/bindResetPassword")
    JSONObject bindResetPassword(@BodyParameter("authKey") final String authKey,
                                 @BodyParameter("authToken") final String authToken,
                                 @BodyParameter("authType") final int authType);

    /**
     * 发送验证码
     *
     *  mobilephone	string	要验证的手机号(请尽量遵循E.614规范)
     * msgType	string	要验证的短信类型， regClub 注册, getPrize 领取奖品, bindPhone 绑定手机, regPhone 注册手机号
     */
    @HttpPost("/sendSmscode")
    JSONObject sendSmscode(@BodyParameter("mobilephone") final String mobilephone,
                           @BodyParameter("msgType") final String msgType);

}
