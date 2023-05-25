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

public class EditBudget extends AppCompatActivity {
    Button btnEditBudget;
    private static EditText Amount,Description;
    private static String cItemcode = "";
    private static JSONParser jParser = new JSONParser();
    private static String urlHost = "http://192.168.254.106/ExpenseTracker/UpdateBudget.php";
    private static String TAG_MESSAGE = "message" , TAG_SUCCESS = "success";
    private static String online_dataset = "";
    public static String String_isempty = "";
    public static final String BGTAMT = "BGTAMT";
    public static final String BGTDESC = "BGTDESC";
    public static final String BGTID = "BGTID";

    private String bgtaydi,bgtamt,bgtdesc;

    public static String BudgetAmount = "";
    public static String BudgetDescription = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        btnEditBudget = findViewById(R.id.btnEditBudget);
        Amount = findViewById(R.id.etAmount3);
        Description = findViewById(R.id.etDesc4);

        Intent i = getIntent();
        bgtaydi = i.getStringExtra(BGTID);
        bgtamt = i.getStringExtra(BGTAMT);
        bgtdesc = i.getStringExtra(BGTDESC);

        Amount.setText(bgtamt);
        Description.setText(bgtdesc);

        btnEditBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BudgetAmount = Amount.getText().toString();
                BudgetDescription = Description.getText().toString();

                new uploadDataToURL().execute();

                Intent i = new Intent(EditBudget.this, MainActivity.class);

                i.putExtra(MainActivity.BGTAMTM, BudgetAmount);

                startActivity(i);
                finish();
            }
        });
    }

    private class uploadDataToURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(EditBudget.this);

        public uploadDataToURL() { }
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

                cPostSQL = bgtaydi;
                cv.put("id", cPostSQL);

                cPostSQL = " '" + BudgetAmount + "' ";
                cv.put("budget", cPostSQL);

                cPostSQL = " '" + BudgetDescription + "' ";
                cv.put("description", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHost, "POST" , cv);
                if(json != null) {
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
            AlertDialog.Builder alert = new AlertDialog.Builder(EditBudget.this);
            if (s !=null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(EditBudget.this, s , Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}