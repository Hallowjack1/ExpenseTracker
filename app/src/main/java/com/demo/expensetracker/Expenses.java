package com.demo.expensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Expenses extends AppCompatActivity {
    Button btnRefresh;
    TextView textView, txtDefault_ExpenseAmount, txtDefault_ExpenseDesc, txtDefault_ExpenseID;
    private static EditText edtitemcode;
    private static JSONParser jParser = new JSONParser();
    private static String urlHostDelete = "http://192.168.254.104/ExpenseTracker/DeleteExpense.php";
    private static String urlHostExpenseAmount = "http://192.168.254.104/ExpenseTracker/SelectExpenseAmount.php";
    private static String urlHostExpenseAmountDesc = "http://192.168.254.104/ExpenseTracker/SelectExpenseAmountDesc.php";
    private static String urlHostExpenseDesc = "http://192.168.254.104/ExpenseTracker/SelectExpenseDescription.php";
    private static String urlHostExpenseID = "http://192.168.254.104/ExpenseTracker/SelectExpenseID.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String cItemcode = "";

    private String expaydi, expamt, expdesc;

    String cItemSelected_ExpenseAmount, cItemSelected_ExpenseDesc, cItemSelected_ExpenseID;
    ArrayAdapter<String> adapter_ExpenseAmount;
    ArrayAdapter<String> adapter_ExpenseDesc;
    ArrayAdapter<String> adapter_ExpenseID;
    ArrayList<String> list_ExpenseAmount;
    ArrayList<String> list_ExpenseDesc;
    ArrayList<String> list_ExpenseID;
    Context context = this;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        edtitemcode = (EditText) findViewById(R.id.edtitemcode);
        listView = (ListView) findViewById(R.id.lvExpense);
        textView = (TextView) findViewById(R.id.textView4);
        txtDefault_ExpenseAmount = (TextView) findViewById(R.id.txt_ExpenseAmount);
        txtDefault_ExpenseDesc = (TextView) findViewById(R.id.txt_ExpenseDesc);
        txtDefault_ExpenseID = (TextView) findViewById(R.id.txt_ExpenseID);

        txtDefault_ExpenseAmount.setVisibility(View.GONE);
        txtDefault_ExpenseDesc.setVisibility(View.GONE);
        txtDefault_ExpenseID.setVisibility(View.GONE);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cItemcode = edtitemcode.getText().toString();
                new uploadDataToURL().execute();
                new ExpenseAmount().execute();
                new ExpenseDesc().execute();
                new ExpenseID().execute();

            }
        });

        FloatingActionButton fabExpense = findViewById(R.id.btnAddExpensefab);
        fabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Expenses.this, AddExpense.class));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                cItemSelected_ExpenseAmount = adapter_ExpenseAmount.getItem(position);
                cItemSelected_ExpenseDesc = adapter_ExpenseDesc.getItem(position);
                cItemSelected_ExpenseID = adapter_ExpenseID.getItem(position);

                androidx.appcompat.app.AlertDialog.Builder alert_confirm =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert_confirm.setMessage("Edit the records of" + " " + cItemSelected_ExpenseDesc);
                alert_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txtDefault_ExpenseAmount.setText(cItemSelected_ExpenseAmount);
                        txtDefault_ExpenseDesc.setText(cItemSelected_ExpenseDesc);
                        txtDefault_ExpenseID.setText(cItemSelected_ExpenseID);

                        expamt = txtDefault_ExpenseAmount.getText().toString().trim();
                        expdesc = txtDefault_ExpenseDesc.getText().toString().trim();
                        expaydi = txtDefault_ExpenseID.getText().toString().trim();

                        Intent intent = new Intent(Expenses.this, EditExpense.class);
                        intent.putExtra(EditExpense.EXPAMT, expamt);
                        intent.putExtra(EditExpense.EXPDESC, expdesc);
                        intent.putExtra(EditExpense.EXPID, expaydi);

                        startActivity(intent);

                    }
                });
                alert_confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert_confirm.show();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                cItemSelected_ExpenseAmount = adapter_ExpenseAmount.getItem(position);
                cItemSelected_ExpenseDesc = adapter_ExpenseDesc.getItem(position);
                cItemSelected_ExpenseID = adapter_ExpenseID.getItem(position);

                androidx.appcompat.app.AlertDialog.Builder alert_confirm =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert_confirm.setMessage("Are you sure you want to delete" + " " + cItemSelected_ExpenseDesc);
                alert_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txtDefault_ExpenseID.setText(cItemSelected_ExpenseID);
                        expaydi = txtDefault_ExpenseID.getText().toString().trim();
                        new delete().execute();
                    }
                });
                alert_confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alert_confirm.show();
            }
        });
    }

    private class uploadDataToURL extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Expenses.this);

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

                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHostExpenseAmountDesc, "POST", cv);
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
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Expenses.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) {
                }
                //toast.makeText(ExpensesRecords.this, s, Toast.LENGTH_SHORT).show();
                String wew = s;

                String str = wew;
                final String ExpenseAmount[] = str.split("-");
                list_ExpenseAmount = new ArrayList<String>(Arrays.asList(ExpenseAmount));
                adapter_ExpenseAmount = new ArrayAdapter<String>(Expenses.this,
                        android.R.layout.simple_list_item_1, list_ExpenseAmount);

                listView.setAdapter(adapter_ExpenseAmount);
                textView.setText(listView.getAdapter().getCount() + " " + "record(s) fround.");

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class ExpenseAmount extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Expenses.this);

        public ExpenseAmount() {
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

                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHostExpenseAmount, "POST", cv);
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
        protected void onPostExecute(String ExpenseAmount) {
            super.onPostExecute(ExpenseAmount);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Expenses.this);
            if (ExpenseAmount != null) {
                if (isEmpty.equals("") && !ExpenseAmount.equals("HTTPSERVER_ERROR")) {
                }

                String expamtt = ExpenseAmount;

                String str = expamtt;
                final String Expamts[] = str.split("-");
                list_ExpenseAmount = new ArrayList<String>(Arrays.asList(Expamts));
                adapter_ExpenseAmount = new ArrayAdapter<String>(Expenses.this,
                        android.R.layout.simple_list_item_1, list_ExpenseAmount);
                
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class ExpenseDesc extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Expenses.this);

        public ExpenseDesc() {
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

                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHostExpenseDesc, "POST", cv);
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


            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String ExpenseDesc) {
            super.onPostExecute(ExpenseDesc);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Expenses.this);
            if (ExpenseDesc != null) {
                if (isEmpty.equals("") && !ExpenseDesc.equals("HTTPSERVER_ERROR")) { }


                String expdescc = ExpenseDesc;

                String str = expdescc;
                final String Expdescs[] = str.split("-");
                list_ExpenseDesc = new ArrayList<String>(Arrays.asList(Expdescs));
                adapter_ExpenseDesc = new ArrayAdapter<String>(Expenses.this,
                        android.R.layout.simple_list_item_1,list_ExpenseDesc);

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class ExpenseID extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Expenses.this);

        public ExpenseID() {
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

                cPostSQL = cItemcode;
                cv.put("code", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHostExpenseID, "POST", cv);
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


            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String ExpenseID) {
            super.onPostExecute(ExpenseID);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Expenses.this);
            if (ExpenseID != null) {
                if (isEmpty.equals("") && !ExpenseID.equals("HTTPSERVER_ERROR")) { }


                String expaydii = ExpenseID;

                String str = expaydii;
                final String Expaydis[] = str.split("-");
                list_ExpenseID = new ArrayList<String>(Arrays.asList(Expaydis));
                adapter_ExpenseID = new ArrayAdapter<String>(Expenses.this,
                        android.R.layout.simple_list_item_1,list_ExpenseID);

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class delete extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Expenses.this);

        public delete() {
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

                cPostSQL = cItemSelected_ExpenseID;
                cv.put("id", cPostSQL);

                JSONObject json = jParser.makeHTTPRequest(urlHostDelete, "POST", cv);
                if (json != null) {
                    nSuccess =json.getInt(TAG_SUCCESS);
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
        protected void onPostExecute(String del) {
            super.onPostExecute(del);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Expenses.this);
            if (expaydi != null) {
                if (isEmpty.equals("") && !del.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(Expenses.this, "Data Deleted", Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}