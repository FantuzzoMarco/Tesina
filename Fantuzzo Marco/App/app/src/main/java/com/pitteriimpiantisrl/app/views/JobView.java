package com.pitteriimpiantisrl.app.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pitteriimpiantisrl.app.R;
import com.pitteriimpiantisrl.app.datastruct.Lavoro;

/**
 * Created by Utente on 17/05/2017.
 */

public class JobView extends FrameLayout {
    public JobView(Context context) {
        super(context);
    }

    public JobView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JobView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private final TextView data, descrizione, prezzo, nOre;

    {
        View inflate = View.inflate(getContext(), R.layout.job_view, null);
        addView(inflate);
        data = (TextView) inflate.findViewById(R.id.date);
        descrizione = (TextView) inflate.findViewById(R.id.description);
        prezzo = (TextView) inflate.findViewById(R.id.prezzo);
        nOre = (TextView) inflate.findViewById(R.id.nOre);
    }

    public void setJob(Lavoro lavoro) {
        data.setText(getContext().getString(
                R.string.date_x,
                DateUtils.formatDateTime(
                        getContext(),
                        lavoro.getDate().getTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        ));
        descrizione.setText(getContext().getString(R.string.description_x, lavoro.getDescription()));
        prezzo.setText(getContext().getString(R.string.price_x, lavoro.getPrice()));
        nOre.setText(getContext().getString(R.string.numberOfHours_x, lavoro.getNumberOfHours()));
    }
}
