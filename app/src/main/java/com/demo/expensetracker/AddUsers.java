package com.demo.expensetracker;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddUsers extends AppCompatActivity {
    EditText name, password;
    Button login;
    SharedPreferences sharedPreferences;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        name = findViewById(R.id.etName);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);

        sharedPreferences = getSharedPreferences("Userinfo", 0);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameValue = name.getText().toString();
                String passwordValue = password.getText().toString();

                if (nameValue.length()>1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", nameValue);
                    editor.putString("password", passwordValue);
                    editor.apply();

                    Toast.makeText(AddUsers.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                    i = new Intent(AddUsers.this, MainActivity.class);

                    startActivity(i);
                }
                else {
                    Toast.makeText(AddUsers.this, "Incorrect Name/Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}