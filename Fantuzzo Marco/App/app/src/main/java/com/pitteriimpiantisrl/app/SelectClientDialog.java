package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pitteriimpiantisrl.app.datastruct.Cliente;
import com.pitteriimpiantisrl.app.views.ClientView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpParamType;

/**
 * Created by Utente on 06/05/2017.
 */

public class SelectClientDialog extends AlertDialog.Builder implements TextWatcher {

    //interfaccia per rilevare quale client è stato selezionato
    public interface OnClientSelectedListener {
        void onClientSelected(Cliente c);
    }

    private OnClientSelectedListener onClientSelectedListener;

    public void setOnClientSelectedListener(OnClientSelectedListener onClientSelectedListener) {
        this.onClientSelectedListener = onClientSelectedListener;
    }

    public SelectClientDialog(Context context) {
        super(context);
    }

    private List<Cliente> clients = null;
    private ListView listView;
    private ProgressBar progressBar;
    private EditText editText;
    private TextView message;

    @Override
    public AlertDialog show() {
        setTitle(R.string.select_client);
        LinearLayout main = (LinearLayout) View.inflate(getContext(), R.layout.select_client_dialog, null);
        progressBar = (ProgressBar) main.findViewById(R.id.progressBar);
        editText = (EditText) main.findViewById(R.id.searchEditText);
        message = (TextView) main.findViewById(R.id.message);
        editText.addTextChangedListener(this);
        listView = (ListView) main.findViewById(R.id.clientList);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return clients == null ? 0 : clients.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ClientView ret;
                if (convertView == null) {
                    ret = new ClientView(getContext());
                } else {
                    ret = (ClientView) convertView;
                }
                ret.setCliente(clients.get(position));
                return ret;
            }
        });
        setView(main);
        setNegativeButton(android.R.string.cancel, null);
        setNeutralButton(R.string.add_client, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Richiama la classe AddClientDialog per nuove registrazioni
                AddClientDialog acd = new AddClientDialog(getContext());
                acd.setOnClientAddedListener(new AddClientDialog.OnClientAddedListener() {
                    @Override
                    public void onClientAdded(Cliente c) {
                        if(onClientSelectedListener!=null){
                            onClientSelectedListener.onClientSelected(c);
                        }
                    }
                });
                acd.show();
            }
        });
        final AlertDialog dialog = super.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onClientSelectedListener != null) {
                    onClientSelectedListener.onClientSelected(clients.get(position));
                    dialog.dismiss();
                }
            }
        });
        return dialog;
    }

    private void search(final String query) {
        new AsyncTask<Void, Void, List<Cliente>>() {

            //richiamato dal thread principale
            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            //richiamato da un thread diverso
            @Override
            protected List<Cliente> doInBackground(Void... params) {
                try {
                    Object o = Utils.processHttpRequest(
                            getContext(),
                            new HttpDownloader(Utils.SERVER_PATH + "/getClients.php")
                                    .addParam("query", query, HttpParamType.GET)

                    );
                    if (o != null) {
                        System.out.println(o);
                        JSONArray result = (JSONArray) o;
                        ArrayList<Cliente> ret = new ArrayList<>(result.length());
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = (JSONObject) result.get(i);
                            ret.add(Cliente.getInstance(item));
                        }
                        return ret;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            //richiamato dal thread principale
            @Override
            protected void onPostExecute(List<Cliente> clientes) {
                //Controllo se la ricerca è ancora "valida" (Se la strnga di ricerca non è cambiata)
                if (query.equals(editText.getText().toString())) {
                    //Nascondo la progress bar
                    progressBar.setVisibility(View.GONE);

                    //Scrivo eventuale messaggio
                    if (clientes == null) {
                        message.setText(R.string.error_searching_clients);
                        message.setVisibility(View.VISIBLE);
                    } else {
                        if (clientes.isEmpty()) {
                            message.setText(R.string.no_client_found);
                            message.setVisibility(View.VISIBLE);
                        } else {
                            message.setVisibility(View.GONE);
                        }
                    }

                    //Imposto il nuovo risultato di ricerca
                    SelectClientDialog.this.clients = clientes;
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }
            }
        }.execute();


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        search(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
