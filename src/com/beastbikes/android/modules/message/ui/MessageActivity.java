package com.beastbikes.android.modules.message.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.message.biz.MessageManager;
import com.beastbikes.android.modules.message.dto.MessageDTO;
import com.beastbikes.android.modules.user.ui.ProfileFragment;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.text.style.HyperLinkSpan;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.HtmlImageGetter;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Alias("我的消息")
@LayoutResource(R.layout.message_activity)
public class MessageActivity extends SessionFragmentActivity implements  RequestQueueManager{

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @IdResource(R.id.message_activity_list_view)
    private ListView messageLv;

    private MessageManager messageManager;
    private MessageAdapter messageAdapter;
    private List<MessageDTO> messages = new ArrayList<MessageDTO>();
    private SharedPreferences sp;

    private RequestQueue requestQueue;

    @Override
    public final RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        this.requestQueue = RequestQueueFactory.newRequestQueue(this);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.messageManager = new MessageManager(this);
        this.messageAdapter = new MessageAdapter(this, messages);
        this.sp = getSharedPreferences(getUserId(), 0);
        this.sp.edit().putInt(Constants.PREF_FRIEND_NEW_MESSAGE_COUNT, 0)
                .commit();
        this.messageLv.setAdapter(messageAdapter);
        this.messageLv.setSelection(messages.size() - 1);

        this.fetchMessageList();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void fetchMessageList() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, List<MessageDTO>>() {

                    @Override
                    protected List<MessageDTO> doInBackground(Void... params) {
                        try {
                            return messageManager.getMessageList();
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<MessageDTO> result) {
                        if (null == result || result.isEmpty())
                            return;

                        messages.clear();
                        messages.addAll(result);
                        messageAdapter.notifyDataSetChanged();
                        int position = messages.size() - 1;
                        messageLv.setSelection(position);

                        sp.edit()
                                .putLong(ProfileFragment.PREF_LAST_DATE,
                                        System.currentTimeMillis())
                                .apply();

                    }
                });
    }

    private final class MessageAdapter extends BaseAdapter {

        private final Context context;
        private final List<MessageDTO> list;

        public MessageAdapter(Context context, List<MessageDTO> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MessageViewHolder vh;

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.message_activity_list_item, null);
                vh = new MessageViewHolder(convertView);
            } else {
                vh = (MessageViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));

            return convertView;
        }

    }

    private final class MessageViewHolder extends ViewHolder<MessageDTO> {

        @IdResource(R.id.message_content_date_tv)
        private TextView time;

        @IdResource(R.id.message_content_msg_by_server)
        private ViewGroup serverVG;

        @IdResource(R.id.message_content_msg_by_client)
        private ViewGroup clientVG;

        @IdResource(R.id.message_content_msg_by_server_tv)
        private TextView serverMsg;

        @IdResource(R.id.message_content_msg_by_client_tv)
        private TextView clientMsg;

        protected MessageViewHolder(View v) {
            super(v);
        }

        @Override
        @SuppressLint("SimpleDateFormat")
        public void bind(MessageDTO t) {
            if (null == t)
                return;

            final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
            this.time.setText(sdf.format(t.getAvailableTime()));

            final RequestQueueManager rqm = MessageActivity.this;
            final ImageGetter getter = new HtmlImageGetter(rqm, this.serverMsg);
            final SpannableStringBuilder builder = (SpannableStringBuilder) Html
                    .fromHtml(t.getMessage(), getter, null);
            final URLSpan[] links = builder.getSpans(0, builder.length(),
                    URLSpan.class);
            for (final URLSpan link : links) {
                final Uri uri = Uri.parse(link.getURL());
                if (!"https".equalsIgnoreCase(uri.getScheme())
                        && !"http".equalsIgnoreCase(uri.getScheme()))
                    continue;

                final int start = builder.getSpanStart(link);
                final int end = builder.getSpanEnd(link);
                final int flags = builder.getSpanFlags(link);
                final ClickableSpan href = new HrefSpan(link.getURL());
                builder.setSpan(href, start, end, flags);
                builder.removeSpan(link);
            }

            this.clientVG.setVisibility(View.GONE);
            this.serverMsg.setVisibility(View.VISIBLE);
            this.serverMsg.setText(builder);
        }
    }

    private final class HrefSpan extends HyperLinkSpan {

        public HrefSpan(final String url) {
            super(url);
        }

        @Override
        public void onClick(View widget) {
            final Context ctx = widget.getContext();
            final Uri uri = Uri.parse(this.getUrl());
            final Intent intent = new Intent(getApplicationContext(),
                    BrowserActivity.class);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setPackage(getPackageName());
            intent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            intent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            intent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            ctx.startActivity(intent);
        }

    }
}
