package com.pitteriimpiantisrl.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.IOException;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpIOException;
import com.pitteriimpiantisrl.app.download.HttpParamType;

/**
 * Created by Utente on 06/05/2017.
 */

public class Utils {

    public static final String SERVER_PATH = "http://pitteriapp.altervista.org";

    public static Object processHttpRequest(Context c, HttpDownloader httpDownloader) throws IOException, InterruptedException, JSONException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        try {
            final String username = sp.getString("username", "");
            final String password = sp.getString("password", "");

            String response = httpDownloader
                    .addParam("login", username, HttpParamType.POST)
                    .addParam("password", password, HttpParamType.POST)
                    .downloadString();
            return new JSONTokener(response).nextValue();
        } catch (HttpIOException e) {
            if (e.getResponseCode() == 403) {
                logout(c);
                return null;
            } else {
                throw e;
            }
        }
    }

    public static void logout(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("password").apply();
        context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
