package com.beastbikes.android.modules.social.im.ui.conversation;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.rong.imkit.tools.PhotoFragment;

public class PhotoActivity extends SessionFragmentActivity {
    private PhotoFragment mPhotoFragment;
    private Uri mUri;
    private Uri mThumbUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_photo);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        initView();
        initData();
    }

    protected void initView() {
        mPhotoFragment = (PhotoFragment) getSupportFragmentManager().getFragments().get(0);
    }

    protected void initData() {
        mUri = getIntent().getParcelableExtra("photo");
        mThumbUri = getIntent().getParcelableExtra("thumbnail");

//        if (mUri != null) {
//            mPhotoFragment.initPhoto(mUri, mThumbUri, null);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.icon) {
            if (mThumbUri == null) {
                mThumbUri = mUri ;
            }

            if(mThumbUri == null){
                return false;
            }


            getAsyncTaskQueue().add(new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    return FileUtil.saveImage(mThumbUri, PhotoActivity.this);
                }

                @Override
                protected void onPostExecute(String result) {
                    Toasts.show(PhotoActivity.this,result);
                }
            });


        }
        return super.onOptionsItemSelected(item);
    }

    public void copyFile(String oldPath, String newPath) {
        InputStream inStream = null;
        FileOutputStream fs = null;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                inStream = new FileInputStream(oldPath);
                fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
            }
            Toast.makeText(this, R.string.de_save_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.de_save_fail, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if(inStream != null) {
                    inStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if(fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mUri != null && mUri.getScheme().startsWith("http")) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.de_fix_username, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

}
