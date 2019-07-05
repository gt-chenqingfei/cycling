package com.beastbikes.android.modules.cycling.activity.util;

public final class CalorieCalculator {

    private CalorieCalculator() {
    }

    /**
     * @param type     The activity type
     * @param velocity The migration velocity
     * @return the K value
     */
    public static final double getCoefficient(ActivityType type, double velocity) {
        final double v = Math.abs(velocity);
        if (v == 0)
            return 0;

        switch (type) {
            case RUNNING:
                return 75.0 / v;
            case CYCLING:
            default:
                return 35.0 / v;
        }
    }

    /**
     * @param m The value of mass in kilogram
     * @param t The value of elapsed time in hour
     * @param k the coefficient
     * @return the number of calories in kcal
     */
    public static final double calculate(double m, double t, double k) {
        return m * t * k;
    }
}
