package com.cmov.acme.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cmov.acme.R;
import com.cmov.acme.api.model.request.RegisterRequest;
import com.cmov.acme.api.model.response.RegisterResponse;
import com.cmov.acme.api.service.Register_service;
import com.cmov.acme.singletons.RetrofitSingleton;
import com.cmov.acme.singletons.User;
import com.cmov.acme.utils.Keygenerator;


import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RegisterActivity extends AppCompatActivity {

    private Button register_button;
    private Retrofit retrofit;
    private EditText input_name;
    private EditText input_username;
    private EditText input_password;
    private EditText input_nif;
    private EditText input_address;
    private EditText input_creditcardnumber;
    private EditText input_creditcardtype;
    private EditText input_creditcardvalidity;
    private View register;
    private ProgressBar progressBar;
    private String publicKey;
    private String privateKey;
    private KeyPair kp;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        retrofit = RetrofitSingleton.getInstance();
        register = findViewById(R.id.register_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        input_name = (EditText) findViewById(R.id.input_name);
        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        input_nif = (EditText) findViewById(R.id.input_nif);
        input_address = (EditText) findViewById(R.id.input_address);
        input_creditcardnumber = (EditText) findViewById(R.id.input_creditcardnumber);
        input_creditcardtype = (EditText) findViewById(R.id.input_creditcardtype);
        input_creditcardvalidity = (EditText) findViewById(R.id.input_creditcardvalidity);

        register_button = (Button)findViewById(R.id.change_button);
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showProgress(true);
                Register_service register_service = retrofit.create(Register_service.class);

                Keygenerator gen = new Keygenerator();
                publicKey = gen.getPublicKey();
                privateKey = gen.getPrivateKey();
                kp = gen.getKeyPair();
                RegisterRequest request = new RegisterRequest(input_name.getText().toString()
                        ,input_username.getText().toString(),input_password.getText().toString(),input_nif.getText().toString()
                        ,input_address.getText().toString(),publicKey,input_creditcardnumber.getText().toString(),input_creditcardtype.getText().toString(),
                        input_creditcardvalidity.getText().toString());

                Call<RegisterResponse> call = register_service.sendRegister(request);


                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if(response.isSuccessful() ) {
                            if(response.body().getToken() != null) {
                                User user = User.getInstance();
                                user.setKeyPair(publicKey, privateKey);
                                user.setToken(response.body().getToken());
                                user.setKp(kp);
                                Intent intent = new Intent(RegisterActivity.this, ShoppingCartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,"Invalid data / empty data", Toast.LENGTH_LONG).show();
                            showProgress(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this,"Unable to connect to server", Toast.LENGTH_LONG).show();
                        showProgress(false);
                    }
                });

            }
        });

        myCalendar = Calendar.getInstance();
         date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        input_creditcardvalidity.setKeyListener(null);

        input_creditcardvalidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });


    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        input_creditcardvalidity.setText(sdf.format(myCalendar.getTime()));
    };

    private void showProgress(final boolean show) {
        register.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(false);
    }


}
