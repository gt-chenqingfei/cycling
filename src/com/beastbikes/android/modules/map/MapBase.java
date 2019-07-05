package com.beastbikes.android.modules.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;

/**
 * Created by chenqingfei on 16/5/10.
 */
public abstract class MapBase<K>  extends LinearLayout{

    protected Activity context;
    protected boolean isPrivate;
    protected MapListener mapListener;
    protected ScrollView scrollView;
    protected float zoomLevel = 16;
    protected SpeedxMap.OnMapStatusChangeListener onMapStatusChangeListener;
    public MapBase(Context context) {
        super(context);
    }

    /**
     * 初始化地图数据
     */
    protected abstract void init(Activity context,MapListener mapListener,boolean isPrivate,ScrollView s);

    /**
     * 地图画起始点
     */
    protected abstract void drawStartingPoint(K start,K end);

    /**
     * 地图画线
     */
    protected abstract void drawLine(List<K> points);

    /**
     * 缩放比例
     */
    protected abstract void zoomToSpan(List<K>points);

    /**
     * 截屏
     */
    protected abstract void snapshot(SnapshotReadyListener listener);

    /**
     * 初始化view
     */
    protected abstract void onInitView();

    /**
     * 把地图上的点转换为屏幕闪的点
     * @param points
     */
    protected abstract List<Point> getScreenPoints(List<K> points);

    /**
     * 获取海拔坐标点
     * @param points
     * @return
     */
    protected abstract String getElevations(List<K> points);

    public abstract void requestLocation();

    public abstract void setMyLocationConfigeration();
    /**
     * 地图缩放
     * @param level
     */
    public void zoomTo(float level){
        this.zoomLevel = level;
    }

    public float getZoomLevel(){
        return zoomLevel;
    }

    public void setOnMapStatusChangeListener(SpeedxMap.OnMapStatusChangeListener l) {
        this.onMapStatusChangeListener = l;
    }


    protected void onResume() {

    }

    protected void onPause() {

    }

    public void onDestroy() {

    }

    public void onLowMemory(){

    }

    protected void onSaveInstanceState(Bundle outState) {

    }

    public void onCreate(Bundle savedInstanceState){

    }

    public void finish(){

    }

    public void setMapFullScreen(){

    }

    public void setMapWarpScreen(){

    }

    public void setMapStyle(boolean isPrivate){

    }


    /**
     *
     * @param context
     * @param mapListener
     * @param isPrivate
     *
     * @hide
     */
    protected void doInit(Activity context,MapListener mapListener,boolean isPrivate,ScrollView scrollView){
        this.context = context;
        this.mapListener = mapListener;
        this.isPrivate = isPrivate;

        this.scrollView = scrollView;
        onInitView();
    }

    protected DisplayMetrics getDm() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public interface SnapshotReadyListener{
        public void onSnapshotReady(Bitmap bitmap);
    }
}
