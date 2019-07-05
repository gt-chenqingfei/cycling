package com.beastbikes.android.modules.cycling.simplify;

import com.baidu.mapapi.model.LatLng;
import com.beastbikes.android.modules.user.dto.SampleDTO;
import com.beastbikes.framework.business.BusinessException;

import java.util.ArrayList;
import java.util.List;

public class SimplifyUtil {

    public static final int TOLERANCES_0 = 0;
    public static final int TOLERANCES_1 = 1;
    public static final int TOLERANCES_2 = 2;
    public static final int TOLERANCES_3 = 3;
    public static final int TOLERANCES_4 = 4;
    public static final int TOLERANCES_5 = 5;
    public static final int TOLERANCES_6 = 6;
    public static final int TOLERANCES_7 = 7;
    public static final int TOLERANCES_8 = 8;
    public static final int TOLERANCES_9 = 9;

    private static final float[] TOLERANCES = new float[]{0.003f, 0.001f,
            0.0009f, 0.0006f, 0.0003f, 0.0001f, 0.00009f, 0.00006f, 0.00003f,
            0.00001f};

    /**
     * 压缩坐标点
     *
     * @param tolerance   压缩精度
     * @param highQuality
     * @param list
     * @return
     * @throws BusinessException
     */
    public static List<LatLng> assertPointsEqual(int tolerance,
                                                 boolean highQuality, List<LatLng> list) throws BusinessException {
        if (tolerance < 0)
            tolerance = 0;

        if (tolerance > 9)
            tolerance = 9;

        Point[] pointsExpected = readPoints(list);

        Simplify<Point> aut = new Simplify<Point>(new PointDTO[0]);
        Point[] pointsActual = aut.simplify(pointsExpected,
                TOLERANCES[tolerance], highQuality);
        List<LatLng> points = pointToLatlng(pointsActual);

        return points;
    }

    public static List<SampleDTO> asserSamplesEqual(float tolerance,
                                                    boolean highQuality, List<SampleDTO> list) {

        Point[] pointsExpected = readSamples(list);

        Simplify<Point> aut = new Simplify<Point>(new SampleDTO[0]);
        Point[] pointsActual = aut.simplify(pointsExpected,
                tolerance, highQuality);
        List<SampleDTO> points = pointToSamples(pointsActual);

        return points;
    }

    /**
     * 压缩坐标点
     *
     * @param list
     * @return
     * @throws BusinessException
     */
    public static List<LatLng> assertPointsEqual(List<LatLng> list)
            throws BusinessException {
        Point[] pointsExpected = readPoints(list);

        Simplify<Point> aut = new Simplify<Point>(new PointDTO[0]);
        Point[] pointsActual = aut
                .simplify(pointsExpected, TOLERANCES[5], true);
        List<LatLng> points = pointToLatlng(pointsActual);

        return points;
    }

    public static List<LatLng> assertPointsEqual(float tolerance, List<LatLng> list)
            throws BusinessException {
        Point[] pointsExpected = readPoints(list);

        Simplify<Point> aut = new Simplify<Point>(new PointDTO[0]);
        Point[] pointsActual = aut
                .simplify(pointsExpected, tolerance, true);
        List<LatLng> points = pointToLatlng(pointsActual);

        return points;
    }

    public static List<com.mapbox.mapboxsdk.geometry.LatLng> assertPointsEqual4MB(float tolerance, List<com.mapbox.mapboxsdk.geometry.LatLng> list)
            throws BusinessException {
        Point[] pointsExpected = readPoints4MB(list);

        Simplify<Point> aut = new Simplify<Point>(new PointDTO4MB[0]);
        Point[] pointsActual = aut
                .simplify(pointsExpected, tolerance, true);
        List<com.mapbox.mapboxsdk.geometry.LatLng> points = pointToLatlng4MB(pointsActual);

        return points;
    }

    public static List<com.google.android.gms.maps.model.LatLng> assertPointsEqualforGoogle(float tolerance, List<com.google.android.gms.maps.model.LatLng> list)
            throws BusinessException {
        Point[] pointsExpected = readPointsforGoogle(list);
        Simplify<Point> aut = new Simplify<Point>(new PointDTOForGoogle[0]);
        Point[] pointsActual = aut
                .simplify(pointsExpected, tolerance, true);
        List<com.google.android.gms.maps.model.LatLng> points = pointToLatlngforGoogle(pointsActual);

        return points;
    }

    private static Point[] readPoints(List<LatLng> list) {
        if (null == list || list.isEmpty())
            return null;

        List<PointDTO> points = new ArrayList<PointDTO>();
        for (LatLng ll : list) {
            points.add(new PointDTO(ll));
        }

        return points.toArray(new PointDTO[points.size()]);
    }

    private static Point[] readPoints4MB(List<com.mapbox.mapboxsdk.geometry.LatLng> list) {
        if (null == list || list.isEmpty())
            return null;

        List<PointDTO4MB> points = new ArrayList<>();
        for (com.mapbox.mapboxsdk.geometry.LatLng ll : list) {
            points.add(new PointDTO4MB(ll));
        }

        return points.toArray(new PointDTO4MB[points.size()]);
    }

    private static Point[] readPointsforGoogle(List<com.google.android.gms.maps.model.LatLng> list) {
        if (null == list || list.isEmpty())
            return null;

        List<PointDTOForGoogle> points = new ArrayList<PointDTOForGoogle>();
        for (com.google.android.gms.maps.model.LatLng ll : list) {
            points.add(new PointDTOForGoogle(ll));
        }

        return points.toArray(new PointDTOForGoogle[points.size()]);
    }

    private static Point[] readSamples(List<SampleDTO> list) {
        if (null == list || list.isEmpty())
            return null;

        List<SampleDTO> points = new ArrayList<SampleDTO>();
        for (SampleDTO ll : list) {
            points.add(ll);
        }

        return points.toArray(new SampleDTO[points.size()]);
    }

    private static List<LatLng> pointToLatlng(Point[] points) {
        if (null == points || points.length < 1)
            return null;

        List<LatLng> list = new ArrayList<LatLng>();
        for (int i = 0; i < points.length; i++) {
            PointDTO pd = (PointDTO) points[i];
            list.add(new LatLng(pd.getY(), pd.getX()));
        }

        return list;
    }

    private static List<com.mapbox.mapboxsdk.geometry.LatLng> pointToLatlng4MB(Point[] points) {
        if (null == points || points.length < 1)
            return null;

        List<com.mapbox.mapboxsdk.geometry.LatLng> list = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            PointDTO4MB pd = (PointDTO4MB) points[i];
            list.add(new com.mapbox.mapboxsdk.geometry.LatLng(pd.getY(), pd.getX()));
        }

        return list;
    }

    private static List<com.google.android.gms.maps.model.LatLng> pointToLatlngforGoogle(Point[] points) {
        if (null == points || points.length < 1)
            return null;

        List<com.google.android.gms.maps.model.LatLng> list = new ArrayList<com.google.android.gms.maps.model.LatLng>();
        for (int i = 0; i < points.length; i++) {
            PointDTOForGoogle pd = (PointDTOForGoogle) points[i];
            list.add(new com.google.android.gms.maps.model.LatLng(pd.getY(), pd.getX()));
        }

        return list;
    }

    private static List<SampleDTO> pointToSamples(Point[] points) {
        if (null == points || points.length < 1)
            return null;

        List<SampleDTO> list = new ArrayList<SampleDTO>();
        for (int i = 0; i < points.length; i++) {
            SampleDTO pd = (SampleDTO) points[i];
            list.add(pd);
        }

        return list;
    }
}
