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

public class HTMLResponse extends WebResourceResponse {

    public HTMLResponse(InputStream data) {
        super("text/html", "utf-8", data);
    }

    //RESOURCE_LEAK
//    public HTMLResponse(File html) throws FileNotFoundException {
//        this(new FileInputStream(html));
//    }

//    public HTMLResponse(FileDescriptor fd) {
//        this(new FileInputStream(fd));
//    }

    public HTMLResponse(AssetFileDescriptor afd) throws IOException {
        this(afd.createInputStream());
    }

//    public HTMLResponse(String data) {
//        this(new ByteArrayInputStream(data.getBytes()));
//    }

}
