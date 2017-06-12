package com.pitteriimpiantisrl.app.datastruct;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pitteriimpiantisrl.app.Utils;

import org.json.JSONObject;

import com.pitteriimpiantisrl.app.download.HttpDownloader;

/**
 * Created by Utente on 03/05/2017.
 */

public class Dipendente {

    @NonNull
    private final String nome;
    @NonNull
    private final String cognome;
    @NonNull
    private final String CF;
    @NonNull
    private final String username;


    public Dipendente(@NonNull String nome, @NonNull String cognome, @NonNull String cf, @NonNull String username) {
        this.nome = nome;
        this.cognome = cognome;
        CF = cf;
        this.username = username;
    }

    @NonNull
    public String getNome() {
        return nome;
    }

    @NonNull
    public String getCognome() {
        return cognome;
    }

    @NonNull
    public String getCF() {
        return CF;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public static Dipendente getDipendente(Context c, String username, String password){
        try {
            Object o = Utils.processHttpRequest(c,  new HttpDownloader(Utils.SERVER_PATH + "/getDipendente.php"));
            if(o==null){
                return null;
            } else{
                JSONObject json = (JSONObject) o;
                return new Dipendente(json.getString("nome"), json.getString("cognome"), json.getString("CF"), json.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCompleteName() {
        return nome +" "+ cognome;
    }
}
