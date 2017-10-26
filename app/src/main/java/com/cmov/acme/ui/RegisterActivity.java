package com.cmov.acme.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cmov.acme.R;
import com.cmov.acme.api.model.request.RegisterRequest;
import com.cmov.acme.api.model.response.RegisterResponse;
import com.cmov.acme.api.service.Register_service;
import com.cmov.acme.singletons.RetrofitSingleton;
import com.cmov.acme.singletons.User;
import com.cmov.acme.utils.ShowDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private Button register_button;
    private Retrofit retrofit;
    private ShowDialog dialog;
    private EditText input_name;
    private EditText input_username;
    private EditText input_password;
    private EditText input_nif;
    private EditText input_address;
    private EditText input_creditcardnumber;
    private EditText input_creditcardtype;
    private EditText input_creditcardvalidity;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dialog = new ShowDialog();
        
        retrofit = RetrofitSingleton.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        input_name = (EditText) findViewById(R.id.input_name);
        input_username = (EditText) findViewById(R.id.input_username);;
        input_password = (EditText) findViewById(R.id.input_password);;
        input_nif = (EditText) findViewById(R.id.input_nif);;
        input_address = (EditText) findViewById(R.id.input_address);;
        input_creditcardnumber = (EditText) findViewById(R.id.input_creditcardnumber);;
        input_creditcardtype = (EditText) findViewById(R.id.input_creditcardtype);;
        input_creditcardvalidity = (EditText) findViewById(R.id.input_creditcardvalidity);;

        register_button = (Button)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Register_service register_service = retrofit.create(Register_service.class);

                RegisterRequest request = new RegisterRequest(input_name.getText().toString()
                        ,input_username.getText().toString(),input_password.getText().toString(),input_nif.getText().toString()
                        ,input_address.getText().toString(),"123131 ",input_creditcardnumber.getText().toString(),input_creditcardtype.getText().toString(),
                        input_creditcardvalidity.getText().toString()); // TODO FALTA PUBLIC KEY

                Call<RegisterResponse> call = register_service.sendRegister(request);


                call.enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if(response.isSuccessful() && response.body().getToken() != null) {

                            User user =  User.getInstance();
                            user.setToken(response.body().getToken());
                            dialog.showDialog(RegisterActivity.this, user.getToken());
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            dialog.showDialog(RegisterActivity.this,"Unsuccessful register. Try again");
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        dialog.showDialog(RegisterActivity.this, "Unable to connect to  the server. Try again later.");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });
    }


}