package com.pitteriimpiantisrl.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pitteriimpiantisrl.app.datastruct.Cliente;
import com.pitteriimpiantisrl.app.datastruct.Lavoro;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pitteriimpiantisrl.app.download.HttpDownloader;
import com.pitteriimpiantisrl.app.download.HttpParamType;

/**
 * Created by Utente on 13/05/2017.
 */

public class AddJobDialog extends AlertDialog.Builder {

    public interface OnJobAdded {
        void onJobAdded(Lavoro l);
    }

    private OnJobAdded onJobAdded;

    public void setOnJobAdded(OnJobAdded onJobAdded) {
        this.onJobAdded = onJobAdded;
    }

    private final Cliente cliente;

    public AddJobDialog(Context context, Cliente cliente) {
        super(context);
        this.cliente = cliente;
    }

    @Override
    public AlertDialog show() {
        View inflate = View.inflate(getContext(), R.layout.add_job_dialog, null);

        final TextView date = (TextView) inflate.findViewById(R.id.date);
        final Button selectDate = (Button) inflate.findViewById(R.id.selectDate);
        final EditText numberHours = (EditText) inflate.findViewById(R.id.numberOfHours);
        final EditText price = (EditText) inflate.findViewById(R.id.price);
        final EditText description = (EditText) inflate.findViewById(R.id.description);

        //Restituisce calendario con data corrente
        date.setTag(Calendar.getInstance());
        invalidateDate(date);

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar previousDate = (Calendar) date.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final DatePicker dp = new DatePicker(getContext());
                dp.updateDate(previousDate.get(Calendar.YEAR), previousDate.get(Calendar.MONTH), previousDate.get(Calendar.DAY_OF_MONTH));
                builder.setView(dp);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                        final TimePicker tp = new TimePicker(getContext());
                        //noinspection deprecation
                        tp.setCurrentHour(previousDate.get(Calendar.HOUR_OF_DAY));
                        //noinspection deprecation
                        tp.setCurrentMinute(previousDate.get(Calendar.MINUTE));
                        tp.setIs24HourView(true);
                        builder2.setView(tp);
                        builder2.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, dp.getYear());
                                cal.set(Calendar.MONTH, dp.getMonth());
                                cal.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                                //noinspection deprecation
                                cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                                //noinspection deprecation
                                cal.set(Calendar.MINUTE, tp.getCurrentMinute());
                                date.setTag(cal);
                                invalidateDate(date);
                            }
                        });
                        builder2.setNegativeButton(android.R.string.cancel, null);
                        builder2.show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.show();
            }
        });

        setView(inflate);
        setTitle(R.string.add_job);

        setNegativeButton(android.R.string.cancel, null);
        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addJob(((Calendar) date.getTag()).getTime(), numberHours.getText().toString(), price.getText().toString(), description.getText().toString());
            }
        });
        final AlertDialog ad = super.show();
        Button positiveButton = ad.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        new ButtonDisablerTextWatcher(positiveButton, new EditText[]{numberHours, price, description});
        return ad;
    }

    private void invalidateDate(TextView date) {
        date.setText(DateUtils.formatDateTime(
                getContext(),
                ((Calendar) date.getTag()).getTime().getTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
        ));
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private void addJob(final Date date, final String numberOfHours, final String price, final String description) {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getContext().getString(R.string.loading));
        pd.show();
        new AsyncTask<Void, Void, Lavoro>() {

            @Override
            protected Lavoro doInBackground(Void... params) {
                try {
                    Object o = Utils.processHttpRequest(
                            getContext(),
                            new HttpDownloader(Utils.SERVER_PATH + "/addJob.php")
                                    .addParam("data", DATE_FORMAT.format(date), HttpParamType.GET)
                                    .addParam("numeroOre", numberOfHours, HttpParamType.GET)
                                    .addParam("cliente", cliente.getId(), HttpParamType.GET)
                                    .addParam("prezzo", price, HttpParamType.GET)
                                    .addParam("descrizione", description, HttpParamType.GET)
                    );
                    return Lavoro.getInstance((JSONObject) o, cliente);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Lavoro lavoro) {
                pd.dismiss();
                if (lavoro == null) {
                    Toast.makeText(getContext(), R.string.error_adding_job, Toast.LENGTH_SHORT).show();
                } else {
                    if (onJobAdded != null) {
                        onJobAdded.onJobAdded(lavoro);
                    }
                }
            }
        }.execute();
    }
}
