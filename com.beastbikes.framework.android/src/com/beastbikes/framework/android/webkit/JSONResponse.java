package com.beastbikes.framework.android.webkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.AssetFileDescriptor;
import android.webkit.WebResourceResponse;

public class JSONResponse extends WebResourceResponse {

    public JSONResponse(InputStream data) {
        super("application/json", "utf-8", data);
    }

    public JSONResponse(AssetFileDescriptor afd) throws IOException {
        this(afd.createInputStream());
    }

    public JSONResponse(String json) {
        this(new ByteArrayInputStream(json.getBytes()));
    }

    public JSONResponse(JSONObject json) {
        this(json.toString());
    }

    public JSONResponse(JSONArray json) {
        this(json.toString());
    }

    public JSONResponse(Map<?, ?> json) {
        this(new JSONObject(json));
    }

}
