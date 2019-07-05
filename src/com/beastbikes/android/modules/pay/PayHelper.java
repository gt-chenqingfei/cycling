package com.beastbikes.android.modules.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.beastbikes.android.R;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dao.entity.Club;
import com.beastbikes.android.utils.JSONUtil;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.business.BusinessObject;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.pingplusplus.android.Pingpp;

import org.json.JSONObject;

/**
 * Created by chenqingfei on 16/8/17.
 */
public class PayHelper extends AbstractBusinessObject implements
        BusinessObject {
    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_QPAY = "qpay";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    private BaseFragmentActivity activity = null;
    private PayStub stub;
    private LoadingDialog loadingDialog;
    private AsyncTask payTask = null;

    public PayHelper(BaseFragmentActivity context) {
        super((BusinessContext) context.getApplicationContext());
        this.activity = context;
        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.stub = factory.create(PayStub.class, RestfulAPI.BASE_URL,
                RestfulAPI.getParams(context));
    }

    /**
     * 调用ping++ sdk 支付。
     *
     * @param data
     */
    public void doPay(String data) {
        if (!TextUtils.isEmpty(data) && null != activity) {
            Pingpp.createPayment(activity, data);
        } else {
            throw new RuntimeException("pay error!!");
        }
    }

    /**
     * 先获得服务端返回的charge，调用ping++ sdk 支付。
     *
     * @param channel
     * @param amount
     */
    public void payRequest(final String channel, final String amount) {
        payTask = new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingDialog == null) {
                    loadingDialog = new LoadingDialog(activity, "", true);
                }
                if (loadingDialog.isShowing()) {
                    return;
                }
                loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        payTask.cancel(true);
                    }
                });
                loadingDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return stub.getPayCharge(channel, amount);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (!JSONUtil.isNull(result)) {
                    int code = result.optInt("code");
                    String data = null;
                    if (code != 0) {
                        Toasts.show(activity, "");
                    } else {
                        doPay(data);
                    }
                } else {
                    Toasts.show(activity, "");
                }
            }
        };
        activity.getAsyncTaskQueue().add(payTask);
    }

}
