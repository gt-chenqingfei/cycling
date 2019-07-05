package com.beastbikes.android.modules.cycling.club.biz;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.beastbikes.android.Constants;
import com.beastbikes.android.RestfulAPI;
import com.beastbikes.android.RestfulAPIFactory;
import com.beastbikes.android.modules.cycling.club.dto.ClubCache;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeed;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedComment;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedPost;
import com.beastbikes.android.modules.cycling.club.dto.ClubMsgDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubPhotoDTO;
import com.beastbikes.android.modules.cycling.club.dto.ClubUser;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.android.utils.JSONUtil;
import com.beastbikes.android.utils.SerializeUtil;
import com.beastbikes.framework.business.AbstractBusinessObject;
import com.beastbikes.framework.business.BusinessContext;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ClubFeedManager extends AbstractBusinessObject implements Constants {

    public interface CacheFeedDataListener {
        public void onGetFeedCacheData(List<ClubFeed> data);
    }

    public interface CachePhotoDataListener {
        public void onGetPhotoCacheData(List<ClubPhotoDTO> data);
    }

    private static final String CLUBFEED_CACHE_DATA_PAHT = "clubfeed";
    private static final Logger logger = LoggerFactory.getLogger(ClubFeedManager.class);

    private ClubFeedStub clubFeedStub;
    private Activity activity;
    private Context context;

    public ClubFeedManager(Context context) {
        super((BusinessContext) context.getApplicationContext());
        this.context = context;

        final RestfulAPIFactory factory = new RestfulAPIFactory(context);
        this.clubFeedStub = factory.create(ClubFeedStub.class,
                RestfulAPI.BASE_URL, RestfulAPI.getParams(context));
    }

    public ClubFeedManager(Activity activity) {

        this((Context) activity);
        this.activity = activity;
    }


    /**
     * 获取俱乐部首页timeline列表
     *
     * @param clubId
     * @param count
     * @return List （俱乐部列表）
     * @throws BusinessException
     */
    public List<ClubFeed> getClubTimeLine(String clubId,
                                          int count, CacheFeedDataListener listener)
            throws BusinessException {

        List<ClubFeed> clubFeedsPost = getClubFeedCache(clubId, ClubFeedService.KEY_CLUBFEED_CACHE_POST);

        if (listener != null) {

            List<ClubFeed> feedList = getClubFeedCache(clubId);
            if (feedList == null) {
                feedList = new ArrayList<ClubFeed>();
            }
            if (clubFeedsPost != null) {
                feedList.addAll(0, clubFeedsPost);
            }
            listener.onGetFeedCacheData(feedList);

        }

        try {
            final JSONObject result = this.clubFeedStub.getClubTimeLine(clubId, 0, 0, count);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {

                List<ClubFeed> feeds = parseClubFeedList(result);
                clubFeedCacheUpdate(feeds, clubId);
                if (clubFeedsPost != null) {
                    feeds.addAll(0, clubFeedsPost);
                }
                return feeds;
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 俱乐部首页timeline列表加载更多
     *
     * @param clubId
     * @param startStamp
     * @param endStamp
     * @param count
     * @return List （俱乐部列表）
     * @throws BusinessException
     */
    public List<ClubFeed> clubTimeLineLoadMore(String clubId, long startStamp, long endStamp,
                                               int count, CacheFeedDataListener listener)
            throws BusinessException {

        try {
            final JSONObject result = this.clubFeedStub.getClubTimeLine(clubId, startStamp, endStamp, count);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                List<ClubFeed> feeds = parseClubFeedList(result);
                return feeds;
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    public List<ClubFeed> parseClubFeedList(JSONObject result) {
        List<ClubFeed> list = new ArrayList<ClubFeed>();

        JSONArray feedList = result.optJSONArray("result");

        for (int i = 0; i < feedList.length(); i++) {
            JSONObject feedItem = feedList.optJSONObject(i);
            ClubFeed feed = new ClubFeed(feedItem);
            if (feed != null) {
                list.add(feed);
            }
        }

        return list;
    }

    public void clubFeedCacheUpdate(List<ClubFeed> clubFeeds, String filePath, String clubId) {
        if (clubFeeds != null) {

            ClubCache cache = new ClubCache(clubFeeds);
            try {
                byte[] cacheData = SerializeUtil.getBytesFromObject(cache);

                File file = FileUtil.archivePath(context, filePath, clubId);
                FileUtil.saveContentToFile(cacheData, file);
            } catch (Exception e) {
                logger.error("clubFeedCache error e=" + e.getMessage());
            }
        }
    }

    public void clubFeedCacheUpdate(List<ClubFeed> clubFeeds, String clubId) {
        clubFeedCacheUpdate(clubFeeds, ClubFeedService.KEY_CLUBFEED_CACHE, clubId);
    }

    public List<ClubFeed> getClubFeedCache(String clubId) {
        return getClubFeedCache(clubId, ClubFeedService.KEY_CLUBFEED_CACHE);
    }

    public List<ClubFeed> getClubFeedCache(String clubId, String filePath) {

        File file = FileUtil.archivePath(context, filePath, clubId);
        byte[] cacheData = FileUtil.readContentBytesFromFile(file);
        if (cacheData == null)
            return null;
        try {
            ClubCache obj = (ClubCache) SerializeUtil.getObjectFromBytes(cacheData);
            if (obj != null) {

                return obj.getClubFeeds();
            }
        } catch (Exception e) {

        }
        return null;
    }

    public void delClubFeedCache(String clubId) {
        List<ClubFeed> list = getClubFeedCache(clubId);
        if (list == null)
            return;
        list.clear();
        clubFeedCacheUpdate(list, clubId);
    }

    public void clubPhotoCacheUpdate(List<ClubPhotoDTO> clubPhotoDTOs, String filePath, String clubId) {
        if (clubPhotoDTOs != null) {

            ClubCache cache = new ClubCache();
            cache.setClubPhotoDTOList(clubPhotoDTOs);
            try {
                byte[] cacheData = SerializeUtil.getBytesFromObject(cache);

                File file = FileUtil.archivePath(context, filePath, clubId);
                FileUtil.saveContentToFile(cacheData, file);
            } catch (Exception e) {
                logger.error("clubPhotoCache error e=" + e.getMessage());
            }
        }
    }

    public List<ClubPhotoDTO> getClubPhotoCache(String clubId, String filePath) {

        File file = FileUtil.archivePath(context, filePath, clubId);
        byte[] cacheData = FileUtil.readContentBytesFromFile(file);
        if (cacheData == null)
            return null;
        try {
            ClubCache obj = (ClubCache) SerializeUtil.getObjectFromBytes(cacheData);
            if (obj != null) {

                return obj.getClubPhotoDTOList();
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 获取某个feed的详情
     *
     * @param fid
     * @return
     * @throws BusinessException
     */
    public ClubFeed getClubFeedInfo(int fid) throws BusinessException {
        try {
            final JSONObject result = this.clubFeedStub.getClubFeedInfo(fid);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject object = result.optJSONObject("result");
                if (!JSONUtil.isNull(object)) {
                    return new ClubFeed(object);
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    public void syncLocalPostClubFeed(ClubFeedPost post) {
        if (post != null) {
            //TODO 同步本地信息
        }
    }

    /**
     * 发布一个俱乐部post
     *
     * @param clubId
     * @param content
     * @param sportIdentify
     * @param imageList
     * @param needSync
     * @return
     * @throws BusinessException
     */
    public ClubFeed postClubFeed(String clubId, String content, String sportIdentify, String imageList,
                                 int needSync) throws BusinessException {
        try {
            logger.info("postClubFeed");
            final JSONObject result = this.clubFeedStub.postClubFeed(clubId, content, sportIdentify,
                    imageList, needSync);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject object = result.optJSONObject("result");
                if (!JSONUtil.isNull(object)) {
                    return new ClubFeed(object);
                }
            }


        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 对俱乐部feed点赞/取消点赞
     *
     * @param cmd    0 like, 1 unlike
     * @param feedId
     * @return
     * @throws BusinessException
     */
    public boolean likeClubFeed(int cmd, int feedId) throws BusinessException {
        try {

            final JSONObject result = this.clubFeedStub.likeClubFeed(cmd, feedId);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                boolean ret = result.optBoolean("result");
                return ret;
            }


        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return false;
    }

    /**
     * 获取单个俱乐部feed的点赞列表
     *
     * @param feedId
     * @param page
     * @param count
     * @return
     * @throws BusinessException
     */
    public List<ClubUser> getClubFeedLikeList(int feedId, int page, int count) throws BusinessException {
        try {

            final JSONObject result = this.clubFeedStub.getClubFeedLikeList(feedId, page, count);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                if (!JSONUtil.isNull(array)) {
                    List<ClubUser> clubUsers = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        ClubUser user = new ClubUser(array.optJSONObject(i));
                        clubUsers.add(user);
                    }
                    return clubUsers;
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }


    /**
     * 评论一条俱乐部feed
     *
     * @param feedId
     * @param content
     * @param replyId
     * @return
     * @throws BusinessException
     */
    public ClubFeedComment postCommentClubFeed(int feedId, String content, int replyId) throws BusinessException {
        try {

            final JSONObject result = this.clubFeedStub.postCommentClubFeed(feedId, content, replyId);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject object = result.optJSONObject("result");
                if (!JSONUtil.isNull(object)) {
                    return new ClubFeedComment(object);
                }
            }


        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 获取俱乐部feed评论列表
     *
     * @param feedId
     * @param page
     * @param count
     * @return
     * @throws BusinessException
     */
    public List<ClubFeedComment> getClubFeedCommentList(int feedId, int page, int count) throws BusinessException {
        try {

            final JSONObject result = this.clubFeedStub.getClubFeedCommentList(feedId, page, count);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                if (!JSONUtil.isNull(array)) {
                    List<ClubFeedComment> comments = new ArrayList<ClubFeedComment>();
                    for (int i = 0; i < array.length(); i++) {
                        ClubFeedComment comment = new ClubFeedComment(array.optJSONObject(i));
                        comments.add(comment);
                    }
                    return comments;
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 删除俱乐部feed
     *
     * @throws BusinessException
     */
    public boolean deleteClubFeed(int feedId, int status, String clubId) throws BusinessException {
        try {

            if (status == ClubFeed.STATE_DOING) {
                ClubFeedService.getInstance().clubFeedPostQueueOut(feedId, clubId);
                return true;
            } else {
                delClubFeedCache(clubId);
                final JSONObject result = this.clubFeedStub.deleteClubFeed(feedId);

                String message = result.optString("message");
                if (!TextUtils.isEmpty(message)) {
                    Toasts.showOnUiThread(activity, message);
                }

                if (result.optInt("code") == 0) {
                    return result.optBoolean("result");
                }

            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return false;
    }

    /**
     * 删除俱乐部feedcomment
     *
     * @throws BusinessException
     */
    public boolean deleteClubComment(int commentId) throws BusinessException {
        try {
            final JSONObject result = this.clubFeedStub.deleteClubFeedComment(commentId);

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
            if (result.optInt("code") == 0) {
                return result.optBoolean("result");
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return false;
    }

    /**
     * 获取俱乐部相册列表
     *
     * @param clubId
     * @param startDate 2015-10-11T09:12:12.918391+0800
     * @param endDate
     * @param count
     * @throws BusinessException
     */
    public List<ClubPhotoDTO> getClubGalleryList(String clubId, String startDate, String endDate, int count, CachePhotoDataListener listener, boolean refresh) throws BusinessException {

        if (listener != null && refresh) {
            List<ClubPhotoDTO> photoList = getClubPhotoCache(clubId, ClubFeedService.KEY_CLUBPHOTO_CACHE);
            listener.onGetPhotoCacheData(photoList);
        }

        try {
            final JSONObject result = this.clubFeedStub.getClubGalleryList(clubId, startDate, endDate, count);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                Map<Long, Integer> headerMap = new TreeMap<>();
                List<ClubPhotoDTO> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    ClubPhotoDTO dto = new ClubPhotoDTO(array.optJSONObject(i));
                    long headerId = dto.getHeaderId();
                    if (!headerMap.containsKey(headerId)) {
                        headerMap.put(dto.getHeaderId(), 0);
                        int year = DateFormatUtil.getYearOfHeader(headerId);
                        int month = DateFormatUtil.getMonthOfHeader(headerId);
                        try {
                            final JSONObject countJson = this.clubFeedStub.getClubGalleryCount(clubId,
                                    DateFormatUtil.getLastDayOfMonth(year, month),
                                    DateFormatUtil.getFirstDayOfMonth(year, month));
                            if (null != countJson && countJson.optInt("code") == 0) {
                                int headerCount = countJson.optInt("result");
                                dto.setHeaderCount(headerCount);
                                headerMap.put(dto.getHeaderId(), headerCount);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(dto);
                }

                if (refresh) {
                    clubPhotoCacheUpdate(list, ClubFeedService.KEY_CLUBPHOTO_CACHE, clubId);
                }

                return list;
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        return null;
    }

    /**
     * 上传俱乐部相册图片
     *
     * @param imageList
     * @param content
     * @throws BusinessException
     */
    public boolean postClubPhotos(String imageList, String content) throws BusinessException {
        try {
            final JSONObject result = this.clubFeedStub.postClubPhotos(imageList, content);
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
//                int success = result.optInt("success");
//                int fail = result.optInt("fail");
//                JSONArray array = result.optJSONArray("imageList");
//                List<ClubPhotoDTO> photos = new ArrayList<>();
//                for (int i = 0; i < array.length(); i++) {
//                    photos.add(new ClubPhotoDTO(array.optJSONObject(i)));
//                }

                return true;
            }
            Log.e("postClubPhotos", result.toString());

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return false;
    }

    /**
     * 删除俱乐部照片
     *
     * @param photoIds
     * @throws BusinessException
     */
    public List<Integer> deleteClubPhotos(String photoIds) {
        try {
            final JSONObject result = this.clubFeedStub.deleteClubPhotos(photoIds);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                JSONObject json = result.optJSONObject("result");
                JSONArray array = json.optJSONArray("success");
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.optInt(i));
                }

                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除俱乐部照片
     *
     * @param photoId
     * @throws BusinessException
     */
    public boolean deleteClubPhoto(int photoId) {
        try {
            final JSONObject result = this.clubFeedStub.deleteClubPhotos(String.valueOf(photoId));
            if (null == result) {
                return false;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                JSONObject json = result.optJSONObject("result");
                JSONArray array = json.optJSONArray("success");
                for (int i = 0; i < array.length(); i++) {
                    return photoId == array.optInt(i);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取照片详情(应广大客户端大爷的意愿，该接口废弃)
     *
     * @param photoId
     * @throws BusinessException
     */
    public void getClubPhotoInfo(int photoId) throws BusinessException {
        try {
            final JSONObject result = this.clubFeedStub.getClubPhotoInfo(photoId);
            Log.e("getClubPhotoInfo", result.toString());
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 对一张俱乐部照片点赞/取消点赞
     *
     * @param photoId
     * @param cmd     操作动作 0 like，1 unlike
     * @throws BusinessException
     */
    public boolean likeClubPhoto(int photoId, int cmd) {
        try {
            final JSONObject result = this.clubFeedStub.likeClubPhoto(photoId, cmd);
            if (null == result) {
                return false;
            }

            if (result.optInt("code") == 0) {
                return result.optBoolean("result");
            }

            String message = result.optString("message");
            if (TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 评论一张俱乐部照片
     *
     * @param photoId
     * @param content 评论内容
     * @param replyId 要回复的评论id, 可不传
     * @throws BusinessException
     */
    public ClubFeedComment postClubPhotoComment(int photoId, String content, int replyId) {
        try {
            final JSONObject result = this.clubFeedStub.postClubPhotoComment(photoId, content, replyId);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONObject object = result.optJSONObject("result");
                if (!JSONUtil.isNull(object)) {
                    return new ClubFeedComment(object);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取俱乐部照片评论列表
     *
     * @param photoId
     * @param page
     * @param count
     * @throws BusinessException
     */
    public List<ClubFeedComment> getClubPhotoCommentList(int photoId, int page, int count) {
        try {
            final JSONObject result = this.clubFeedStub.getClubPhotoCommentList(photoId, page, count);
            if (null == result) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            Log.e("getClubPhotoCommentList", result.toString());
            if (result.optInt("code") == 0) {
                JSONArray array = result.optJSONArray("result");
                List<ClubFeedComment> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    list.add(new ClubFeedComment(array.optJSONObject(i)));
                }

                return list;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取俱乐部照片点赞列表
     *
     * @param photoId
     * @param page
     * @param count
     * @throws BusinessException
     */
    public List<ClubUser> getClubPhotoLikeList(int photoId, int page, int count) throws BusinessException {
        try {

            final JSONObject result = this.clubFeedStub.getClubPhotoLikeList(photoId, page, count);
            if (JSONUtil.isNull(result)) {
                return null;
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            int code = result.optInt("code");
            if (code == 0) {
                JSONArray array = result.optJSONArray("result");
                if (!JSONUtil.isNull(array)) {
                    List<ClubUser> clubUsers = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        ClubUser user = new ClubUser(array.optJSONObject(i));
                        clubUsers.add(user);
                    }
                    return clubUsers;
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 获取未读俱乐部消息数量
     *
     * @throws BusinessException
     */
    public int getMyClubMsgCount() throws BusinessException {
        try {
            int count = 0;
            final JSONObject result = this.clubFeedStub.getMyClubMsgCount();
            if (JSONUtil.isNull(result)) {
                return 0;
            }
            if (result.optInt("code") == 0) {
                count = result.optInt("result", 0);
            }
            return count;
//            Log.e("getMyClubMsgCount", result.toString());
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 获取俱乐部消息(未读消息/历史消息)
     *
     * @param history
     * @param stamp
     * @param count
     * @throws BusinessException
     */
    public List<ClubMsgDTO> getPushRecordList(boolean history, Long stamp, int count) throws BusinessException {
        try {
            if (!history) {
                stamp = null;
            }
            final JSONObject result = this.clubFeedStub.getPushRecordList(history ? 1 : 0, stamp, count);
            if (result == null)
                return null;

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                JSONArray array = result.optJSONArray("result");
                if (array != null && array.length() > 0) {
                    List<ClubMsgDTO> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        ClubMsgDTO clubMsgDTO = new ClubMsgDTO(array.getJSONObject(i));
                        list.add(clubMsgDTO);
                    }
                    return list;
                }
            }

        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return null;
    }

    /**
     * 清空俱乐部消息
     *
     * @throws BusinessException
     */
    public boolean cleanMyClubMsgList() throws BusinessException {
        try {
            final JSONObject result = this.clubFeedStub.cleanMyClubMsgList();

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }

            if (result.optInt("code") == 0) {
                return result.optBoolean("result");
            }
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return false;
    }

    /**
     * 获取某一时间段照片数量
     *
     * @param clubId
     * @param startDate
     * @param endDate
     * @return int
     */
    public int getPhotoNumOfDate(String clubId, String startDate, String endDate) {
        try {
            final JSONObject result = this.clubFeedStub.getClubGalleryCount(clubId, startDate, endDate);
            if (null == result) {
                return 0;
            }

            int code = result.optInt("code");
            if (code == 0) {
                return result.optInt("result");
            }

            String message = result.optString("message");
            if (!TextUtils.isEmpty(message)) {
                Toasts.showOnUiThread(activity, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
