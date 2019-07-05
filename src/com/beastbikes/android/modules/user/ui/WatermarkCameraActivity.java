package com.beastbikes.android.modules.user.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.filter.other.SaveImageView;
import com.beastbikes.android.modules.user.filter.other.SquareCameraPreview;
import com.beastbikes.android.utils.ImageUtility;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;

@LayoutResource(R.layout.activity_watermark_camera)
public class WatermarkCameraActivity extends SessionFragmentActivity implements
        OnClickListener, SurfaceHolder.Callback, Camera.PictureCallback {

    public static final String EXTRA_ACTIVITY_DTO = "activity_dto";

    public static final String CAMERA_ID_KEY = "camera_id";

    public static final String CAMERA_FLASH_KEY = "flash_mode";

    public static final String PREVIEW_HEIGHT_KEY = "preview_height";

    public static final int REQ_IMAGE_PICKER = 2;

    public static final int REQ_GALLERY_PREVIEW = 3;

    private static final int REQ_CUTTING = 4;

    public static final int REQ_TAKE_PHOTO = 5;

    public static final int REQ_FINISH_CAMERA = 6;

    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;

    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private static final SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyyMMddhhmmss", Locale.getDefault());

    private final DisplayMetrics dm = new DisplayMetrics();

    @IdResource(R.id.activity_watermark_surfaceView)
    private SquareCameraPreview surfaceView;

    @IdResource(R.id.activity_watermark_back)
    private ImageView back;

    @IdResource(R.id.activity_watermark_capture)
    private ImageView capture;

    @IdResource(R.id.activity_watermark_captureView)
    private SaveImageView captureView;

    @IdResource(R.id.activity_watermark_switch_camera)
    private ImageView cameraSwitch;

    @IdResource(R.id.activity_watermark_switch_flash)
    private ImageView flash;

    @IdResource(R.id.activity_watermark_gallery)
    private ImageView gallery;

    private Uri imageUri;

//	private GPUImage gPUImage;
//	private CameraHelper cameraHelper;
//	private CameraLoader camera;
//	private boolean flashMode = true;

    private ActivityDTO dto;

    @IdResource(R.id.activity_watermark_camera_tools)
    private RelativeLayout cameraTools;

    private CameraOrientationListener orientationListener;
    private RelativeLayout previewRoot;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private String flashMode;

    private int coverHeight;
    private int previewHeight;
    private int cameraID;
    private int displayOrientation;
    private int layoutOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        getWindowManager().getDefaultDisplay().getMetrics(this.dm);

        this.orientationListener = new CameraOrientationListener(this);
        this.orientationListener.enable();

        if (savedInstanceState == null) {
            this.cameraID = getBackCameraID();
            this.flashMode = Camera.Parameters.FLASH_MODE_AUTO;
        } else {
            this.cameraID = savedInstanceState.getInt(CAMERA_ID_KEY);
            this.flashMode = savedInstanceState.getString(CAMERA_FLASH_KEY);
            this.previewHeight = savedInstanceState.getInt(PREVIEW_HEIGHT_KEY);
        }


        this.surfaceView.getHolder().addCallback(this);

        //为了保证摄像头预览显示为正方形且不改变压缩比例
        this.previewRoot = (RelativeLayout) this.findViewById(R.id.activity_watermark_surfaceView_root);
        final View topCoverView = this.findViewById(R.id.activity_watermark_cover_top_view);
        final View bottomCoverView = this.findViewById(R.id.activity_watermark_cover_bottom_view);
        final Context ctx = this;
        if (0 == this.coverHeight) {
            ViewTreeObserver observer = surfaceView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    int width = surfaceView.getWidth();
                    previewHeight = surfaceView.getHeight();
                    coverHeight = (previewHeight - width) / 2;

                    topCoverView.getLayoutParams().height = coverHeight;
                    bottomCoverView.getLayoutParams().height = coverHeight;
                    captureView.getLayoutParams().width = width;
                    captureView.getLayoutParams().height = previewHeight;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        surfaceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        surfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    final MarginLayoutParams mlp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    final RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(mlp);
                    final int offset = DimensionUtils.dip2px(ctx, 45);
                    rlp.topMargin = -(coverHeight - offset);
                    previewRoot.setLayoutParams(rlp);
                    captureView.setOffset(coverHeight);

                    final MarginLayoutParams mlp1 = (MarginLayoutParams) cameraTools.getLayoutParams();
                    mlp1.topMargin = surfaceView.getWidth();
                    cameraTools.setLayoutParams(mlp1);
                }
            });
        } else {
            topCoverView.getLayoutParams().height = coverHeight;
            bottomCoverView.getLayoutParams().height = coverHeight;
        }

//		final LayoutParams lp = surfaceView.getLayoutParams();
//		lp.height = dm.widthPixels;
//		lp.width = dm.widthPixels;
//		this.surfaceView.setLayoutParams(lp);

//		this.gPUImage = new GPUImage(this);
//		this.gPUImage.setGLSurfaceView(this.surfaceView);

//		this.cameraHelper = new CameraHelper(this);
//		this.camera = new CameraLoader();

        this.back.setOnClickListener(this);
        this.capture.setOnClickListener(this);
        this.gallery.setOnClickListener(this);
        this.flash.setOnClickListener(this);
        this.cameraSwitch.setOnClickListener(this);
//		if (!cameraHelper.hasFrontCamera() || !cameraHelper.hasBackCamera()) {
//			this.flash
//					.setImageResource(R.drawable.ic_activity_watermark_switch_flash_off);
//		}
        this.setGalleryCover();

        final Intent intent = getIntent();
        if (null != intent && intent.hasExtra(EXTRA_ACTIVITY_DTO)) {
            this.dto = (ActivityDTO) intent
                    .getSerializableExtra(EXTRA_ACTIVITY_DTO);
        }
    }

    @Override
    protected void onResume() {
        this.orientationListener.enable();
        try {
            this.getCamera(this.cameraID);
            this.startCameraPreview();
        } catch (Exception e) {
        }
        if (this.camera == null) {
            Toasts.show(this, R.string.toast_show_open_camera);
            this.finish();
        }
        this.capture.setClickable(true);
        this.surfaceView.setClickable(true);
        this.surfaceView.setAutoFocus(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.orientationListener.disable();
        if (null == this.camera) {
            super.onPause();
            return;
        }

        this.stopCameraPreview();
        this.camera.release();
        this.camera = null;
        super.onPause();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.activity_watermark_capture:
                this.surfaceView.setClickable(false);
                this.surfaceView.setAutoFocus(false);
                this.capture.setClickable(false);
                this.takePicture();
                break;

            case R.id.activity_watermark_gallery:

                final Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_IMAGE_PICKER);
                break;

            case R.id.activity_watermark_switch_flash:

                if (this.flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
                    this.flashMode = Camera.Parameters.FLASH_MODE_ON;
                    this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_on);
                } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                    this.flashMode = Camera.Parameters.FLASH_MODE_OFF;
                    this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_off);
                } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                    this.flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                    this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_auto);
                }
                this.setupCamera();
                break;

            case R.id.activity_watermark_switch_camera:

                if (this.cameraID == CameraInfo.CAMERA_FACING_FRONT) {
                    this.cameraID = getBackCameraID();
                } else {
                    this.cameraID = getFrontCameraID();
                }
                this.restartPreview();
                break;

            case R.id.activity_watermark_back:
                this.finish();
                break;
        }
    }

    private String generatePhotoPath() {
        final File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(dcim, String.format("Beast_Watermark_Camera_%s.png",
                SDF.format(new Date()))).getAbsolutePath();
    }


    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        if (android.os.Build.BRAND.equals("Meizu")) {
            if (RESULT_CANCELED != resultCode) {
                this.dispatch(requestCode, resultCode, data);
            } else {
                this.finish();
            }
        } else {
            this.dispatch(requestCode, resultCode, data);
        }
    }

    private void dispatch(int requestCode, int resultCode, Intent data) {
        if (RESULT_CANCELED != resultCode) {
            switch (requestCode) {
                case REQ_IMAGE_PICKER: {
                    if (RESULT_OK == resultCode) {
                        startCrop(data.getData());
                    }
                    break;
                }
                case REQ_CUTTING: {
                    if (RESULT_OK == resultCode) {
                        if (null != this.imageUri) {
                            final Intent intent = new Intent(this,
                                    WatermarkGalleryActivity.class);
                            intent.putExtra(
                                    WatermarkGalleryActivity.EXTRA_PICTURE_PATH,
                                    this.imageUri);
                            intent.putExtra(
                                    WatermarkGalleryActivity.EXTRA_ACTIVITY_DTO,
                                    this.dto);
                            startActivityForResult(intent, REQ_FINISH_CAMERA);
                        }
                    }
                    break;
                }
                case REQ_FINISH_CAMERA: {
                    if (RESULT_OK == resultCode) {
                        this.setResult(RESULT_OK, data);
                        this.finish();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }


    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 750);
        intent.putExtra("outputY", 750);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        this.imageUri = Uri.parse("file://" + "/" + this.generatePhotoPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQ_CUTTING);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        this.getCamera(cameraID);
        this.startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /*
     *  设置相册封面
     *
     *  */
    private void setGalleryCover() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, File>() {

                                         @Override
                                         protected File doInBackground(Void... params) {
                                             final String[] projection = new String[]{
                                                     MediaStore.Images.ImageColumns._ID,
                                                     MediaStore.Images.ImageColumns.DATA,
                                                     MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                                                     MediaStore.Images.ImageColumns.DATE_TAKEN,
                                                     MediaStore.Images.ImageColumns.MIME_TYPE};

                                             final Cursor cursor = getContentResolver().query(
                                                     MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                                     null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
                                             try {

                                                 if (cursor != null && cursor.moveToFirst()) {
                                                     final String imgPath = cursor.getString(1);
                                                     final File imgFile = new File(imgPath);
                                                     if (imgFile.exists()) {
                                                         return imgFile;
                                                     }
                                                 }
                                             } catch (Exception e) {
                                                 return null;
                                             } finally {
                                                 if(cursor != null) {
                                                     cursor.close();
                                                 }
                                             }
                                             return null;
                                         }

                                         @Override
                                         protected void onPostExecute(File result) {
                                             if (null != result && result.exists()) {
                                                 gallery.setImageURI(Uri.fromFile(result));
                                             } else {
                                                 gallery.setImageResource(R.drawable.ic_activity_watermark_no_image);
                                             }

                                         }

                                     }

        );
    }

    private void getCamera(int cameraID) {
        try {
            this.camera = Camera.open(cameraID);
            this.surfaceView.setCamera(this.camera);
        } catch (Exception e) {
        }
    }

    /**
     * Start the camera preview
     */
    private void startCameraPreview() {
        this.determineDisplayOrientation();
        this.setupCamera();

        try {
            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.startPreview();
        } catch (IOException e) {
            this.finish();
        }
    }

    /**
     * Stop the camera preview
     */
    private void stopCameraPreview() {
        if (null != this.surfaceHolder) {
            this.surfaceHolder.removeCallback(this);
        }
        this.camera.setPreviewCallback(null);
        this.camera.stopPreview();
        this.surfaceView.setCamera(null);
    }

    /**
     * When orientation changes, onOrientationChanged(int) of the listener will be called
     */
    private static class CameraOrientationListener extends OrientationEventListener {

        private int currentNormalizedOrientation;
        private int rememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                currentNormalizedOrientation = normalize(orientation);
            }
        }

        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            rememberedNormalOrientation = currentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            return rememberedNormalOrientation;
        }

    }

    /**
     * Determine the current display orientation and rotate the camera preview
     * accordingly
     */
    private void determineDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.cameraID, cameraInfo);

        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }

        int displayOrientation;

        // Camera direction
        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        this.layoutOrientation = degrees;

        if (null == this.camera) {
            this.getCamera(this.cameraID);
        }

        if (null == this.camera) {
            finish();
        }
        this.camera.setDisplayOrientation(displayOrientation);
    }

    /**
     * Setup the camera parameters
     */
    private void setupCamera() {
        // Never keep a global parameters
        Camera.Parameters parameters = this.camera.getParameters();

        Size bestPreviewSize = determineBestPreviewSize(parameters);
        Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);


        // Set continuous picture focus, if it's supported
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        final List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null && flashModes.contains(this.flashMode)) {
            parameters.setFlashMode(this.flashMode);
            this.flash.setVisibility(View.VISIBLE);
            this.flash.setEnabled(true);
            if (this.flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_AUTO)) {
                this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_auto);
            } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_ON)) {
                this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_on);
            } else if (flashMode.equalsIgnoreCase(Camera.Parameters.FLASH_MODE_OFF)) {
                this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_off);
            }
        } else {
            this.flash.setImageResource(R.drawable.ic_activity_watermark_switch_flash_disable);
            this.flash.setEnabled(false);
        }

        // Lock in the changes
        this.camera.setDisplayOrientation(90);
        parameters.set("rotation", 90);
        if (this.getFrontCameraID() == this.cameraID) {
            parameters.set("rotation", 270);
        }
        this.camera.setParameters(parameters);
    }

    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;
        Size size;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++) {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null) || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize) {
                bestSize = size;
            }
        }

        if (bestSize == null) {
            return sizes.get(sizes.size() - 1);
        }

        return bestSize;
    }

    private void restartPreview() {
        this.stopCameraPreview();
        this.camera.release();
        this.getCamera(this.cameraID);
        this.startCameraPreview();
    }

    private int getFrontCameraID() {
        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return CameraInfo.CAMERA_FACING_FRONT;
        }

        return getBackCameraID();
    }

    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * Take a picture
     */
    private void takePicture() {
        this.orientationListener.rememberOrientation();

        // Shutter callback occurs after the image is captured. This can
        // be used to trigger a sound to let the user know that image is taken
        Camera.ShutterCallback shutterCallback = null;

        // Raw callback occurs when the raw image data is available
        Camera.PictureCallback raw = null;

        // postView callback occurs when a scaled, fully processed
        // postView image is available.
        Camera.PictureCallback postView = null;

        // jpeg callback occurs when the compressed image is available
        this.camera.takePicture(shutterCallback, raw, postView, this);
    }

    @Override
    public void finish() {

        super.finish();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        int rotation = (displayOrientation + orientationListener.getRememberedNormalOrientation() + layoutOrientation) % 360;

        Uri photoUri = null;
        try {
            this.rotatePicture(rotation, data, this.captureView);
            Bitmap bitmap = ((BitmapDrawable) this.captureView.getDrawable()).getBitmap();
            photoUri = ImageUtility.savePicture(this, bitmap);
        } catch (Throwable e) {
        }

        final Intent intent = new Intent(WatermarkCameraActivity.this, WatermarkGalleryActivity.class);
        intent.putExtra(WatermarkGalleryActivity.EXTRA_PICTURE_PATH, photoUri);
        intent.putExtra(WatermarkGalleryActivity.EXTRA_ACTIVITY_DTO, this.dto);
        startActivityForResult(intent, REQ_FINISH_CAMERA);
    }

    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(this, data);
        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
            oldBitmap = null;
        }
        this.captureView.setVisibility(View.VISIBLE);
        this.captureView.setImageBitmap(bitmap);
    }

}
