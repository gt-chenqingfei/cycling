package com.beastbikes.android.modules.user.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.beastbikes.android.R;

public class ActivityDataUtil {

    public static final Map<Integer, Integer> source = new TreeMap<Integer, Integer>();
    private static final int DEFAULT_MAX_DISTANCE = 782;
    private static final int DEFAULT_COUNT = 15759;

    static {
        source.put(0, 0);
        source.put(1, 884);
        source.put(2, 2115);
        source.put(3, 3491);
        source.put(4, 4781);
        source.put(5, 6123);
        source.put(6, 7218);
        source.put(7, 8279);
        source.put(8, 9218);
        source.put(9, 9994);
        source.put(10, 10692);
        source.put(11, 11252);
        source.put(12, 11757);
        source.put(13, 12208);
        source.put(14, 12577);
        source.put(15, 12936);
        source.put(16, 13233);
        source.put(17, 13512);
        source.put(18, 13735);
        source.put(19, 13931);
        source.put(20, 14099);
        source.put(21, 14235);
        source.put(22, 14359);
        source.put(23, 14467);
        source.put(24, 14545);
        source.put(25, 14614);
        source.put(26, 14695);
        source.put(27, 14774);
        source.put(28, 14835);
        source.put(29, 14893);
        source.put(30, 14967);
        source.put(31, 15018);
        source.put(32, 15063);
        source.put(33, 15110);
        source.put(34, 15146);
        source.put(35, 15183);
        source.put(36, 15233);
        source.put(37, 15265);
        source.put(38, 15308);
        source.put(39, 15324);
        source.put(40, 15347);
        source.put(41, 15375);
        source.put(42, 15407);
        source.put(43, 15423);
        source.put(44, 15439);
        source.put(45, 15450);
        source.put(46, 15463);
        source.put(47, 15481);
        source.put(48, 15496);
        source.put(49, 15510);
        source.put(50, 15521);
        source.put(51, 15529);
        source.put(52, 15535);
        source.put(53, 15545);
        source.put(54, 15555);
        source.put(55, 15558);
        source.put(56, 15569);
        source.put(57, 15576);
        source.put(58, 15584);
        source.put(59, 15589);
        source.put(60, 15593);
        source.put(61, 15600);
        source.put(62, 15608);
        source.put(63, 15615);
        source.put(64, 15619);
        source.put(65, 15624);
        source.put(66, 15628);
        source.put(67, 15635);
        source.put(68, 15637);
        source.put(69, 15638);
        source.put(70, 15641);
        source.put(71, 15645);
        source.put(72, 15647);
        source.put(73, 15651);
        source.put(74, 15656);
        source.put(75, 15658);
        source.put(76, 15663);
        source.put(77, 15664);
        source.put(78, 15667);
        source.put(79, 15668);
        source.put(80, 15669);
        source.put(81, 15670);
        source.put(82, 15671);
        source.put(84, 15676);
        source.put(86, 15677);
        source.put(87, 15679);
        source.put(88, 15680);
        source.put(89, 15682);
        source.put(90, 15684);
        source.put(91, 15686);
        source.put(92, 15688);
        source.put(93, 15689);
        source.put(94, 15692);
        source.put(97, 15694);
        source.put(98, 15698);
        source.put(99, 15699);
        source.put(101, 15701);
        source.put(102, 15703);
        source.put(103, 15704);
        source.put(104, 15705);
        source.put(105, 15707);
        source.put(106, 15708);
        source.put(107, 15709);
        source.put(109, 15711);
        source.put(110, 15712);
        source.put(111, 15714);
        source.put(112, 15716);
        source.put(114, 15717);
        source.put(117, 15719);
        source.put(118, 15720);
        source.put(119, 15721);
        source.put(120, 15723);
        source.put(122, 15726);
        source.put(125, 15727);
        source.put(130, 15728);
        source.put(131, 15730);
        source.put(136, 15731);
        source.put(137, 15733);
        source.put(138, 15735);
        source.put(142, 15736);
        source.put(144, 15737);
        source.put(151, 15738);
        source.put(153, 15740);
        source.put(156, 15741);
        source.put(159, 15743);
        source.put(174, 15744);
        source.put(177, 15745);
        source.put(180, 15746);
        source.put(182, 15747);
        source.put(186, 15748);
        source.put(201, 15749);
        source.put(202, 15750);
        source.put(206, 15752);
        source.put(221, 15753);
        source.put(289, 15754);
        source.put(305, 15755);
        source.put(325, 15756);
        source.put(429, 15757);
        source.put(695, 15758);
        source.put(782, 15759);
    }

    public static String getPerForDistance(double totalDistance) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);

        int distance = (int) Math.round(totalDistance);

        if (distance == 0)
            return numberFormat.format((float) distance / (float) DEFAULT_COUNT
                    * 100);

        if (source.containsKey(distance)) {
            int sum = source.get(distance);
            return numberFormat.format((float) sum / (float) DEFAULT_COUNT
                    * 100);
        } else {
            List<Integer> list = new ArrayList<Integer>();
            for (Integer dis : source.keySet()) {
                list.add(dis);
            }
            for (int i = 1; i < list.size(); i++) {
                int dis = list.get(i - 1);
                if (distance > dis && distance < list.get(i)) {
                    return numberFormat.format((float) source.get(dis)
                            / (float) DEFAULT_COUNT * 100);
                } else if (distance > DEFAULT_MAX_DISTANCE) {
                    return numberFormat.format((float) (DEFAULT_COUNT + 1)
                            / (float) DEFAULT_COUNT * 100);
                }
            }
        }

        return "20";
    }

    public static String formatDateTime(Context activity, long time) {
        @SuppressWarnings("deprecation")
        String data = DateUtils.formatDateTime(activity, time,
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR);

        if (TextUtils.isEmpty(data)) {
            return "";
        }

        String hour = data.split(":")[0];
        if (TextUtils.isEmpty(hour))
            return "";
        int h = Integer.parseInt(hour);

        if (h >= 4 && h < 11) {
            return activity
                    .getString(R.string.activity_record_detail_activity_morning_cycling);
        } else if (h >= 11 && h < 13) {
            return activity
                    .getString(R.string.activity_record_detail_activity_midday_cycling);
        } else if (h >= 13 && h < 18) {
            return activity
                    .getString(R.string.activity_record_detail_activity_afternoon_cycling);
        } else {
            return activity
                    .getString(R.string.activity_record_detail_activity_evening_cycling);
        }

    }

}
