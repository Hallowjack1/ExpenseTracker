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

public class EditExpense extends AppCompatActivity {
Button btnEditExpense;
    private static EditText Amount,Description;
    private static String cItemcode = "";
    private static JSONParser jParser = new JSONParser();
    private static String urlHost = "http://192.168.254.105/ExpenseTracker/UpdateExpense.php";
    private static String TAG_MESSAGE = "message" , TAG_SUCCESS = "success";
    private static String online_dataset = "";
    public static String String_isempty = "";
    public static final String EXPAMT = "EXPAMT";
    public static final String EXPDESC = "EXPDESC";
    public static final String EXPID = "EXPID";

    private String expaydi,expamt,expdesc;

    public static String ExpenseAmount = "";
    public static String ExpenseDescription = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        btnEditExpense = findViewById(R.id.btnEditExpense);
        Amount = findViewById(R.id.etAmount);
        Description = findViewById(R.id.etDesc);

        Intent i = getIntent();
        expaydi = i.getStringExtra(EXPID);
        expamt = i.getStringExtra(EXPAMT);
        expdesc = i.getStringExtra(EXPDESC);

        Amount.setText(expamt);
        Description.setText(expdesc);

        btnEditExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseAmount = Amount.getText().toString();
                ExpenseDescription = Description.getText().toString();

                new uploadDataToURL().execute();

                Intent i = new Intent(EditExpense.this, MainActivity.class);

                i.putExtra(MainActivity.EXPAMTM, ExpenseAmount);

                startActivity(i);
                finish();
            }
        });
    }

    private class uploadDataToURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(EditExpense.this);

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
                //insert anything in this cod

                cPostSQL = expaydi;
                cv.put("id", cPostSQL);

                cPostSQL = " '" + ExpenseAmount + "' ";
                cv.put("amount", cPostSQL);

                cPostSQL = " '" + ExpenseDescription + "' ";
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
            AlertDialog.Builder alert = new AlertDialog.Builder(EditExpense.this);
            if (s !=null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(EditExpense.this, s , Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}