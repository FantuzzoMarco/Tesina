package com.pitteriimpiantisrl.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pitteriimpiantisrl.app.R;
import com.pitteriimpiantisrl.app.datastruct.Cliente;

/**
 * Created by Utente on 10/05/2017.
 */

public class ClientView extends FrameLayout {
    public ClientView(Context context) {
        super(context);
    }

    public ClientView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClientView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final TextView name, vatNumber, address;

    //Sto estendendo il costruttore
    //Questo codice viene eseguito alla creazione dell'oggetto indipendentemente dal costruttore chiamato
    {
        View inflate = View.inflate(getContext(), R.layout.client_view, null);
        name = (TextView) inflate.findViewById(R.id.name);
        vatNumber = (TextView) inflate.findViewById(R.id.vatNumber);
        address = (TextView) inflate.findViewById(R.id.address);

        addView(inflate);
    }

    public void setCliente(Cliente c){
        name.setText(c.getName());
        vatNumber.setText(getContext().getString(R.string.vat_x, c.getVatNumber()));
        address.setText(getContext().getString(R.string.address_x, c.getAddress()));
    }
}
