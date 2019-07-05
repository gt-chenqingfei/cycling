package com.beastbikes.android;

import java.util.UUID;

public interface Constants {

    public static final String PREF_USER_MAX_HEART_RATE_KEY = "user.max.heart.rate";

    public static final String PREF_SETTING_ACCURACY = "beast.setting.accuracy";

    public static final int MODE_SETTING_ACCURACY_HIGH = 0;

    public static final int MODE_SETTING_ACCURACY_POWER_SAVING = 1;

    public static final String PREF_SETTING_VOICE_FEEDBACK = "beast.setting.voicefeedback";

    public static final int MASK_SETTING_VOICE_FEEDBACK_MASTER = 0x80000000;

    public static final int MASK_SETTING_VOICE_FEEDBACK_DISTANCE = 1 << 0;

    public static final int MASK_SETTING_VOICE_FEEDBACK_ELAPSED_TIME = 1 << 1;

    public static final int MASK_SETTING_VOICE_FEEDBACK_VELOCITY = 1 << 2;

    public static final int MASK_SETTING_VOICE_FEEDBACK_CALORIE = 1 << 3;

    public static final int MASK_SETTING_VOICE_FEEDBACK_ACTIVITY_STATE = 1 << 4;

    public static final String PREF_SETTING_AUTO_PAUSE = "beast.setting.autopause";

    public static final String PREF_SETTING_FOREGROUND = "beast.setting.foreground";

    public static final String PREF_SETTING_MAP_STYLE = "beast.setting.map.style";

    public static final String PREF_SETTING_CYCLING_KEEP_SCREEN_ON = "beast.setting.cycling.keep.screen.on";


    public static final String PREF_CLUB_FEED_ERROR_WARNING = "beast.club.feed.error.warning";

    public static final String PREF_FRIEND_APPLY_LAST_TIME = "beast.friend.apply.last.time";

    public static final String PREF_FRIEND_NEW_MESSAGE_COUNT = "beast.friend.new.message.count";

    public static final String PREF_RONGCLOUD_NEW_MESSAGE_COUNT = "beast.rongcloud.new.message.count";

    public static final String PREF_RONGCLOUD_USER_TOKEN = "beast.rongcloud.user.token";

    public static final String PREF_HOME_NAV_CYCLING_STATE = "beast.home.nav.cycling.state";

    public static final String PREF_HOME_TAB_CHANGE = "beast.home.tab.change";
    public static final String PREF_STRAVA_TOKEN = "com.beastbikes.starva_token";

    // push data key
    public static final String PUSH_START_ACTIVITY_DATA = "push_data";
    public static final String ACTION_VIEW_PUSHDATA = "com.beastbikes.intent.action.VIEW_PUSHDATA";

    public static final String PREF_CYCLING_DATA_SETTING_KEY = "cycling_data_setting";

    public static final String PREF_WATER_MARK_LOAD ="beast.water.marker.load";

    public static final int CYCLING_DATA_SVG_SPEED = 0;

    public static final int CYCLING_DATA_MAX_SPEED = 1;

    public static final int CYCLING_DATA_ALTITUDE = 2;

    public static final int CYCLING_DATA_UPHILL_DISTANCE = 3;

    public static final int CYCLING_DATA_TIME = 4;

    public static final int CYCLING_DATA_CALORIE = 5;

    public static final String PREF_CYCLING_MY_GOAL_KEY = "cycling_my_goal";

    public static final String PREF_CYCLING_TARGET_SETTING_KEY = "cycling_target_setting";

    public static final String SPEEDX_MONTHLY_SVG_DISTANCE = "sa";

    public static final String SPEEDX_TARGET_SELECT_DEFAULT = "target_select_key";

    public static final String PREF_UPDATE_USERINFO = "com.beastbikes.android.home.update_userinfo";

    public static final String PREF_SET_MAP_PRIVATE_FIRST = "com.beastbikes.android.set_map_private_first";


    public static final String PREF_BAZINGA = "com.beastbikes.baznga";
    public static final String PREF_BAZINGA_COUNT = "com.beastbikes.baznga.count";
    public static final String PREF_BAZINGA_BID = "com.beastbikes.baznga.bid";
    public static final String PREF_BAZINGA_COUNTER = "com.beastbikes.baznga.counter";
    public static final String PREF_BAZINGA_IMAGE_URL = "com.beastbikes.baznga.image_url";
    public static final String PREF_BAZINGA_LINK_TO = "com.beastbikes.baznga.link_to";

//    public static final String PREF_IS_LABORATORYON = "isLaboratoryOn";

    /**
     * 友盟自定义参数
     */
    public static final String UMENG_OPEN_MEDAL = "open_medal";

    public static final String UMENG_OPEN_FACEBOOK_LOGIN = "open_3rd_party_login_facebook";

    public static final String UMENG_OPEN_GOOGLEPLUS_LOGIN = "open_google_plus_login_for_android";

    public static final String UMENG_OPEN = "1";

    public static final String UMENG_CLOSE = "0";

    public static final String CLUB_LOGO = "beast.club.logo";

    public static final String CLUB_NAME = "beast.club.name";

    public static final String CLUB_DESC = "beast.club.desc";

    public static final String CLUB_LOGO_CHANGE = "beast.club.logo.change";

    public static final String CLUB_LOGO_LOCALE = "beast.club.logo.locale";

    public static final String PREF_CLUB_LEVEL = "beast.club.level";

    public static final String PREF_CLUB_STATUS = "beast.club.status";

    public static final String PREF_CLUB_REFRESH = "beast.club.refresh";

    public static final String PREF_CLUB_ID = "beast.club.id";

    public static final String PREF_CLUB_USER_ID = "beast.club.user.id";
    public static final String KM_OR_MI = "km_or_mi";

    public static final int DISPLAY_KM = 0;

    public static final int DISPLAY_MI = 1;
    public static final String RANK_GEO = "beast.rank_geo";

    /**
     * 骑行状态监测标志位
     */
    public static final String PREF_CYCLING_STATE_CHECK_KEY = "beast.cycling.state.check";

    public static final String CLUB_NOTICE = "beast.club.notices";


    public static class BLE {
        // -------------------------------------- Ble Setting ------------------------------------------
        /**
         * Speed force protocol version v1.0.0
         */
        public static final byte BLE_PROTOCOL_VERSION = 0x01;
        /**
         * Command Request activity preview sample
         */
        public static final String PREF_BLE_COMMAND_ACTIVITY_PREVIEW_KEY = "beast.ble.command.activity.preview";
        /**
         * Command Request activity sync
         */
        public static final String PREF_BLE_COMMAND_ACTIVITY_SYNC_KEY = "beast.ble.command.activity.sync";
        /**
         * Command request ota start
         */
        public static final String PREF_BLE_COMMAND_OTA_START_KEY = "beast.ble.command.ota.start";
        /**
         * Command request ota activate
         */
        public static final String PREF_BLE_COMMAND_OTA_ACTIVATE_KEY = "beast.ble.command.ota.activate";
        /**
         * Command request ota packet start
         */
        public static final String PREF_BLE_COMMAND_OTA_PACKET_START_KEY = "beast.ble.command.ota.packet.start";
        /**
         * Command request navigation push
         */
        public static final String PREF_BLE_COMMAND_NAVIGATION_PUSH_KEY = "beast.ble.command.navigation.push";
        /**
         * 轮径尺寸
         * 0:20英寸 1:22英寸 2:24英寸 3:26英寸 4:700C 5:27.5 6:29
         */
        public static final String PREF_BLE_WHEEL_KEY = "beast.ble.wheel";
        /**
         * 语言设置
         * 0:zh_CN 1:en_US
         */
        public static final String PREF_BLE_LOCAL_KEY = "beast.ble.local";
        /**
         * 背光设置
         */
        public static final String PREF_BLE_BACK_LIGHT_KEY = "beast.ble.back.light";
        /**
         * 自动灯光
         * 0:No 1:Yes
         */
        public static final String PREF_BLE_AUTO_LIGHT_KEY = "beast.ble.auto.light";
        /**
         * 声音开关
         * 0:No 1:Yes
         */
        public static final String PREF_BLE_SOUND_KEY = "beast.ble.sound";
        /**
         * GPS开关
         * 0:OFF 1:ON
         */
        public static final String PREF_BLE_GPS_KEY = "beast.ble.gps";
        /**
         * 里程单位设置开关
         * 0:KM 1:Mile
         */
        public static final String PREF_BLE_MILEAGE_UNIT_KEY = "beast.ble.mileage.unit";
        /**
         * 消息通知开关
         * 0:OFF,1:ON
         */
        public static final String PREF_BLE_MESSAGE_ON_KEY = "beast.ble.message.on";
        /**
         * 震动开关
         * 0:OFF,1:ON
         */
        public static final String PREF_BLE_VIBRATION_WAKE_KEY = "beast.ble.vibration.wake";
        /**
         * 常用踏频
         */
        public static final String PREF_BLE_CADENCE_KEY = "beast.ble.cadence";

        public static final UUID UUID_SERVICE = UUID.fromString("0751CD10-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        public static final UUID UUID_ACTIVITY_SAMPLE_NOTIFI = UUID.fromString("0751CD20-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_SENSOR_NOTIFI = UUID.fromString("0751CD22-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_CONFIGURATION_WRITE = UUID.fromString("0751CD23-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_COMMAND_REQUEST_WRITE = UUID.fromString("0751CD28-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_COMMAND_NOTIFICATION = UUID.fromString("0751CD24-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_ACTIVITY_SYNC_NOTIFI = UUID.fromString("0751CD26-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_OTA_INFO_WRITE = UUID.fromString("0751CD27-A473-4EA1-B985-38F488DE94DC");

        public static final UUID UUID_PHONE_EXCHANGE_WRITE = UUID.fromString("0751CD29-A473-4EA1-B985-38F488DE94DC");

        /**
         * 连接中控的auth key
         */
        public static final String SPEEDX_BLE_AUTH_KEY = "0Yxa8Wxp!X";

        /**
         * 接收到中控返回的Device Info
         */
        public static final String ACTION_DEVICES_INFO_RESPONSE = "com.beastbikes.android.ble.device.response.action";

        public static final String PREF_BLE_IMG_KEY = "beast.ble.img";

        public static final String PREF_MCU_IMG_KEY = "beast.mcu.img";

        public static final String PREF_UI_IMG_KEY = "beast.ui.img";

        public static final String PREF_A_GPS_IMG_KEY = "beast.a_gps.img";

        public static final String PREF_FONT_IMG_KEY = "beast.font.img";

        public static final String PREF_POWER_IMG_KEY = "beast.power.img";

        public static final String PREF_LOCATION_LAT = "beast.location.manager.lat";

        public static final String PREF_LOCATION_LON = "beast.location.manager.lon";

        public static final String PREF_BLE_AGPS_LAST_UPDATE_TIME_KEY = "beast.ble.agps.last.update.time";

        public static final String PREF_BLE_CONTROL_IS_ACTIVE = "beast.ble.control.active";
        public static final String PREF_BLE_CONTROL_ACTIVE_OWNER = "beast.ble.control.active.owner";

        public static final int SPEEDX_SPEEDFORCE_UUID = 0xCD10;

        public static final int SPEEDX_WHOLE_BIKE_UUID = 0xCD11;

    }


    // 排行榜
    public static final int RANK_REGION_NET_CODE = 0;
    public static final int RANK_REGION_COUNTRY = 1;
    public static final int RANK_REGION_AREA_CODE = 2;

    /**
     * PUSH 相关的常量定义
     */
    public static final class PUSH {


        public static class BADGE {
            /**
             * 俱乐部成员退出
             */
            public static final String CLUB_MEMBER_QUIT_KEY = "10";
            /**
             * 新的俱乐部入队申请
             */
            public static final String CLUB_APPLY_KEY = "11";
            /**
             * 俱乐部转让成功
             */
            public static final String CLUB_TRANSFER_SUCCESS_KEY = "12";
            /**
             * 俱乐部转让被拒绝
             */
            public static final String CLUB_TRANSFER_REFUSE_KEY = "13";
            /**
             * 俱乐部转让取消
             */
            public static final String CLUB_TRANSFER_CANCEL_KEY = "14";
            /**
             * 俱乐部公告
             */
            public static final String CLUB_NOTICE_KEY = "16";
            /**
             * 俱乐部成为首领
             */
            public static final String CLUB_TRANSFER_MASTER_KEY = "17";
            /**
             * 俱乐部转让给你
             */
            public static final String CLUB_TRANSFER_APPLY_KEY = "18";
            /**
             * 俱乐部申请加入成功
             */
            public static final String CLUB_MEMBER_SUCCESS_KEY = "19";
            /**
             * 有人关注你
             */
            public static final String FOLLOWED_KEY = "20";
            /**
             * 赞了你的Feed
             */
            public static final String LIKE_FEED_KEY = "21";
            /**
             * 有人评论的你的Feed
             */
            public static final String COMMENT_FEED_KEY = "22";

            /**
             * 俱乐部申请被拒绝
             */
            public static final String CLUB_TRANSFER_REAPPLY_KEY = "23";
            /**
             * 俱乐部活动
             */
            public static final String CLUB_ACTIVITY_KEY = "30"; // FIXME: 16/9/29 由原来的32改动,服务器没有32,只有30
            /**
             * 未点亮勋章
             */
            public static final String MEDAL_UNRECEIVE_KEY = "31";

            /**
             * 有新的点亮勋章
             * V2.5.0勋章改为自动点亮,既是服务器条件满足后点亮
             */
            public static final String MEDAL_NEW_ACTIVE = "32";

            /**
             * 直接推送内容文本到客户端
             */
            public static final String SHOW_TEXT = "100";
        }

        static class OPEN_PAGE {
            /**
             * 跳转到主页
             */
            public static final int PAGE_MAIN = 0;
            /**
             * 跳转到用户详情页
             */
            public static final int PAGE_PROFILE = 1;
            /**
             * 跳转到路线详情页
             */
            public static final int PAGE_ROUTE = 2;
            /**
             * 跳转到俱乐部详情页
             */
            public static final int PAGE_CLUB_FEED_INFO = 3;
            /**
             * 跳转到入队申请
             */
            public static final int PAGE_APPLY_MANAGER = 4;
            /**
             * 跳转到好友请求
             */
            public static final int PAGE_FRIENDS_APPLY = 5;
            /**
             * 跳转到野兽小助手
             */
            public static final int PAGE_BROADCAST = 6;
            /**
             * 跳转到俱乐部消息
             */
            public static final int PAGE_MESSAGE = 7;
            /**
             * 跳转到勋章首页
             */
            public static final int PAGE_MEDAL = 8;

            /**
             * 跳转到活动详情
             */
            public static final int PAGE_ACTIVITY_DETAIL = 9;

            /**
             * 跳转到Feed详情
             */
            public static final int PAGE_CLUB_FEED_DETAIL = 10;

            /**
             * 跳转历史公告页面
             */
            public static final int PAGE_CLUB_HISTORY_NOTICE = 12;
        }

        public static class PREF_KEY {
            public static final String DOT_CLUB_MORE = "beast.club.dot.more";

            public static final String DOT_GROUP_CHAT = "beast.club.dot.group.chat";

            public static final String DOT_CLUB_ACTIVITY = "beast.club.dot.activity";

            public static final String DOT_CLUB_MSG_TOTAL_COUNT = "beast.club.feed.dot.total.count";

            public static final String DOT_FOLLOW = "beast.follow.dot";

            public static final String DOT_CYCLING_ACTIVITY = "beast.cycling.activity.dot";

            public static final String NOTIFY_CLUB_TRANSFER = "beast.club.notify.transfer";

            public static final String NOTIFY_CLUB_APPLY_PASS = "beast.club.notify.apply.pass";
            public static final String NOTIFY_CLUB_APPLY_REFUSE = "beast.club.notify.apply.refuse";

            public static final String NOTIFY_CLUB_NOTICE = "beast.club.notify.notice";

            public static final String NOTIFY_CLUB_FEED = "beast.club.notify.feed";

            public static final String NOTIFY_FOLLOW = "beast.follow.notify";

            public static final String NOTIFY_CLUB_MEMBER_QUIT = "beast.club.notify.member.quit";

            public static final String NOTIFY_CLUB_TRANSFER_MASTER = "beast.club.notify.transfer.master";
        }

    }


    // 群聊消息免打扰
    public static final String NOTIFY_GROUP_MSG_DND = "beast.club.group.msg.dnd.switch";
    public static final String PREF_DOT_VERSION_UPDATE = "beast.version.update";
    public static final String PREF_DOT_VERSION_UPDATE_GUIDE = "beast.version.update.guide";

    public static final class UrlConfig {
        public static final String DEV_SPEEDX_HOST = configHost();
        public static final String DEV_SPEEDX_HOST_DOMAIN = configHostDomain();
        public static final String DEV_SPEEDX_API = configApi();
        public static final String DEV_SPEEDX_OTA_API = configOtaApi();
        public static final String DEV_SPEEDX_PRODUCTS_URL = configProductsUrl();

        static String configHost() {
            String host = BeastBikes.getHost();
            if (BeastBikes.isDebug) {
                host = "https://" + BeastBikes.devMode + ".speedx.com";
            }
            return host;
        }

        static String configHostDomain() {
            String hostDomain = BeastBikes.getHostDomain();
            if (BeastBikes.isDebug) {
                hostDomain = BeastBikes.devMode + ".speedx.com";

            }
            return hostDomain;
        }

        static String configApi() {
            String api = BeastBikes.getApiUrl();
            if (BeastBikes.isDebug) {
                api = "https://" + BeastBikes.devMode + ".speedx.com" + "/api/";
            }
            return api;
        }

        static String configProductsUrl() {
            String url = "https://account.speedx.com/m/products";
            if (BeastBikes.isDebug) {
                url = "https://fedev.speedx.com/account/m/products";
            }
            return url;
        }

        /**
         * SpeedX ota api
         */
        static String configOtaApi() {
            String api = "http://static.speedx.com/speedforce/update.json?t=";
            if (BeastBikes.isDebug) {
                api = "http://7xof5v.com1.z0.glb.clouddn.com/speedforce/update_test.json?=";
            }

            return api + System.currentTimeMillis();
        }
    }


}
