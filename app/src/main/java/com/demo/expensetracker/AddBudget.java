package com.demo.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddBudget extends AppCompatActivity {
Button addBudget;
EditText inputBudget, BudgetDesc;
TextView tvBudget;
Intent i;

    private static JSONParser jParser = new JSONParser();
    private static String urlHost = "http://192.168.254.104/ExpenseTracker/AddBudget.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String budgetamt = "";
    private static String budgetdesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        addBudget = findViewById(R.id.btnAddBudget);
        BudgetDesc = findViewById(R.id.etDesc2);
        inputBudget = findViewById(R.id.etAddBudget);
        tvBudget = findViewById(R.id.tvBudget);

        addBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                budgetamt = inputBudget.getText().toString().trim();
                budgetdesc = BudgetDesc.getText().toString().trim();

                i = new Intent(AddBudget.this, MainActivity.class);

                new uploadDataToURL().execute();

                startActivity(i);
                finish();
            }
        });
    }
        private class uploadDataToURL extends AsyncTask<String, String, String> {
            String CPOST = "", cPostSQL = "", cMessage = "Querying data...";
            int nPostValueIndex;
            ProgressDialog pDialog = new ProgressDialog(AddBudget.this);

            public uploadDataToURL() {
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.setMessage(cMessage);
                pDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                int nSuccess;
                try {
                    ContentValues cv = new ContentValues();
                    //insert anything in this code

                    cPostSQL = " '" + budgetamt + "' , '" + budgetdesc + "' ";
                    cv.put("code", cPostSQL);


                    JSONObject json = jParser.makeHTTPRequest(urlHost, "POST", cv);
                    if (json != null) {
                        nSuccess = json.getInt(TAG_SUCCESS);
                        if (nSuccess == 1) {
                            online_dataset = json.getString(TAG_MESSAGE);
                            return online_dataset;
                        } else {
                            return json.getString(TAG_MESSAGE);
                        }
                    } else {
                        return "HTTPSERVER_ERROR";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pDialog.dismiss();
                String isEmpty = "";
                AlertDialog.Builder alert = new AlertDialog.Builder(AddBudget.this);
                if (s != null) {
                    if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) {
                    }
                    Toast.makeText(AddBudget.this, s, Toast.LENGTH_SHORT).show();
                } else {
                    alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                    alert.setTitle("Error");
                    alert.show();
                }
            }
        }
}