package com.beastbikes.framework.android.webkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetFileDescriptor;
import android.webkit.WebResourceResponse;

public class JavaScriptResponse extends WebResourceResponse {

    public JavaScriptResponse(InputStream data) {
        super("application/javascript", "utf-8", data);
    }

    public JavaScriptResponse(AssetFileDescriptor afd) throws IOException {
        this(afd.createInputStream());
    }

    public JavaScriptResponse(String text) {
        this(new ByteArrayInputStream(text.getBytes()));
    }

}
