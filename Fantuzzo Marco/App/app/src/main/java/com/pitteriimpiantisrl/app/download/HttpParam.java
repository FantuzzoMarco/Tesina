/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pitteriimpiantisrl.app.download;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Parametro da inviare con la richiesta HTTP
 */
public class HttpParam {

    private final String name;
    private final String encodedName;
    private final HttpParamType type;
    private final Object value;
    private final String encodedValue;


    public HttpParam(String name, HttpParamType type, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name null");
        } else if (type == null) {
            throw new IllegalArgumentException("type == null");
        } else if(value == null) {
            throw new IllegalArgumentException("value == null");
        }
        this.name = name;
        this.encodedName = encode(name);
        this.type = type;
        this.value = value;
        this.encodedValue = encode(String.valueOf(value));
    }

    protected static String encode(final String toEncode) {
        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public String getEncodedName() {
        return encodedName;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public String getQueryString() {
        return getEncodedName() + "=" + getEncodedValue();
    }

    public HttpParamType getType() {
        return type;
    }

    public boolean isGet() {
        return type == HttpParamType.GET;
    }

    public boolean isPost() {
        return type == HttpParamType.POST;
    }

    //AUTOGENERATO
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.name.hashCode();
        hash = 83 * hash + (this.type.ordinal());
        return hash;
    }

    //AUTOGENERATO
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HttpParam)) {
            return false;
        }
        final HttpParam other = (HttpParam) obj;
        return other.name.equals(other.name) || this.type != other.type;
    }

}
