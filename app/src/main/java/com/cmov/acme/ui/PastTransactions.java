package com.cmov.acme.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
    import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmov.acme.R;
import com.cmov.acme.adapters.PastTransactionsAdapter;

import com.cmov.acme.api.model.response.PastTransactionsResponse;
import com.cmov.acme.api.service.PastTransactions_service;
import com.cmov.acme.singletons.RetrofitSingleton;
import com.cmov.acme.singletons.User;


import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PastTransactions extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private Retrofit retrofit;
    private ArrayList<PastTransactionsResponse> listaResposta;
    private ListView listView;
    private String date;
    private String id;
    private String token;
    private TextView totalPrice;
    private ProgressBar progressBar;
    private View transactionsview;
    private Button print;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_transactions_activity_drawer);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.getString("date");
            id = extras.getString("id");
            token = extras.getString("token");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        print = (Button) findViewById(R.id.print_button);
        print.setOnClickListener(printHandler);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listaResposta = new ArrayList<PastTransactionsResponse>();

        transactionsview = findViewById(R.id.transactionview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        listView = (ListView) findViewById(R.id.listview);

        TextView name = (TextView) findViewById(R.id.text_compra);
        name.setText(date);
        TextView token_text = (TextView) findViewById(R.id.text_token);
        token_text.setText(token);



        totalPrice = (TextView) findViewById(R.id.text_price);

        showProgress(true);

        retrofit = RetrofitSingleton.getInstance();
        PastTransactions_service transactions_service = retrofit.create(PastTransactions_service.class);
        Call<ArrayList<PastTransactionsResponse>> call = transactions_service.getPastTransactions("Bearer "+ User.getInstance().getToken(),id);

        call.enqueue(new Callback<ArrayList<PastTransactionsResponse>> () {
            @Override
            public void onResponse(Call<ArrayList<PastTransactionsResponse>> call, Response<ArrayList<PastTransactionsResponse>> response) {
                if(response.isSuccessful() ) {
                    listaResposta = response.body();
                    PastTransactionsAdapter adapter = new PastTransactionsAdapter(PastTransactions.this, listaResposta);
                    listView.setAdapter(adapter);

                    double price = 0;
                    for(PastTransactionsResponse r : listaResposta) {
                        price += Double.parseDouble(r.getPrice())*Double.parseDouble(r.getQuantity());
                    }
                    totalPrice.setText("Total Price: " + price+"€");
                    showProgress(false);

                } else {
                    Toast.makeText(PastTransactions.this,response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }


            @Override
            public void onFailure(Call<ArrayList<PastTransactionsResponse>>  call, Throwable t) {
                Toast.makeText(PastTransactions.this,"Unable to connect to server", Toast.LENGTH_LONG).show();
                finish();

            }

        });

    }


    View.OnClickListener printHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(PastTransactions.this, NFC_send.class);
            intent.putExtra("token", token);
            startActivity(intent);
        }
    };

    private void showProgress(final boolean show) {
        transactionsview.setVisibility(show ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shopping_cart) {
            Intent intent = new Intent(PastTransactions.this, ShoppingCartActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.past_transactions) {
            Intent intent = new Intent(PastTransactions.this, ReceiptsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.account) {
            Intent intent = new Intent(PastTransactions.this, AccountActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {

            Intent intent = new Intent(PastTransactions.this, LoginActivity.class);
            if(User.getInstance().getAdapter() != null)
                User.getInstance().getAdapter().reset_products();
            User user = User.getInstance();
            user.deleteInstance();

            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
