package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpIOException;
import com.pitteriimpiantisrl.app.download.HttpParamType;

import java.io.IOException;

/**
 * Created by Utente on 27/05/2017.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText name, surname, cf, username, password;
    private Button register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_worker_dialog);

        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        cf = (EditText) findViewById(R.id.cf);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);

        new ButtonDisablerTextWatcher(register, new EditText[]{name, surname, cf, username, password});
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle(R.string.insert_special_password);
                final EditText editText = new EditText(RegisterActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(editText);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        register(editText.getText().toString());
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                AlertDialog dialog = builder.show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setEnabled(false);
                editText.addTextChangedListener(new ButtonDisablerTextWatcher(positiveButton, new EditText[]{editText}));
            }
        });


    }

    private enum RegistrationStatus {
        OK, INTERNAL_ERROR, USERNAME_TAKEN, WRONG_SPECIAL_PASSWORD;
    }

    private void register(final String specialPassword) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        final String paramNome = name.getText().toString();
        final String paramCognome = surname.getText().toString();
        final String paramCf = cf.getText().toString();
        final String paramUsername = username.getText().toString();
        final String paramPassword = password.getText().toString();

        new AsyncTask<Void, Void, RegistrationStatus>() {

            @Override
            protected RegistrationStatus doInBackground(Void... params) {
                try {
                    new HttpDownloader(Utils.SERVER_PATH + "/register.php")
                            .addParam("nome", paramNome, HttpParamType.POST)
                            .addParam("cognome", paramCognome, HttpParamType.POST)
                            .addParam("CF", paramCf, HttpParamType.POST)
                            .addParam("username", paramUsername, HttpParamType.POST)
                            .addParam("password", paramPassword, HttpParamType.POST)
                            .addParam("special_password", specialPassword, HttpParamType.POST)
                            .download();
                    return RegistrationStatus.OK;
                } catch (HttpIOException e) {
                    switch (e.getResponseCode()) {
                        case 403:
                            return RegistrationStatus.WRONG_SPECIAL_PASSWORD;
                        case 409:
                            return RegistrationStatus.USERNAME_TAKEN;
                        default:
                            return RegistrationStatus.INTERNAL_ERROR;
                    }
                } catch (IOException e) {
                    return RegistrationStatus.INTERNAL_ERROR;
                }
            }

            @Override
            protected void onPostExecute(RegistrationStatus result) {
                progressDialog.dismiss();
                switch (result) {
                    case OK:
                        Toast.makeText(RegisterActivity.this, R.string.registration_completed_successufully, Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                        sp.edit()
                                .putString("username", paramUsername)
                                .putString("password", paramPassword)
                                .apply();
                        Intent i = new Intent(RegisterActivity.this, DipendenteActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    case INTERNAL_ERROR:
                        Toast.makeText(RegisterActivity.this, R.string.registration_completed_successufully, Toast.LENGTH_SHORT).show();
                        break;
                    case USERNAME_TAKEN:
                        Toast.makeText(RegisterActivity.this, R.string.username_already_taken, Toast.LENGTH_SHORT).show();
                        break;
                    case WRONG_SPECIAL_PASSWORD:
                        Toast.makeText(RegisterActivity.this, R.string.wrong_special_password, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }.execute();
    }
}
