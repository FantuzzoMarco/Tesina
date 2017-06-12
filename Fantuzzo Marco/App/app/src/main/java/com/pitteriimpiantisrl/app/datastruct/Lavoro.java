package com.pitteriimpiantisrl.app.datastruct;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Utente on 13/05/2017.
 */

public class Lavoro {
    private final int id;
    @NonNull
    private final Date date;
    private final int numberOfHours;
    private final int price;
    @NonNull
    private final String description;
    private final int idDipendente;

    public Lavoro(int id, @NonNull Date date, int numberOfHours, int price, @NonNull String description, int idDipendente) {
        this.id = id;
        this.date = date;
        this.numberOfHours = numberOfHours;
        this.price = price;
        this.description = description;
        this.idDipendente = idDipendente;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public int getNumberOfHours() {
        return numberOfHours;
    }

    public int getPrice() {
        return price;
    }

    public int getIdDipendente() {
        return idDipendente;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static Lavoro getInstance(JSONObject o, Cliente c) throws JSONException, ParseException {
        return new Lavoro(
                o.getInt("id"),
                DATE_FORMAT.parse(o.getString("data")),
                o.getInt("numeroOre"),
                o.getInt("prezzo"),
                o.getString("descrizione"),
                o.getInt("dipendente"));
    }
}
