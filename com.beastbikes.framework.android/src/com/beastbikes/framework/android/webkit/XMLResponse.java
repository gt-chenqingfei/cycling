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

public class XMLResponse extends WebResourceResponse {

    public XMLResponse(InputStream data) {
        super("application/xml", "utf-8", data);
    }

    public XMLResponse(AssetFileDescriptor afd) throws IOException {
        this(afd.createInputStream());
    }

    public XMLResponse(String xml) {
        this(new ByteArrayInputStream(xml.getBytes()));
    }

}
