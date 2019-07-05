package com.beastbikes.android.modules.message.biz;

import android.app.Activity;
import android.text.TextUtils;

import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.android.modules.message.dto.MessageDTO;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MessageManager extends AbstractBusinessObject {

    private final MessageComparator comparator;

    private SpeedyServiceStub speedyServiceStub;
    private Activity activity;

    public MessageManager(Activity activity) {
        super((BusinessContext) activity.getApplicationContext());
        this.comparator = new MessageComparator();
        this.activity = activity;
        final Map<String, String> params = new TreeMap<>();
        params.put("User-Agent", RestfulAPI.buildUserAgent(activity));
        params.put("X-Client-Lang", Locale.getDefault().getLanguage());
        if (null != AVUser.getCurrentUser()) {
            params.put("COOKIE", "sessionid="
                    + AVUser.getCurrentUser().getSessionToken());
        }
        final RestfulAPIFactory factory = new RestfulAPIFactory(activity);
        this.speedyServiceStub = factory.create(SpeedyServiceStub.class, RestfulAPI.BASE_URL, params);
    }

    /**
     * New Result APU v2.0
     *
     * @return message list
     * @throws BusinessException
     */
    public List<MessageDTO> getMessageList() throws BusinessException {
        try {
            final JSONObject result = this.speedyServiceStub.getBroadcasts();
            if (null == result) {
                return null;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONArray messageArray = result.optJSONArray("result");
                final List<MessageDTO> list = new ArrayList<>();

                for (int i = 0; i < messageArray.length(); i++) {
                    list.add(new MessageDTO(messageArray.optJSONObject(i)));
                }

                Collections.sort(list, comparator);
                return list;
            } else {
                final String message = result.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * New Result API v2.0
     *
     * @return message count
     * @throws BusinessException
     */
    public int getMessageCount(final long lastDate) throws BusinessException {
        try {
            final JSONObject result = this.speedyServiceStub.getBroadcastCount(lastDate);
            if (null == result) {
                return -1;
            }

            int code = result.optInt("code");
            if (code == 0) {
                final JSONObject obj = result.optJSONObject("result");
                return obj.optInt("count");
            } else {
                String message = result.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return -1;
    }

    private final class MessageComparator implements Comparator<MessageDTO> {

        @Override
        public int compare(MessageDTO lhs, MessageDTO rhs) {
            Date lhsDate = lhs.getAvailableTime();
            Date rhsDate = rhs.getAvailableTime();
            if (lhsDate.after(rhsDate)) {
                return 1;
            }
            return -1;
        }

    }

}
