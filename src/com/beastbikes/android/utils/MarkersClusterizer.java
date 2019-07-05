package com.beastbikes.android.utils;


import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Projection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Utility class for clustering markers.
 * Add it to your project
 */
public class MarkersClusterizer {
    //    private static MapView mapView;
    private static MapboxMap mapboxMap;
    private static int interval;
    private static final int DEFAULT_INTERVAL = 25;

    /**
     * This method will clusterize markers and draw it on the given mapView instance.
     * Used default value for interval
     *
     * @param mapView target {@link com.google.android.gms.maps.GoogleMap} instance
     * @param markers list of all {@link com.google.android.gms.maps.model.MarkerOptions}
     * @return mapView of clusters. You can use it to find all markers in given cluster.
     */
    public static LinkedHashMap<Point, ArrayList<MarkerOptions>> clusterMarkers(MapboxMap mapboxMap, ArrayList<MarkerOptions> markers) throws ExecutionException, InterruptedException {
        return clusterMarkers(mapboxMap, markers, DEFAULT_INTERVAL);
    }

    /**
     * This method will clusterize markers and draw it on the given mapView instance
     *
     * @param mapView target {@link com.google.android.gms.maps.GoogleMap} instance
     * @param markers list of all {@link com.google.android.gms.maps.model.MarkerOptions}
     * @param i       interval between two markers (in pixels)
     * @return mapView of clusters. You can use it to find all markers in given cluster.
     */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<Point, ArrayList<MarkerOptions>> clusterMarkers(MapboxMap mapboxMap, ArrayList<MarkerOptions> markers, int i) throws ExecutionException, InterruptedException {
        MarkersClusterizer.mapboxMap = mapboxMap;
        Projection projection = mapboxMap.getProjection();
        interval = i;
        LinkedHashMap<MarkerOptions, Point> points = new LinkedHashMap<>();
        for (MarkerOptions markerOptions : markers) {
            PointF pf = projection.toScreenLocation(markerOptions.getPosition());
            points.put(markerOptions, new Point((int) pf.x, (int) pf.y));
        }
        MarkersClusterizer.mapboxMap.removeAnnotations();

        CheckMarkersTask checkMarkersTask = new CheckMarkersTask();
        checkMarkersTask.execute(points);
        return checkMarkersTask.get();
    }

    private static class CheckMarkersTask extends AsyncTask<LinkedHashMap<MarkerOptions, Point>, Void, LinkedHashMap<Point, ArrayList<MarkerOptions>>> {

        private double findDistance(float x1, float y1, float x2, float y2) {
            return Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        }

        @Override
        protected LinkedHashMap<Point, ArrayList<MarkerOptions>> doInBackground(LinkedHashMap<MarkerOptions, Point>... params) {
            LinkedHashMap<Point, ArrayList<MarkerOptions>> clusters = new LinkedHashMap<Point, ArrayList<MarkerOptions>>();
            LinkedHashMap<MarkerOptions, Point> points = params[0];
            for (MarkerOptions markerOptions : points.keySet()) { //go thru all markers
                Point point = points.get(markerOptions);
                double minDistance = -1; //Currently found min distance. This need for finding nearest point.
                Point nearestPoint = null; //Currently found nearest point
                double currentDistance;
                for (Point existingPoint : clusters.keySet()) {  //try to find existing cluster for current marker
                    currentDistance = findDistance(point.x, point.y, existingPoint.x, existingPoint.y);
                    if ((currentDistance <= interval) && ((currentDistance < minDistance) || (minDistance == -1))) {
                        minDistance = currentDistance;
                        nearestPoint = existingPoint;
                    }
                }

                if (nearestPoint != null) {
                    clusters.get(nearestPoint).add(markerOptions);
                } else {
                    ArrayList<MarkerOptions> markersForPoint = new ArrayList<>();
                    markersForPoint.add(markerOptions);
                    clusters.put(point, markersForPoint);
                }
            }
            return clusters;
        }

        @Override
        protected void onPostExecute(LinkedHashMap<Point, ArrayList<MarkerOptions>> clusters) {
            mapboxMap.removeAnnotations();
            List<MarkerOptions> markers = new ArrayList<>();
            for (Point point : clusters.keySet()) {
                ArrayList<MarkerOptions> markersForPoint = clusters.get(point);
                MarkerOptions mainMarker = markersForPoint.get(0);
                markers.add(mainMarker);
            }
            mapboxMap.addMarkers(markers);
        }
    }
}
