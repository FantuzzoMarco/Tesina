/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pitteriimpiantisrl.app.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;

/**
 * Classe per effettuare una richiesta Http
 * Non utilizza thread, la richiesta è completamente sincrona
 */
public class HttpDownloader {

    public static final int DEFAULT_TIMEOUT = 30000;

    private final String url;
    private final Set<HttpParam> params = new HashSet<>();
    private final Map<String, String> headers = new HashMap<>();
    private int timeout = DEFAULT_TIMEOUT;

    public HttpDownloader(String url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public HttpDownloader addParam(final HttpParam param) {
        this.params.add(param);
        return this;
    }

    public HttpDownloader addParam(final String name, final Object value, final HttpParamType type) {
        addParam(new HttpParam(name, type, value));
        return this;
    }

    public HttpDownloader putHeader(final String name, final String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name null or empty");
        } else if (value == null) {
            throw new IllegalArgumentException("value == null");
        } else {
            headers.put(name, value);
        }
        return this;
    }

    public HttpDownloader setTimeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getTimeout() {
        return this.timeout;
    }

    private String getQuery(HttpParamType type) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (HttpParam param : params) {
            if (param.getType() == type) {
                if (!first) {
                    sb.append("&");
                } else {
                    first = false;
                }
                sb.append(param.getQueryString());
            }
        }
        if (first) {
            return null;
        } else {
            return sb.toString();
        }
    }

    private URL getUrlWithQuery() throws MalformedURLException {
        final StringBuilder sb = new StringBuilder(url);
        final String query = getQuery(HttpParamType.GET);
        if (query != null) {
            sb.append('?').append(query);
        }
        return new URL(sb.toString());
    }

    public boolean hasPostParams() {
        for (HttpParam param : params) {
            if (param.isPost()) {
                return true;
            }
        }
        return false;
    }


    public HttpURLConnection download() throws HttpIOException, IOException {
        URL url = getUrlWithQuery();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        for (Map.Entry<String, String> entrySet : headers.entrySet()) {
            conn.setRequestProperty(entrySet.getKey(), entrySet.getValue());
        }

        conn.setDoInput(true);
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);

        try {
            if (hasPostParams()) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                try (final DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
                    outputStream.writeBytes(getQuery(HttpParamType.POST));
                    outputStream.flush();
                }
            }

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new HttpIOException(conn);
            } else {
                return conn;
            }
        } catch (IOException e) {
            final int responseCode = conn.getResponseCode();
            if (responseCode == -1) {
                throw e;
            } else {
                throw new HttpIOException(conn, e);
            }
        }
    }

    public InputStream downloadInputStream() throws IOException {
        return new BufferedInputStream(download().getInputStream());
    }

    public String downloadString() throws IOException {
        return fromInputStream(downloadInputStream());
    }

    private static String fromInputStream(InputStream is) throws IOException {
        final byte[] buffer =  new byte[1024];
        int readCount;
        StringBuilder sb = new StringBuilder();
        while ((readCount = is.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, readCount));
        }
        return sb.toString();
    }

}
