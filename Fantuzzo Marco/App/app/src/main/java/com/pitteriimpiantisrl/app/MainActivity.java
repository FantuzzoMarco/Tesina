package com.pitteriimpiantisrl.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpIOException;
import com.pitteriimpiantisrl.app.download.HttpParamType;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private Button login;
    private TextView forgot_credentials, not_registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.pw);
        login = (Button) findViewById(R.id.login);
        not_registered = (TextView) findViewById(R.id.ask_register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //Se ho già username e pasword salvati andiamo direttamente sula seconda activity
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.contains("username") && sp.contains("password")) {
            goToNextActivity();
        } else if (sp.contains("username")) {
            username.setText(sp.getString("username", ""));
        }

        not_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });


    }

    private void goToNextActivity() {
        finish();
        startActivity(new Intent(this, DipendenteActivity.class));
    }

    private void login() {
        final String username = this.username.getText().toString();
        final String password = this.password.getText().toString();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
        dialog.show();
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            //Eseguito nel thread secondario
            protected Boolean doInBackground(Void... params) {
                try {
                    new HttpDownloader(Utils.SERVER_PATH + "/login.php")
                            .addParam("login", username, HttpParamType.POST)
                            .addParam("password", password, HttpParamType.POST)
                            .download();
                    return true;
                } catch (HttpIOException e) {
                    e.printStackTrace();
                    if (e.getResponseCode() == 403) {
                        return false;
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            //eseguito nel thread principale
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result == null) {
                    //impossibile stabilire se login corretto
                    Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                } else if (result) {
                    //login ok
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    sp.edit()
                            .putString("username", username)
                            .putString("password", password)
                            .apply();
                    goToNextActivity();

                } else {
                    //login sbagliato
                    Toast.makeText(MainActivity.this, R.string.wrong_login, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }
}
