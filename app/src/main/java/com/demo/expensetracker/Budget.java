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

public class Budget extends AppCompatActivity {
    Button btnRefresh;
    TextView textView, txtDefault_BudgetAmount, txtDefault_BudgetDesc, txtDefault_BudgetID;
    private static EditText edtitemcode;
    private static JSONParser jParser = new JSONParser();
    private static String urlHostDelete = "http://192.168.254.106/ExpenseTracker/DeleteBudget.php";
    private static String urlHostBudgetAmount = "http://192.168.254.106/ExpenseTracker/SelectBudgetAmount.php";
    private static String urlHostBudgetAmountDesc = "http://192.168.254.106/ExpenseTracker/SelectBudgetAmountDesc.php";
    private static String urlHostBudgetDesc = "http://192.168.254.106/ExpenseTracker/SelectBudgetDescription.php";
    private static String urlHostBudgetID = "http://192.168.254.106/ExpenseTracker/SelectBudgetID.php";
    private static String TAG_MESSAGE = "message", TAG_SUCCESS = "success";
    private static String online_dataset = "";
    private static String cItemcode = "";

    private String bgtaydi, bgtamt, bgtdesc;

    String cItemSelected_BudgetAmount, cItemSelected_BudgetDesc, cItemSelected_BudgetID, cItemSelected_BudgetAmountDesc;
    ArrayAdapter<String> adapter_BudgetAmount;
    ArrayAdapter<String> adapter_BudgetDesc;
    ArrayAdapter<String> adapter_BudgetID;
    ArrayList<String> list_BudgetAmount;
    ArrayList<String> list_BudgetDesc;
    ArrayList<String> list_BudgetID;
    Context context = this;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        edtitemcode = (EditText) findViewById(R.id.edtitemcode);
        listView = (ListView) findViewById(R.id.lvBudget);
        textView = (TextView) findViewById(R.id.textView4);
        txtDefault_BudgetAmount = (TextView) findViewById(R.id.txt_BudgetAmount);
        txtDefault_BudgetDesc = (TextView) findViewById(R.id.txt_BudgetDesc);
        txtDefault_BudgetID = (TextView) findViewById(R.id.txt_BudgetID);

        txtDefault_BudgetAmount.setVisibility(View.GONE);
        txtDefault_BudgetDesc.setVisibility(View.GONE);
        txtDefault_BudgetID.setVisibility(View.GONE);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cItemcode = edtitemcode.getText().toString();
                new uploadDataToURL().execute();
                new BudgetAmount().execute();
                new BudgetDesc().execute();
                new BudgetID().execute();

            }
        });

        FloatingActionButton fabBudget = findViewById(R.id.btnAddBudgetfab);
        fabBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Budget.this, AddBudget.class));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                cItemSelected_BudgetAmount = adapter_BudgetAmount.getItem(position);
                cItemSelected_BudgetDesc = adapter_BudgetDesc.getItem(position);
                cItemSelected_BudgetID = adapter_BudgetID.getItem(position);

                androidx.appcompat.app.AlertDialog.Builder alert_confirm =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert_confirm.setMessage("Edit the records of" + " " + cItemSelected_BudgetDesc);
                alert_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txtDefault_BudgetAmount.setText(cItemSelected_BudgetAmount);
                        txtDefault_BudgetDesc.setText(cItemSelected_BudgetDesc);
                        txtDefault_BudgetID.setText(cItemSelected_BudgetID);

                        bgtamt = txtDefault_BudgetAmount.getText().toString().trim();
                        bgtdesc = txtDefault_BudgetDesc.getText().toString().trim();
                        bgtaydi = txtDefault_BudgetID.getText().toString().trim();

                        Intent intent = new Intent(Budget.this, EditBudget.class);
                        intent.putExtra(EditBudget.BGTAMT, bgtamt);
                        intent.putExtra(EditBudget.BGTDESC, bgtdesc);
                        intent.putExtra(EditBudget.BGTID, bgtaydi);

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
                cItemSelected_BudgetAmount = adapter_BudgetAmount.getItem(position);
                cItemSelected_BudgetDesc = adapter_BudgetDesc.getItem(position);
                cItemSelected_BudgetID = adapter_BudgetID.getItem(position);

                androidx.appcompat.app.AlertDialog.Builder alert_confirm =
                        new androidx.appcompat.app.AlertDialog.Builder(context);
                alert_confirm.setMessage("Are you sure you want to delete" + " " + cItemSelected_BudgetDesc);
                alert_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        txtDefault_BudgetID.setText(cItemSelected_BudgetID);
                        bgtaydi = txtDefault_BudgetID.getText().toString().trim();
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
        ProgressDialog pDialog = new ProgressDialog(Budget.this);

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

                JSONObject json = jParser.makeHTTPRequest(urlHostBudgetAmountDesc, "POST", cv);
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
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Budget.this);
            if (s != null) {
                if (isEmpty.equals("") && !s.equals("HTTPSERVER_ERROR")) {
                }
                //toast.makeText(BudgetRecords.this, s, Toast.LENGTH_SHORT).show();
                String wew = s;

                String str = wew;
                final String BudgetAmount[] = str.split("-");
                list_BudgetAmount = new ArrayList<String>(Arrays.asList(BudgetAmount));
                adapter_BudgetAmount = new ArrayAdapter<String>(Budget.this,
                        android.R.layout.simple_list_item_1, list_BudgetAmount);

                listView.setAdapter(adapter_BudgetAmount);
                textView.setText(listView.getAdapter().getCount() + " " + "record(s) fround.");

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class BudgetAmount extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Budget.this);

        public BudgetAmount() {
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

                JSONObject json = jParser.makeHTTPRequest(urlHostBudgetAmount, "POST", cv);
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
        protected void onPostExecute(String BudgetAmount) {
            super.onPostExecute(BudgetAmount);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Budget.this);
            if (BudgetAmount != null) {
                if (isEmpty.equals("") && !BudgetAmount.equals("HTTPSERVER_ERROR")) {
                }


                String bgtamtt = BudgetAmount;

                String str = bgtamtt;
                final String Expamts[] = str.split("-");
                list_BudgetAmount = new ArrayList<String>(Arrays.asList(Expamts));
                adapter_BudgetAmount = new ArrayAdapter<String>(Budget.this,
                        android.R.layout.simple_list_item_1, list_BudgetAmount);

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class BudgetDesc extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Budget.this);

        public BudgetDesc() {
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

                JSONObject json = jParser.makeHTTPRequest(urlHostBudgetDesc, "POST", cv);
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
        protected void onPostExecute(String BudgetDesc) {
            super.onPostExecute(BudgetDesc);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Budget.this);
            if (BudgetDesc != null) {
                if (isEmpty.equals("") && !BudgetDesc.equals("HTTPSERVER_ERROR")) { }


                String bgtdescc = BudgetDesc;

                String str = bgtdescc;
                final String Expdescs[] = str.split("-");
                list_BudgetDesc = new ArrayList<String>(Arrays.asList(Expdescs));
                adapter_BudgetDesc = new ArrayAdapter<String>(Budget.this,
                        android.R.layout.simple_list_item_1,list_BudgetDesc);

            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }

    private class BudgetID extends AsyncTask<String, String, String> {
        String cPOST = "", cPostSQL = "", cMessage = "Querying data...";
        int nPostValueIndex;
        ProgressDialog pDialog = new ProgressDialog(Budget.this);

        public BudgetID() {
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

                JSONObject json = jParser.makeHTTPRequest(urlHostBudgetID, "POST", cv);
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
        protected void onPostExecute(String BudgetID) {
            super.onPostExecute(BudgetID);
            pDialog.dismiss();
            String isEmpty = "";
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Budget.this);
            if (BudgetID != null) {
                if (isEmpty.equals("") && !BudgetID.equals("HTTPSERVER_ERROR")) { }


                String bgtaydii = BudgetID;

                String str = bgtaydii;
                final String Expaydis[] = str.split("-");
                list_BudgetID = new ArrayList<String>(Arrays.asList(Expaydis));
                adapter_BudgetID = new ArrayAdapter<String>(Budget.this,
                        android.R.layout.simple_list_item_1,list_BudgetID);

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
        ProgressDialog pDialog = new ProgressDialog(Budget.this);

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

                cPostSQL = cItemSelected_BudgetID;
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
            android.app.AlertDialog.Builder alert = new AlertDialog.Builder(Budget.this);
            if (bgtaydi != null) {
                if (isEmpty.equals("") && !del.equals("HTTPSERVER_ERROR")) { }
                Toast.makeText(Budget.this, "Data Deleted", Toast.LENGTH_SHORT).show();
            } else {
                alert.setMessage("Query Interrupted... \nPlease Check Internet connection");
                alert.setTitle("Error");
                alert.show();
            }
        }
    }
}