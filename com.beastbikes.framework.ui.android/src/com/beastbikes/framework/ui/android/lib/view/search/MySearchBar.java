package com.beastbikes.framework.ui.android.lib.view.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.beastbikes.framework.ui.android.R;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;

/**
 * NOTE: when use this control, if it will be in TabHost, make sure NOT set its'
 */
public class MySearchBar extends RelativeLayout {

    /**
     * 用来创建view
     */
    protected Context context;
    private MyAutoCompleteTextView autoCompleteTextView;
    private ImageView btnSearch;
    private Button searchButton;

    private MySearchListener searchListener;
    protected BaseSearchBarAdapter adapter;

    public MySearchBar(Context context, String btnSearchText, String hint) {
        super(context);
        initViews(context, hint, btnSearchText);
    }

    public MySearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, null, null);
    }

    private boolean searchBtnVisible = false;

    public void setSearchButtonVisivible(Boolean visible) {
        searchBtnVisible = visible;
    }

    /**
     * 设置搜索的所有监听，并生成adapter设置到autotextview
     *
     * @param barListener
     */
    public void setSearchBarListener(MySearchListener barListener) {
        this.searchListener = barListener;
        this.adapter = new DefaultSearchAdapter(context, searchListener);
        this.autoCompleteTextView.setAdapter(adapter);
    }

    @SuppressLint("InflateParams")
    private void initViews(Context context, String hint, String btnSearchText) {
        this.context = context;
        this.setBackgroundColor(0xefeef4);

        RelativeLayout layout = new RelativeLayout(getContext());
        layout.setId(1111);
        this.setBackgroundResource(R.drawable.bg_search);

        RelativeLayout.LayoutParams pSearch = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(
                context, RelativeLayout.LayoutParams.WRAP_CONTENT));
        pSearch.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        pSearch.addRule(RelativeLayout.CENTER_VERTICAL);
        pSearch.leftMargin = DensityUtil.dip2px(10, getContext());
        btnSearch = new ImageView(context);
        btnSearch.setId(R.id.btn_search);
        btnSearch.setBackgroundResource(R.drawable.ic_search_default);
        btnSearch.setPadding(0, 0, 0, 0);

        searchButton = new Button(getContext());
        searchButton.setId(R.id.btn_voice);
        searchButton.setBackgroundResource(R.drawable.search_btn_bg);
        searchButton.setText(TextUtils.isEmpty(btnSearchText) ? context
                .getString(R.string.search_btn_text_default) : btnSearchText);
        searchButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        searchButton.setTextColor(0xff4995dd);
        RelativeLayout.LayoutParams pSearchButton = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(
                30, getContext()));
        pSearchButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        pSearchButton.addRule(RelativeLayout.CENTER_VERTICAL);
        pSearchButton.rightMargin = DensityUtil.dip2px(5, getContext());

        RelativeLayout.LayoutParams pTv = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pTv.leftMargin = DensityUtil.dip2px(10, getContext());
        pTv.addRule(RelativeLayout.RIGHT_OF, R.id.btn_search);
        pTv.addRule(RelativeLayout.CENTER_VERTICAL);

        autoCompleteTextView = (MyAutoCompleteTextView) /*
                                                         * new
														 * MyAutoCompleteTextView
														 * (context);
														 */LayoutInflater.from(
                context).inflate(R.layout.layout_searchbar, null);
        autoCompleteTextView.setId(R.id.auto_complete);
        autoCompleteTextView.setDropDownWidth(DensityUtil
                .getWidth(getContext()));
        autoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        autoCompleteTextView.setBackgroundColor(getResources().getColor(
                android.R.color.transparent));
        autoCompleteTextView.setHint(TextUtils.isEmpty(hint) ? context
                .getString(R.string.search_hint_default) : hint);
        autoCompleteTextView.setSingleLine(true);
        autoCompleteTextView.setPadding(0, DensityUtil.dip2px(context, 5), 0,
                DensityUtil.dip2px(context, 5));
        autoCompleteTextView.setHintTextColor(0xff777777);
        autoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        autoCompleteTextView.setTextColor(getResources().getColor(
                android.R.color.white));
        autoCompleteTextView.setDropDownBackgroundDrawable(new ColorDrawable(
                Color.WHITE));

        autoCompleteTextView.setDropDownVerticalOffset(DensityUtil.dip2px(30,
                getContext()));

        this.addView(searchButton, pSearchButton);

        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
        layout.addView(autoCompleteTextView, pTv);
        layout.addView(btnSearch, pSearch);

        RelativeLayout.LayoutParams play = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        play.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        play.addRule(RelativeLayout.CENTER_VERTICAL);
        play.addRule(RelativeLayout.LEFT_OF, R.id.btn_voice);

        this.addView(layout, play);
        searchButton.setVisibility(View.GONE);

        btnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                collapseSoftInputMethod();
                if (searchListener != null) {
                    String keyword = autoCompleteTextView.getText().toString()
                            .trim();
                    if (!TextUtils.isEmpty(keyword)) {
                        searchListener.recordHistory(
                                searchListener.getSearchKey(), keyword);
                    }
                    searchListener.goSearch(searchListener.getSearchKey(),
                            keyword);
                }

            }
        });

        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    String keyword = autoCompleteTextView.getText().toString()
                            .trim();
                    searchListener.goSearch(searchListener.getSearchKey(),
                            keyword);
                }

            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (TextUtils.isEmpty(autoCompleteTextView.getText()
                        .toString().trim())) {
                    searchButton.setVisibility(View.GONE);
                    searchListener.clearHistory(null);
                } else {
                    if (searchBtnVisible) {
                        searchButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (searchListener != null) {
                    if (adapter.isClear(position)) {
                        searchListener.clearHistory(searchListener
                                .getSearchKey());
                        return;
                    }
                    if (adapter.isShowHistory()) {
                        searchListener.onHistoryItemClicked(adapter
                                .getItem(position));
                    } else {
                        searchListener.onIntelligenceItemClicked(adapter
                                .getItem(position));
                    }
                }

            }
        });

        autoCompleteTextView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    collapseSoftInputMethod();
                    if (searchListener != null) {
                        String keyword = autoCompleteTextView.getText()
                                .toString().trim();
                        searchListener.recordHistory(
                                searchListener.getSearchKey(), keyword);
                        searchListener.goSearch(searchListener.getSearchKey(),
                                keyword);
                    }
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    public void setHint(String text) {
        autoCompleteTextView.setHint(text);
    }

    public void setThreshold(int threshold) {
        autoCompleteTextView.setThreshold(threshold);
    }

    public void setDropDownAnchor(int id) {
        autoCompleteTextView.setDropDownAnchor(this.getId());
    }

    public void setDropDownVerticalOffset(int offset) {
        autoCompleteTextView.setDropDownVerticalOffset(offset);
    }

    public void setDropDownWidth(int width) {
        autoCompleteTextView.setDropDownWidth(width);
    }

    public void setDropDownHeight(int height) {
        autoCompleteTextView.setDropDownHeight(height);
    }

    public void collapseSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);

    }

    public MyAutoCompleteTextView getTextView() {
        return autoCompleteTextView;
    }

    public void setBGResource(int drawable) {
        this.setBackgroundResource(drawable);
    }

}
