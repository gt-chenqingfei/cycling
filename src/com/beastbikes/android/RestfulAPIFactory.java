package com.beastbikes.android;

import android.content.Context;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationException;
import com.beastbikes.android.authentication.biz.AuthenticationManager;
import com.beastbikes.android.modules.preferences.ui.SettingActivity;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.sphere.restful.InvocationExpireListener;
import com.beastbikes.android.sphere.restful.ServiceStub;
import com.beastbikes.android.sphere.restful.ServiceStubFactory;

import java.util.Collections;
import java.util.Map;

public class RestfulAPIFactory extends ServiceStubFactory implements  InvocationExpireListener{

    public RestfulAPIFactory(Context context) {
        super(context);
    }

    public <T extends ServiceStub> T create(final Class<T> iface, final String baseUrl) {
        return super.create(iface, baseUrl, Collections.<String, String>emptyMap(), this);
    }

    public <T extends ServiceStub> T create(final Class<T> iface, final String baseUrl, final Map<String, String> headers) {

        return (T) super.create(iface,baseUrl,headers,this);
    }

    @Override
    public void onInvokeTokenExpire() {
        AVUser user = AVUser.getCurrentUser();
        if(user != null)
        {
            LocalUser upo = new LocalUser();
            upo.setPassword(AVUser.getPwd());
            upo.setUsername(user.getUsername());
            new AuthenticationManager(context).signIn(upo, new AuthenticationManager.AuthenticationCallback() {
                @Override
                public void onResult(AuthenticationException e) {
                    if(e != null){
                        SettingActivity.quit(context);
                    }
                }
            }, user.getSignType());
        }
    }
}
