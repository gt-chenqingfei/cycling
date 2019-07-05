package com.beastbikes.android.modules.user.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.authentication.AuthenticationBean;
import com.beastbikes.android.authentication.AuthenticationFactory;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.modules.user.ui.binding.BoundPhoneActivity;
import com.beastbikes.android.modules.user.ui.binding.SelectPopupWindow;
import com.beastbikes.android.widget.MaterialDialog;

import java.util.List;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by zhangyao on 2016/1/25.
 */
public class AccountManagementAdapter extends BaseAdapter implements AuthenticationFactory.ShareSDKUserInfoCallBack {

    //    private SelectPopupWindow menuWindow;
    private MaterialDialog dialog;
    private AccountBindGetDataListener accountBindGetDataListener;
    private List<AccountDTO> list;
    private Activity context;
    private int signType;
    private SelectPopupWindow popupWindow;

    public AccountManagementAdapter(List<AccountDTO> data, Activity context, AccountBindGetDataListener accountBindGetDataListener) {
        this.context = context;
        this.list = data;
        this.accountBindGetDataListener = accountBindGetDataListener;
        AVUser avUser = AVUser.getCurrentUser();
        if (avUser != null)
            this.signType = avUser.getSignType();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public AccountDTO getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_account_management_item, null);
            holder.icon = (ImageView) convertView.
                    findViewById(R.id.activity_account_management_item_account_icon);
            holder.account_name = (TextView) convertView.
                    findViewById(R.id.activity_account_management_item_account_name);
            holder.account_tv = (TextView) convertView.
                    findViewById(R.id.activity_account_management_item_account_tv);
            holder.binding = (TextView) convertView.
                    findViewById(R.id.activity_account_management_item_binding);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.account_management_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(null);
        holder.binding.setOnClickListener(null);
        AccountDTO accountDTO = getItem(position);
        holder.bind(accountDTO);
        return convertView;
    }


    //item变成绑定的
    private void changeToBanding(ViewHolder holder) {
        holder.account_tv.setVisibility(View.GONE);
        holder.binding.setVisibility(View.VISIBLE);

    }

    //item变成解绑定的
    private void changeToUnbundling(ViewHolder holder) {
        holder.account_tv.setVisibility(View.VISIBLE);
        holder.binding.setVisibility(View.GONE);

    }

    //设置每个item的图标和名称
    private void changeNameAndIcon(ViewHolder holder, AccountDTO accountDTO) {
        switch (accountDTO.getAuthType()) {
            case AuthenticationFactory.TYPE_WEIBO:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_wieibo_icon_band);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_wieibo_icon_unband);
                break;
            case AuthenticationFactory.TYPE_EMAIL:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_mail_icon_banding);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_mail_icon_banding);
                break;
            case AuthenticationFactory.TYPE_MOBILE_PHONE:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_mobile_icon_banding);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_mobile_icon_unband);
                break;
            case AuthenticationFactory.TYPE_QQ:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_qq_icon_banding);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_qq_icon_unbanding);
                break;
            case AuthenticationFactory.TYPE_WEIXIN:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_wechat_icon_banding);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_wechat_icon_unbanding);
                break;
            case AuthenticationFactory.TYPE_FACEBOOK:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_facebook_icon_band);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_facebook_icon_unband);
                break;
            case AuthenticationFactory.TYPE_GOOGLE_PLUS:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_googleplus_icon_band);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_googleplus_icon_unband);
                break;
            case AuthenticationFactory.TYPE_TWITTER:
                if (accountDTO.getStatus() == AccountDTO.STATUS_BOND)
                    holder.icon.setImageResource(R.drawable.activity_account_management_twitter_icon_band);
                else
                    holder.icon.setImageResource(R.drawable.activity_account_management_twitter_icon_unband);
                break;
        }
    }

    class ViewHolder {
        ImageView icon;
        TextView account_name;
        TextView account_tv;
        TextView binding;
        RelativeLayout layout;

        void bind(final AccountDTO accountDTO) {
            account_name.setText(accountDTO.getAccountName());
            account_tv.setText(accountDTO.getThirdNick());
            if (accountDTO.getStatus() == AccountDTO.STATUS_BOND) {
                changeToUnbundling(this);
                layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showPopupWindow(accountDTO);
                    }
                });
            } else {
                changeToBanding(this);
                binding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding(accountDTO);
                    }
                });
            }
            changeNameAndIcon(this, accountDTO);
        }

    }

    private void showRemoveDialog(final AccountDTO accountDTO) {
        dialog = new MaterialDialog(context);
        dialog.setMessage(context.getString(R.string.activity_account_management_dialog_ms_head) +
                accountDTO.getAccountName() +
                context.getString(R.string.activity_account_management_dialog_ms_tail));
        dialog.setPositiveButton(R.string.club_release_activites_dialog_binding, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountBindGetDataListener.refreshAccountUnBindDate(accountDTO);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


//    //显示对话框再次确认解绑
//    private void showRemoveBindingDialog(String ms) {
//        AlertDialog dialog = new AlertDialog(context, "",
//                context.getString(R.string.activity_account_management_dialog_ms_head) + ms +
//                        context.getString(R.string.activity_account_management_dialog_ms_tail),
//                context.getString(R.string.club_release_activites_dialog_cencle),
//                context.getString(R.string.club_release_activites_dialog_binding),
//                new AlertDialog.DialogListener() {
//                    @Override
//                    public void onClickOk(int id) {
//                        removeBinding();
//                    }
//
//                    @Override
//                    public void onClickCancel(int id) {
//                    }
//                }, R.layout.dialog_alert_layout);
//        dialog.show();
//    }


//    //解绑的逻辑
//    private void removeBinding() {
////        Toasts.showOnUiThread(((Activity) getContext()), getItem(position).getType());
//    }


    //绑定的逻辑
    private void binding(AccountDTO accountDTO) {
        switch (accountDTO.getAuthType()) {
            case AuthenticationFactory.TYPE_EMAIL:

                break;

            case AuthenticationFactory.TYPE_MOBILE_PHONE:
                Intent boundphone_intent = new Intent(context, BoundPhoneActivity.class);
                for (AccountDTO accountdto : list) {
                    if ((accountdto.getAuthType() == AuthenticationFactory.TYPE_EMAIL && accountdto.getStatus() == AccountDTO.STATUS_BOND) ||
                            accountdto.getAuthType() == AuthenticationFactory.TYPE_MOBILE_PHONE && accountdto.getStatus() == AccountDTO.STATUS_BOND) {
                        boundphone_intent.putExtra(BoundPhoneActivity.EXTRA_ISSERPASSWARD, false);
                        break;
                    }
                }
                context.startActivity(boundphone_intent);
                break;
            case AuthenticationFactory.TYPE_WEIBO:
                AuthenticationFactory.authAndGetUserInfo(context, SinaWeibo.NAME, this);
                break;
            case AuthenticationFactory.TYPE_QQ:
                AuthenticationFactory.authAndGetUserInfo(context, QQ.NAME, this);
                break;
            case AuthenticationFactory.TYPE_WEIXIN:
                AuthenticationFactory.authAndGetUserInfo(context, Wechat.NAME, this);
                break;
            case AuthenticationFactory.TYPE_FACEBOOK:
                AuthenticationFactory.authAndGetUserInfo(context, Facebook.NAME, this);
                break;
            case AuthenticationFactory.TYPE_GOOGLE_PLUS:
                AuthenticationFactory.authAndGetUserInfo(context, GooglePlus.NAME, this);
                break;
            case AuthenticationFactory.TYPE_TWITTER:
                AuthenticationFactory.authAndGetUserInfo(context, Twitter.NAME, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void getShareSDKUserInfoCallBack(AuthenticationBean shareSDKUserInfoBean) {
        if (shareSDKUserInfoBean == null) {
//            Toasts.show(context, "获取登录信息失败请重试");
            return;
        }
        Log.e("UserInfoCallBack", "success");
        accountBindGetDataListener.refreshAccountBindDate(shareSDKUserInfoBean);
    }


    public interface AccountBindGetDataListener {
        void refreshAccountBindDate(AuthenticationBean shareSDKUserInfoBean);

        void refreshAccountUnBindDate(AccountDTO accountDTO);
    }

    private void showPopupWindow(final AccountDTO accountDTO) {
        if (accountDTO.getStatus() == AccountDTO.STATUS_BOND) {
            popupWindow = new SelectPopupWindow(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    if (v.getId() == R.id.popup_window_account_slelect_banding)
                        showRemoveDialog(accountDTO);
                }
            });
            popupWindow.showAtLocation(context.findViewById(R.id.activity_account_management),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        }
    }
}
