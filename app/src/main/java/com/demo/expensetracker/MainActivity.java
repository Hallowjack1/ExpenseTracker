package com.demo.expensetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView Dashboard, tvBudget, tvExpense;
    Button budgetAct, expenseAct;

    public static final String BGTAMTM = "BGTAMTM";

    public static final String EXPAMTM = "EXPAMTM";

    private String bgtamt, expamt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dashboard = findViewById(R.id.tvDashboard);
        budgetAct = findViewById(R.id.btnBudgetAct);
        expenseAct = findViewById(R.id.btnExpenseAct);
        tvBudget = findViewById(R.id.tv_budget);
        tvExpense = findViewById(R.id.tv_expense);

        sharedPreferences = getSharedPreferences("Userinfo", 0);
        String sname = sharedPreferences.getString("name", "");
        Dashboard.setText(sname + "'s Dashboard");

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;


        Intent i = getIntent();

        bgtamt = i.getStringExtra(BGTAMTM);

        expamt = i.getStringExtra(EXPAMTM);


        tvBudget.setText("₱ " + bgtamt);
        tvExpense.setText("₱ " + expamt);

        budgetAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Budget.class);
                startActivity(i);
            }
        });

        expenseAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Expenses.class);
                startActivity(i);
            }
        });
    }
}