package com.beastbikes.android.persistence;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.File;

public final class BeastStore {

    public static final String AUTHORIY = "beast";

    private BeastStore() {
    }

    public static final class Caches {

        public static final String AVATARS = "avatars";

        public static final String ACTIVITIES = "activities";

        public static final String APKS = "apks";

        public static void initialize(Context ctx) {
            final File root = ctx.getExternalCacheDir();
            final String[] dirs = {AVATARS, ACTIVITIES, APKS};
            final int n = dirs.length;

            for (int i = 0; i < n; i++) {
                new File(root, dirs[i]).mkdirs();
            }
        }

    }

    /**
     * Users
     *
     * @author johnson
     */
    public static final class Users {

        public static final String CONTENT_CATEGORY = "user";

        public static final int GENDER_MALE = 1;

        public static final int GENDER_FEMAIL = 0;

        public static final int GENDER_UNKNOWN = -1;

        public static interface UserColumns extends BaseColumns {

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String USERNAME = "username";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String NICKNAME = "nickname";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String EMAIL = "email";

            /**
             * <p>
             * Type: INTEGER
             * </p>
             */
            public static final String GENDER = "gender";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String HEIGHT = "height";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String WEIGHT = "weight";

            /**
             * <p>
             * Type: TEXT  弃用AVATARIMAGE
             * </p>
             */
            public static final String AVATAR = "avatar";

            /**
             * <p>
             * Type: TEXT  弃用AVATARIMAGE
             * </p>
             */
            public static final String BIRTHDAY = "birthday";

            /**
             * <p>
             * Type: BOOLEAN
             * </p>
             */
            public static final String IS_ADMIN = "is_admin";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String CREATED_TIME = "created_time";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String UPDATED_TIME = "updated_time";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String PROVINCE = "province";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CITY = "city";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String DISTRICT = "district";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_DISTANCE = "total_distance";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_ELAPSED_TIME = "total_elapsed_time";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String LATEST_ACTIVITY_TIME = "latest_activity_time";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String USERID = "userId";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String USERINTID = "userIntId";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String UPDATEDAT = "updatedAt";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CREATEDAT = "createdAt";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUBID = "clubId";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String OBJECTID = "objectId";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String ISOK = "isOk";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String GRIDNUM = "gridNum";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String EDITED = "edited";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String WEEKLYDISTANCE = "weeklyDistance";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String SAMEGRID = "sameGrid";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String MONTHLYDISTANCE = "monthlyDistance";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUBNAME = "clubName";

            // 粉丝数
            public static final String FANS_NUM = "fans_num";
            // 关注数
            public static final String FOLLOWER_NUM = "follower_num";
            // 关注状态
            public static final String FOLLOW_STATUS = "follow_status";

            public static final String MEDAL_NUM = "medal_num";

            public static final String SPEEDX_ID = "speedx_id";

        }
    }

    /**
     * Accounts
     *
     * @author caoxiao
     */
    public static final class Accounts {

        public static final String CONTENT_CATEGORY = "accounts";

        public static interface AccountColumns extends BaseColumns {

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String USER_ID = "user_id";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String AUTHTYPE = "authType";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String STATUS = "status";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String AUTHTOKEN = "authToken";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String THIRDNICK = "thirdNick";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String AUTHKEY = "authkey";
        }

    }

    /**
     * Activities
     *
     * @author johnson
     */
    public static final class Activities {

        public static final String CONTENT_CATEGORY = "activity";

        public static final int STATE_NONE = 0;

        public static final int STATE_STARTED = 1;

        public static final int STATE_PAUSED = 2;

        public static final int STATE_AUTO_PAUSED = 3;

        public static final int STATE_FINISHED = 4;

        public static interface ActivityColumns extends BaseColumns {

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String USER_ID = "user_id";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String USERNAME = "username";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String EMAIL = "email";

            /**
             * <p>
             * Type: INTEGER
             * </p>
             */
            public static final String TYPE = "type";

            /**
             * <p>
             * Type: INTEGER
             * </p>
             *
             * @see Activities#STATE_NONE
             * @see Activities#STATE_STARTED
             * @see Activities#STATE_PAUSED
             * @see Activities#STATE_AUTO_PAUSED
             * @see Activities#STATE_FINISHED
             */
            public static final String STATE = "state";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String TITLE = "title";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String DESCRIPTION = "description";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String START_TIME = "start_time";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String FINISH_TIME = "finish_time";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String COORDINATE = "coordinate_system";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String INSTANTANEOUS_VELOCITY = "instantaneous_velocity";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_DISTANCE = "total_distance";

            /**
             * <p>
             * Type: LONG
             * </p>
             */
            public static final String TOTAL_ELAPSED_TIME = "total_elapsed_time";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_CALORIE = "total_calorie";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_RISEN_ALTITUDE = "total_risen_altitude";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String MAX_VELOCITY = "max_velocity";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String MAX_ALTITUDE = "max_altitude";

            /**
             * 最大心率
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String MAX_CARDIAC_RATE = "max_cardiac_rate";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String TOTAL_UPHILL_DISTANCE = "total_uphill_distance";

            public static final String ACTIVITY_URL = "activity_url";

            public static final String FAKE = "fake";

            public static final String SPEED = "speed";

            // BLe 新添加
            /**
             * 数据来源
             */
            public static final String SOURCE = "source";
            /**
             * 设备ID
             */
            public static final String DEVICE_ID = "device_id";
            /**
             * 打点个数
             */
            public static final String SAMPLE_COUNT = "sample_count";
            /**
             * 打点频率
             */
            public static final String SAMPLE_RATE = "sample_rate";
            /**
             * ble 数据类型
             */
            public static final String BLE_DATA_TYPE = "ble_data_type";
            public static final String IS_PRIVATE = "is_private";
            /** ================== v2.4.1 ============= */
            // 平均心率
            public static final String CARDIAC_RATE = "cardiac_rate";
            // 踏频
            public static final String CADENCE = "cadence";
            // 最大踏频
            public static final String CADENCE_MAX = "cadence_max";
            // 中控mac地址
            public static final String CENTRAL_ID = "central_id";
            //中控设备名称
            public static final String CENTRAL_NAME = "central_name";

            // public static final String SCENERY_URL = "scenery_url";
            //
            // public static final String LOCAL_SCENERY_URL =
            // "local_scenery_path";

            public static final Uri CONTENT_URI = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORIY)
                    .appendPath(CONTENT_CATEGORY).build();

        }

        public static final class Samples {

            public static final String CONTENT_CATEGORY = "activity_sample";

            /**
             * Sample data of the specific activity
             *
             * @author johnson
             */
            public static interface SampleColumns extends BaseColumns {

                /**
                 * <p/>
                 * Type: TEXT
                 * </P>
                 */
                public static final String ACTIVITY_ID = "activity_id";

                /**
                 * <p/>
                 * Type: TEXT
                 * </P>
                 */
                public static final String USER_ID = "user_id";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String LONGITUDE_0 = "longitude_0";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String LATITUDE_0 = "latitude_0";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String LONGITUDE_1 = "longitude_1";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String LATITUDE_1 = "latitude_1";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String DISTANCE = "distance";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String VELOCITY = "velocity";

                /**
                 * <p/>
                 * Type: LONG
                 * </P>
                 */
                public static final String TIME = "time";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String ALTITUDE = "altitude";

                /**
                 * <p/>
                 * Type: DOUBLE
                 * </P>
                 */
                public static final String CALORIE = "calorie";

                /**
                 * <p/>
                 * Type: DOUBLE 心率
                 * </P>
                 */
                public static final String CARDIAC_RATE = "cardiac_rate";

                /**
                 * <p/>
                 * Type: LONG
                 * </P>
                 */
                public static final String ELAPSED_TIME = "elapsed_time";

                // v2.0.0 添加的新字段，表示当前定位的时间戳
                public static final String CURR_TIME = "curr_time";

                // ble 添加的新字段
                /**
                 * 极速
                 */
                public static final String MAX_SPEED = "max_speed";
                /**
                 * 最大心率
                 */
                public static final String MAX_CARDIAC_RATE = "max_cardiac_rate";
                /**
                 * 频率
                 */
                public static final String CADENCE = "cadence";
                /**
                 * 最大频率
                 */
                public static final String MAX_CADENCE = "max_cadence";

                public static final Uri CONTENT_URI = new Uri.Builder()
                        .scheme(ContentResolver.SCHEME_CONTENT)
                        .authority(AUTHORIY).appendPath(CONTENT_CATEGORY)
                        .build();

            }

            private Samples() {
            }

        }

        private Activities() {
        }
    }

    public static final class Clubs {

        public static final String CONTENT_CATEGORY = "club";

        /**
         * Sample data of the specific activity
         *
         * @author icedan
         */
        public static interface ClubsColumns extends BaseColumns {

            /**
             * <p/>
             * Type: TEXT
             * </P>
             */
            public static final String CLUB_ID = "club_id";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_NAME = "name";

            /**
             * <p/>
             * Type: TEXT
             * </P>
             */
            public static final String CLUB_DESC = "desc";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_MANAGER_ID = "manager_id";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String CLUB_MILESTONE = "milestone";

            /**
             * <p>
             * Type: DOUBLE
             * </p>
             */
            public static final String CLUB_SCORE = "score";

            /**
             * <p>
             * Type: INT
             * </p>
             */
            public static final String MAX_MEMBERS = "max_members";

            /**
             * <p>
             * Type: INT
             * </p>
             */
            public static final String CLUB_MEMBERS = "members";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_LOGO = "logo";

            /**
             * <p>
             * Type: Text
             * </p>
             */
            public static final String CLUB_PROVINCE = "province";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_CITY = "city";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_NOTICE = "notice";

            /**
             * <p>
             * Type: TEXT
             * </p>
             */
            public static final String CLUB_ACTIVITIES = "activities";

            /**
             * <p/>
             * Type: TEXT 0 成员, 128 管理员
             * </P>
             */
            public static final String USER_ID = "user_id";

            /**
             * <p>
             * Type: INT 0 未加入, 1 已加入, 2 加入申请审核中, 3 创建申请审核中, 4 已创建
             * </p>
             */
            public static final String STATUS = "status";

            /**
             * <p>
             * Type: INT
             * </p>
             */
            public static final String RANK = "rank";

            /**
             * <p>
             * Type: INT
             * </p>
             */
            public static final String LEVEL = "level";

            public static final String TYPE = "type";

            public static final String LINKTO = "linkTo";

            public static final String ISPRIVATE = "isPrivate";

            public static final String CLUB_LEVEL = "clubLevel";

            public static final Uri CONTENT_URI = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORIY)
                    .appendPath(CONTENT_CATEGORY).build();

        }

        private Clubs() {
        }

    }

    public static final class Friend {

        public static final String CONTENT_CATEGORY = "friend";

        /**
         * Sample data of the specific activity
         *
         * @author icedan
         */
        public static interface FriendColumns extends BaseColumns {

            /**
             * <p/>
             * Type: TEXT
             * </P>
             */
            public static final String USER_ID = "user_id";

            public static final String FRIEND_ID = "friend_id";

            public static final String FRIEND_NICKNAME = "friend_name";

            public static final String FRIEND_AVATAR = "friend_avatar";

            public static final String FRIEND_REMARKS = "friend_remarks";

            /**
             * <p>
             * Type: INT 0 好友， 1 非好友
             * </p>
             */
            public static final String STATUS = "status";

            public static final String CREATE_TIME = "create_time";

            public static final Uri CONTENT_URI = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORIY)
                    .appendPath(CONTENT_CATEGORY).build();

        }

        private Friend() {
        }

    }

    public static final class Grid {

        public static final String CONTENT_CATEGORY = "grid";

        public static interface GirdColumns extends BaseColumns {

            public static final String USER_ID = "user_id";

            public static final String COUNT = "count";

            public static final String UNLOCK_AT = "unlock_at";

        }

        private Grid() {

        }

    }

    /**
     * Ble Device
     */
    public static final class BleDevices {

        public static final String CONTENT_CATEGORY = "ble_device";

        public static interface BleDevicesColumns extends BaseColumns {

            public static final String USER_ID = "user_id";

            public static final String DEVICE_ID = "device_id";

            public static final String DEVICE_NAME = "device_name";

            public static final String LAST_BIND_TIME = "last_bind_time";

            public static final String DEVICE_URL = "device_url";

            /**
             * 0: 未连接
             * 1: 已连接
             */
            public static final String STATUS = "status";

            /**
             * 硬件类型
             * 0:Central Computer
             * 1:Whole Bike
             */
            public static final String HARDWARE_TYPE = "hardware_type";

            /**
             * 车型
             * 0 : Speedforce
             * 1 : Mustang
             * 2 : Leopard
             * 3 : Leopard Pro
             * 4 : Giant Customed
             */
            public static final String BRAND_TYPE = "brandType";

            public static final String MAC_ADDRESS = "mac_address";

            public static final String GUARANTEE_TIME = "guarantee_time";

            /**
             * 车架编号
             */
            public static final String FRAME_ID = "frame_id";

            public static final Uri CONTENT_URI = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORIY)
                    .appendPath(CONTENT_CATEGORY).build();
        }

        public BleDevices() {

        }

    }

}
