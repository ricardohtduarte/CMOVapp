package com.cmov.acme.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cmov.acme.R;
import com.cmov.acme.api.model.response.PastTransactionsResponse;
import com.cmov.acme.api.model.response.ReceiptResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mauro on 30/10/2017.
 */

public class ReceiptAdapter extends ArrayAdapter<ReceiptResponse> {

    private Context context;
    private ArrayList<ReceiptResponse> list;
    ReceiptResponse itemPosition;

    public ReceiptAdapter(@NonNull Context context, ArrayList<ReceiptResponse> list) {
        super(context,0,list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ReceiptResponse itemPosition =  this.list.get(position);
        convertView = LayoutInflater.from(this.context).inflate(R.layout.custom_receipt, null);

        TextView recibo_id = (TextView) convertView.findViewById(R.id.text_recibo);
        recibo_id.setText("Transaction nº " + itemPosition.getReceipt_id());

        TextView date = (TextView) convertView.findViewById(R.id.text_date);
        String oldstring = itemPosition.getReceipt_date();
        Date newDate = null;
        String newstring = null;
        try {
            newDate = new SimpleDateFormat("yyyy-MM-dd").parse(oldstring);
            newstring = new SimpleDateFormat("EEE, MMM d, ''yy").format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText(newstring);

        Button button = (Button) convertView.findViewById(R.id.button_ver);
        button.setOnClickListener(handler);
        button.setTag(itemPosition.getReceipt_id());
        return convertView;
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(context, PastTransactions.class);
            intent.putExtra("id",v.getTag().toString());
            context.startActivity(intent);
            }};

}