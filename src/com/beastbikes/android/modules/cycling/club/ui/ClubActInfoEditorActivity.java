package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.ui.widget.ColorPopupWindows;
import com.beastbikes.android.modules.cycling.club.ui.widget.richeditor.RichEditor;
import com.beastbikes.android.modules.cycling.club.ui.widget.richeditor.RichEditorRelativeLayout;
import com.beastbikes.android.modules.cycling.club.ui.widget.richeditor.Utils;
import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;

@MenuResource(R.menu.create_club_activity_info)
@LayoutResource(R.layout.activity_create_club_act_info)
public class ClubActInfoEditorActivity extends SessionFragmentActivity implements ColorPopupWindows.OnColorChangedListener {

    @IdResource(R.id.activity_create_club_act_info_richeditor)
    private RichEditor mEditor;

    @IdResource(R.id.activity_create_club_act_info_RichEditorLinearLayout)
    private RichEditorRelativeLayout linearLayout;

    @IdResource(R.id.activity_create_club_act_info_addview)
    private LinearLayout addView;

    @IdResource(R.id.activity_create_club_act_info_count)
    private TextView count;

    private boolean isBold = false;

    private boolean isItalic = false;

    private boolean isUnderline = false;

    private int color = Color.BLACK;

    private View menu;

    private ColorPopupWindows popupWindow;

    //图片数量
    private int imageSize;

    //字数是否合格
    private boolean isCommitTextSize = true;
    private static final int REQ_SELECT_IMAGE = 0x453;
    public static String EXTRA_CONTENT = "CONTENT";
    public static String EXTRA_HTML = "html";
    private String html = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_CONTENT) &&
                !intent.getStringExtra(EXTRA_CONTENT).equals("null")) {
            mEditor.setHtml(intent.getStringExtra(EXTRA_CONTENT));
        } else {
            mEditor.setPlaceholder(getString(R.string.club_act_info_hint));
        }
        mEditor.setEditorHeight(232);
        mEditor.setEditorFontSize(18);
        mEditor.setPadding(10, 10, 10, 40);
        menu = LayoutInflater.from(this).inflate(R.layout.activity_create_club_act_info_menu, null);
        addView.addView(menu);
        popupWindow = new ColorPopupWindows(this, this);
        linearLayout.setOnResizeListener(new RichEditorRelativeLayout.OnResizeListener() {
            @Override
            public void OnSoftKeyboardChanged(boolean isSoftKeyboardShown) {
                if (isSoftKeyboardShown) {
                    count.setVisibility(View.GONE);
                    menu.setVisibility(View.VISIBLE);
                } else {
                    count.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.GONE);
                }
            }
        });

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                html = text;
                int n = 0;
                String htmlRemoveTag = Utils.htmlRemoveTag(text);
                if (!TextUtils.isEmpty(htmlRemoveTag)) {
                    n = htmlRemoveTag.length();
                }

                count.setText(n + "/3000");
                if (n > 3000) {
                    Toasts.showOnUiThread(ClubActInfoEditorActivity.this,
                            getString(R.string.club_act_info_text_count));
                    count.setTextColor(Color.RED);
                    isCommitTextSize = false;
                } else {
                    count.setTextColor(Color.BLACK);
                    isCommitTextSize = true;
                }
                imageSize = Utils.getImageSize(html);
                if (imageSize > 3) {
                    Toasts.showOnUiThread(ClubActInfoEditorActivity.this,
                            getString(R.string.club_act_info_images));
                    isCommitTextSize = false;
                }
            }
        });

        menu.findViewById(R.id.activity_create_club_act_info_commit).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCommitTextSize) {
                            Intent data = new Intent();
                            data.putExtra(EXTRA_HTML, mEditor.getHtml());
                            setResult(RESULT_OK, data);
                            hideInputMethod();
                            finish();
                        } else {
                            Toasts.showOnUiThread(ClubActInfoEditorActivity.this,
                                    getString(R.string.club_act_info_text_iamge_count));
                        }
                    }
                });

        menu.findViewById(R.id.action_textcolor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] location = new int[2];
                menu.findViewById(R.id.action_textcolor).getLocationOnScreen(location);
                int x = location[0];

                popupWindow.showAsDropDown(menu, x - menu.getWidth() / 5, 0);
            }
        });


        menu.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBold) {
                    ((ImageButton) (menu.findViewById(R.id.action_bold))).
                            setImageResource(R.drawable.rich_bold);
                } else {
                    ((ImageButton) (menu.findViewById(R.id.action_bold))).
                            setImageResource(R.drawable.not_rich_bold);
                }
                isBold = !isBold;
                mEditor.setBold();
            }
        });


        menu.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isUnderline) {
                    ((ImageButton) (menu.findViewById(R.id.action_underline))).
                            setImageResource(R.drawable.rich_underline);
                } else {
                    ((ImageButton) (menu.findViewById(R.id.action_underline))).
                            setImageResource(R.drawable.not_rich_underline);
                }
                isUnderline = !isUnderline;
                mEditor.setUnderline();
            }
        });

        menu.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isItalic) {
                    ((ImageButton) (menu.findViewById(R.id.action_italic))).
                            setImageResource(R.drawable.rich_italic);
                } else {
                    ((ImageButton) (menu.findViewById(R.id.action_italic))).
                            setImageResource(R.drawable.not_rich_italic);
                }
                isItalic = !isItalic;
                mEditor.setItalic();
            }
        });

//        menu.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
//            boolean isChanged;
//
//            @Override public void onClick(View v) {
//                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
//                isChanged = !isChanged;
//            }
//        });


        menu.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        menu.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        menu.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });


        menu.findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
            }
        });

    }


    private void getImages() {
        if (imageSize < 3) {
            Intent it = new Intent(this, MultiImageSelectorActivity.class);
            it.putExtra(MultiImageSelectorActivity.EXTRA_GALLERY_FULL, true);
            it.putExtra(MultiImageSelectorActivity.EXTRA_IS_SHOWMAX, false);
            it.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 3 - imageSize);
            it.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST,
                    new ArrayList<String>());
            startActivityForResult(it, REQ_SELECT_IMAGE);
        } else {
            Toasts.showOnUiThread(this, getString(R.string.club_act_info_images));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case REQ_SELECT_IMAGE:
                        if (mEditor != null) {
                            ArrayList<String> list = data.getStringArrayListExtra(
                                    MultiImageSelectorActivity.EXTRA_RESULT);
                            if (null != list && !list.isEmpty()) {
                                for (String path : list) {
                                    mEditor.insertImage(path, path.substring(path.lastIndexOf("/") + 1));
                                }
                            }
                        }
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    public void hideInputMethod() {
        View view = this.getWindow().peekDecorView();
        if (view != null) {
            //隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
                    0);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent data = new Intent();
            data.putExtra(EXTRA_HTML, mEditor.getHtml());
            setResult(RESULT_OK, data);
            hideInputMethod();
            finish();
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent data = new Intent();
            data.putExtra(EXTRA_HTML, mEditor.getHtml());
            setResult(RESULT_OK, data);
            hideInputMethod();
            finish();
            return true;
        } else if (item.getItemId() == R.id.create_club_activity_info_clear_item) {
            mEditor.clearAll();
            imageSize = 0;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void colorChanged(int color) {
        mEditor.setTextColor(color);
        ImageButton imageButton = (ImageButton) menu.findViewById(R.id.action_textcolor);

        if (color == getResources().getColor(R.color.view_color_circle_red)) {
            imageButton.setImageResource(R.drawable.action_textcolor_red);
        } else if (color == getResources().getColor(R.color.view_color_circle_black)) {
            imageButton.setImageResource(R.drawable.rich_textcolor);
        } else if (color == getResources().getColor(R.color.view_color_circle_green)) {
            imageButton.setImageResource(R.drawable.action_textcolor_green);
        } else if (color == getResources().getColor(R.color.view_color_circle_orange)) {
            imageButton.setImageResource(R.drawable.action_textcolor_orange);
        } else if (color == getResources().getColor(R.color.view_color_circle_violet)) {
            imageButton.setImageResource(R.drawable.action_textcolor_violet);
        } else if (color == getResources().getColor(R.color.view_color_circle_blue)) {
            imageButton.setImageResource(R.drawable.action_textcolor_blue);
        }
    }
}
