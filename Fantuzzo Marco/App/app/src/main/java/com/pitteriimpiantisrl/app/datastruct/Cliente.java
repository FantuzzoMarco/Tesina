package com.pitteriimpiantisrl.app.datastruct;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Utente on 03/05/2017.
 */

public class Cliente {

    private final int id;
    @NonNull
    private final String name, address, vatNumber;

    public Cliente(int id, @NonNull String name, @NonNull String address, @NonNull String vatNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.vatNumber = vatNumber;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    @NonNull
    public String getVatNumber() {
        return vatNumber;
    }

    public static Cliente getInstance(JSONObject o) throws JSONException {
        return new Cliente(o.getInt("id"), o.getString("nome"), o.getString("indirizzo"), o.getString("PIVA"));
    }

    public int getId() {
        return id;
    }
}
