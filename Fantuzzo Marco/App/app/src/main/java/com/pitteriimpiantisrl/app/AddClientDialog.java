package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pitteriimpiantisrl.app.datastruct.Cliente;

import org.json.JSONObject;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpParamType;

/**
 * Created by Utente on 13/05/2017.
 */

public class AddClientDialog extends AlertDialog.Builder {

    public interface OnClientAddedListener {
        void onClientAdded(Cliente c);
    }

    private OnClientAddedListener onClientAddedListener;

    public void setOnClientAddedListener(OnClientAddedListener onClientAddedListener) {
        this.onClientAddedListener = onClientAddedListener;
    }

    public AddClientDialog(Context context) {
        super(context);
    }


    @Override
    public AlertDialog show() {
        View inflate = View.inflate(getContext(), R.layout.add_client_dialog, null);

        final EditText name = (EditText) inflate.findViewById(R.id.nameClient);
        final EditText address = (EditText) inflate.findViewById(R.id.addressClient);
        final EditText vatNumber = (EditText) inflate.findViewById(R.id.vatClient);

        setTitle(R.string.add_client);
        setView(inflate);

        setNegativeButton(android.R.string.cancel, null);
        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addClient(name.getText().toString(), address.getText().toString(), vatNumber.getText().toString());
            }
        });

        final AlertDialog ad = super.show();
        Button positiveButton = ad.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        new ButtonDisablerTextWatcher(positiveButton, new EditText[]{name, address, vatNumber});
        return ad;
    }

    private void addClient(final String name, final String address, final String vatNumber) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getContext().getString(R.string.loading));
        pd.show();
        new AsyncTask<Void, Void, Cliente>() {

            @Override
            protected Cliente doInBackground(Void... params) {
                try {
                    Object o = Utils.processHttpRequest(
                            getContext(),
                            new HttpDownloader(Utils.SERVER_PATH + "/addClient.php")
                                    .addParam("nome", name, HttpParamType.GET)
                                    .addParam("indirizzo", address, HttpParamType.GET)
                                    .addParam("PIVA", vatNumber, HttpParamType.GET)
                    );
                    return Cliente.getInstance((JSONObject) o);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Cliente result) {
                pd.dismiss();
                if (result != null) {
                    if (onClientAddedListener != null) {
                        onClientAddedListener.onClientAdded(result);
                    }
                    Toast.makeText(getContext(), R.string.client_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.error_adding_client, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
