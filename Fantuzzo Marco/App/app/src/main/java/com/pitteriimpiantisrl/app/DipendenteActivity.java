package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pitteriimpiantisrl.app.datastruct.Cliente;
import com.pitteriimpiantisrl.app.datastruct.Dipendente;
import com.pitteriimpiantisrl.app.datastruct.Lavoro;
import com.pitteriimpiantisrl.app.views.ClientView;
import com.pitteriimpiantisrl.app.views.JobView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpParamType;

public class DipendenteActivity extends AppCompatActivity implements SelectClientDialog.OnClientSelectedListener {

    private Dipendente dipendente;
    private Cliente selectedClient = null;

    private LinearLayout clientPart;
    private TextView workerTextView, cfTextView, usernameTextView, noClientSelected, totalHours;
    private ClientView clientView;
    private List<Lavoro> jobs = null;
    private ListView jobsListView;
    private BaseAdapter jobsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        workerTextView = (TextView) findViewById(R.id.dipendente);
        cfTextView = (TextView) findViewById(R.id.dipendenteCF);
        usernameTextView = (TextView) findViewById(R.id.dipendenteUsername);
        noClientSelected = (TextView) findViewById(R.id.noClientSelectedView);
        clientView = (ClientView) findViewById(R.id.selectedClientView);
        clientPart = (LinearLayout) findViewById(R.id.clientPart);
        totalHours = (TextView) findViewById(R.id.totalHours);
        jobsListView = (ListView) findViewById(R.id.jobsListView);

        jobsAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (jobs == null) {
                    return 0;
                } else {
                    return jobs.size();
                }
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
                final JobView ret;
                if (convertView!=null){
                    ret = (JobView) convertView;
                } else {
                    ret = new JobView(DipendenteActivity.this);
                }
                ret.setJob(jobs.get(position));
                return ret;
            }
        };
        jobsListView.setAdapter(jobsAdapter);
        jobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(DipendenteActivity.this)
                        .setMessage(jobs.get(position).getDescription())
                        .show();
            }
        });

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.show();

        new AsyncTask<Void, Void, Dipendente>() {

            @Override
            protected Dipendente doInBackground(Void... params) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DipendenteActivity.this);
                String username = sp.getString("username", "");
                String password = sp.getString("password", "");
                return Dipendente.getDipendente(DipendenteActivity.this, username, password);
            }

            @Override
            protected void onPostExecute(Dipendente dipendente) {
                dialog.dismiss();
                if (dipendente == null) {
                    //Error
                } else {
                    setDipendente(dipendente);
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dipendente_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.logout(this);
                break;
            default:
                return false;
        }
        return true;
    }

    private void setDipendente(Dipendente dipendente) {
        this.dipendente = dipendente;
        workerTextView.setText(getString(R.string.dipendente_x, dipendente.getCompleteName()));
        cfTextView.setText(getString(R.string.CF_x, dipendente.getCF()));
        usernameTextView.setText(getString(R.string.username_x, dipendente.getUsername()));
        setSelectedClient(null);
    }

    public void selectClient(View view) {
        SelectClientDialog selectClientDialog = new SelectClientDialog(this);
        selectClientDialog.setOnClientSelectedListener(this);
        selectClientDialog.show();
    }

    private void setSelectedClient(Cliente cliente) {
        this.selectedClient = cliente;
        if (cliente == null) {
            clientPart.setVisibility(View.GONE);
            noClientSelected.setVisibility(View.VISIBLE);

        } else {
            clientPart.setVisibility(View.VISIBLE);
            noClientSelected.setVisibility(View.GONE);
            clientView.setCliente(cliente);
            downloadJobs(cliente);
        }
    }

    private void downloadJobs(final Cliente cliente) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        new AsyncTask<Void, Void, List<Lavoro>>() {

            @Override
            protected List<Lavoro> doInBackground(Void... params) {
                try {
                    Object o = Utils.processHttpRequest(
                            DipendenteActivity.this,
                            new HttpDownloader(Utils.SERVER_PATH + "/getJobs.php")
                                    .addParam("cliente", cliente.getId(), HttpParamType.GET)
                    );
                    JSONArray response = (JSONArray) o;
                    ArrayList<Lavoro> ret = new ArrayList<Lavoro>(response.length());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject item = (JSONObject) response.get(i);
                        ret.add(Lavoro.getInstance(item, cliente));
                    }
                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Lavoro> lavoros) {
                progressDialog.dismiss();
                if (lavoros == null) {
                    Toast.makeText(DipendenteActivity.this, R.string.error_loading_jobs, Toast.LENGTH_SHORT).show();
                }
                jobs = lavoros;
                jobsAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onClientSelected(Cliente c) {
        setSelectedClient(c);
    }

    public void addJob(View view) {
        AddJobDialog ajd = new AddJobDialog(this, selectedClient);
        ajd.setOnJobAdded(new AddJobDialog.OnJobAdded() {
            @Override
            public void onJobAdded(Lavoro l) {
                Toast.makeText(DipendenteActivity.this, "Job added", Toast.LENGTH_SHORT).show();
                downloadJobs(selectedClient);
            }
        });
        ajd.show();
    }
}
