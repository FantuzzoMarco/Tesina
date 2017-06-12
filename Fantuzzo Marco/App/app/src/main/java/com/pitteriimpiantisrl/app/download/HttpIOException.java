/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pitteriimpiantisrl.app.download;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpIOException extends IOException {

    private final int responseCode;

    public HttpIOException(HttpURLConnection connection) throws IOException {
        super("Error downloading " + connection.getURL() + " - Response code: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        this.responseCode = connection.getResponseCode();
    }

    public HttpIOException(HttpURLConnection connection, Throwable cause) throws IOException {
        super("Error downloading " + connection.getURL() + " - Response code: " + connection.getResponseCode() + " " + connection.getResponseMessage(), cause);
        this.responseCode = connection.getResponseCode();
    }

    public int getResponseCode() {
        return responseCode;
    }

}
