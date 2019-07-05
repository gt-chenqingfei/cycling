package com.beastbikes.android.modules.cycling.activity.biz;

import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivitySample;

import java.util.ArrayList;

/**
 * Created by secret on 16/8/17.
 */
public class CorrectionSample {

    private static ArrayList<LocalActivitySample> samplesList = new ArrayList<>();

    private static double SAMPLE_GAP = 1.0;
    private static int SAMPLE_COUNT = 5;
    private static double SPEED_SENSITIVE = 2.0;
    private static double RADIUS_LOW_FILTER = 5.0;
    private static int DRAW_ORIGINAL = 1;
    private static int SELECT_STRATAGRY = 1;
    private static int DYNAMIC_STRAGAGRY = 2;
    private  static int SEE_POINTS = 5;

    private static LocalActivitySample preSample;

    public static LocalActivitySample dynamicSample(LocalActivitySample localActivitySample) {

        samplesList.add(localActivitySample);

        if (samplesList.size() <= SEE_POINTS) {
            return null;
        }

        samplesList.remove(0);

        LocalActivitySample localActivitySample1 = correction(samplesList);

//        double speed = speedCorrection(localActivitySample1);
//        if (speed != -1 && null != localActivitySample1) {
//            localActivitySample1.setVelocity(speed);
//        }

        return localActivitySample1;
    }

    public static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static int getMax(ArrayList<LocalActivitySample> localActivitySamples) {
        double maxDistance = 0.0;
        int maxIndex = 0;
        int len = localActivitySamples.size();
        for (int i = 0; i < len; i++) {
            LocalActivitySample sample = localActivitySamples.get(i);
            if (maxDistance < sample.getDistance()) {
                maxDistance = sample.getDistance();
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static int getMin(ArrayList<LocalActivitySample> localActivitySamples) {
        double minDistance = 9999999.0;
        int minIndex = 0;
        int len = localActivitySamples.size();
        for (int i = 0; i < len; i++) {
            LocalActivitySample sample = localActivitySamples.get(i);
            if (minDistance > sample.getDistance()) {
                minDistance = sample.getDistance();
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);

        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

        double earthRadius = 6738.137;
        s *= earthRadius;
        return Math.abs(s) * 1000;
    }

    public static LocalActivitySample correction(ArrayList<LocalActivitySample> localActivitySamples) {
        double x;
        double y;
        double xSum = 0.0;
        double ySum = 0.0;
        double speedSum = 0.0;
        double speedMax = 0.0;
        for (LocalActivitySample sample : localActivitySamples) {
            xSum += Double.valueOf(sample.getLatitude1());
            ySum += Double.valueOf(sample.getLongitude1());
            speedSum += sample.getVelocity();

            if (speedMax < sample.getVelocity()) {
                speedMax = sample.getVelocity();
            }
        }

        int len = localActivitySamples.size();
        x = xSum / len;
        y = ySum / len;

        double r = speedSum * SAMPLE_GAP * SPEED_SENSITIVE;

        for (LocalActivitySample sample : localActivitySamples) {
            sample.setDistance(distance(Double.valueOf(sample.getLatitude1()), Double.valueOf(sample.getLongitude1()), x, y));
        }

        for (LocalActivitySample sample : localActivitySamples) {
            if (r > RADIUS_LOW_FILTER && sample.getDistance() > r) {
                localActivitySamples.remove(getMax(localActivitySamples));

                return correction(localActivitySamples);
            } else {
                if (SELECT_STRATAGRY == 1) {
                    sample.setLatitude1(String.valueOf(x));
                    sample.setLongitude1(String.valueOf(y));
                    sample.setVelocity(speedSum / localActivitySamples.size());
                    return sample;
                } else {
                    return localActivitySamples.get(getMin(localActivitySamples));
                }
            }
        }
        return null;
    }

    /**
     * 优化速度
     * @param localActivitySample
     * @return
     */

    public static double speedCorrection(LocalActivitySample localActivitySample) {
        double MAX_HUMAN_A = 2.0;
        double AVG_HUMAN_A = 1.2;
        double fixed_speed;
        if (preSample == null) {
            preSample = localActivitySample;
        } else if(localActivitySample.getVelocity() > MAX_HUMAN_A * (localActivitySample.getCurrTime() * 1000 - samplesList.get(0).getCurrTime() * 1000) + samplesList.get(0).getVelocity()) {
            fixed_speed = AVG_HUMAN_A * (localActivitySample.getCurrTime() * 1000 - preSample.getCurrTime() * 1000) + preSample.getVelocity();
            return fixed_speed;
        }
        return -1;
    }

}
