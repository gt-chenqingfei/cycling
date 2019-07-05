package com.beastbikes.android.widget.multiimageselector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.beastbikes.android.R;
import com.beastbikes.android.widget.multiimageselector.utils.TouchImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LookImageActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_IMAGES = "images";
    public static final String EXTRA_POS = "position";
    public static final String EXTRA_RESULT = "select_result";
    public static final String EXTRA_PHOTO_IMAGES = "gallery_photos";


    private ArrayList<String> imageUrls = null;
    private ViewPager pager;
    private int pagerPosition = 0;

    private AdapterImagePager adapterImagePager;

    private double windowWidth;
    private double windowHeight;

    private ImageView btn_back;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        setContentView(R.layout.multi_image_selector_image_pager);

        final ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pager = (ViewPager) findViewById(R.id.pager);
        bundle = this.getIntent().getExtras();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.windowWidth = dm.widthPixels;
        this.windowHeight = dm.heightPixels;
        if (bundle != null) {
            imageUrls = bundle.getStringArrayList(EXTRA_IMAGES);
            pagerPosition = bundle.getInt(EXTRA_POS, 0);

            pager.setAdapter(adapterImagePager = new AdapterImagePager(this, imageUrls));
            pager.setCurrentItem(pagerPosition);

            setTitle((pagerPosition + 1) + "/" + imageUrls.size());
            pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    pagerPosition = position;
                    setTitle(position + 1 + "/" + imageUrls.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button commit = (Button) findViewById(R.id.commit);
        commit.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, imageUrls);
        setResult(RESULT_OK, data);
        super.finish();
    }


    public class AdapterImagePager extends PagerAdapter {

        private LayoutInflater inflater;
        public List<String> images = null;

        public AdapterImagePager(Context context, List<String> urls) {
            images = urls;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.multi_image_selector_image_deatils, view, false);
            assert imageLayout != null;
            TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.iv_image);
            if (images.size() <= 0) {
                return null;
            }
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//
//            windowHeight = (double) options.outHeight / (double) options.outWidth * windowWidth;
//            options.inJustDecodeBounds = false;
//


            if(images.get(position).contains("http")){
                Picasso.with(view.getContext()).load(images.get(position)).into(imageView);
            }
            else {
                Picasso.Builder builder = new Picasso.Builder(view.getContext());
                builder.build().load(new File(images.get(position)))
                        .placeholder(R.drawable.multi_image_selector_default_error)
                        .error(R.drawable.multi_image_selector_default_error)
                        .centerCrop()
                        .resize((int) windowWidth, (int) windowHeight)
                        .into(imageView);
            }
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }
}
