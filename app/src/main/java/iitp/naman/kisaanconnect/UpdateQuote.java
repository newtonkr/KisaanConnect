package iitp.naman.kisaanconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 02-Dec-16.
 */

public class UpdateQuote extends AppCompatActivity {
    private String quoteid1 ;
    private String price1 ;
    private String quantity1 ;
    private String description1 ;
    private String type1;
    private String is_active1;
    private String inputphone1;

    private String serverName;
    private String serverType;
    private String serverAddress;

    private TextView desc;
    private EditText price;
    private EditText type;
    private EditText quantity;
    private Button yes1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatequote);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            quoteid1 = extras.getString("quoteid");
            price1=extras.getString("price");
            quantity1=extras.getString("quantity");
            description1=extras.getString("desc");
            inputphone1=extras.getString("serverphone");
            type1=extras.getString("type");
            is_active1=extras.getString("is_active");
            serverAddress=extras.getString("serveraddress");
            serverName=extras.getString("servername");
            serverType=extras.getString("servertype");
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        yes1 = (Button) findViewById(R.id.update);
        price = (EditText) findViewById(R.id.price);
        quantity = (EditText) findViewById(R.id.quantity);
        desc = (TextView) findViewById(R.id.description);
        type=(EditText) findViewById(R.id.type);

        price.setText(price1,TextView.BufferType.EDITABLE);
        quantity.setText(quantity1,TextView.BufferType.EDITABLE);
        type.setText(type1,TextView.BufferType.EDITABLE);
        desc.setText(description1);

        yes1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                new InterestedNotification().execute();
            }
        });
    }



    @Override
    public void onBackPressed() {
//        Intent upanel = new Intent(getApplicationContext(), GetAllQuotesUser.class);
//        upanel.putExtra("phoneno", inputphone1);
//        upanel.putExtra("name",serverName);
//        upanel.putExtra("address",serverAddress);
//        upanel.putExtra("type",serverType);
//        startActivity(upanel);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.govtnotification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent upanel = new Intent(getApplicationContext(), GetAllQuotesUser.class);
//                upanel.putExtra("phoneno", inputphone1);
//                upanel.putExtra("name",serverName);
//                upanel.putExtra("address",serverAddress);
//                upanel.putExtra("type",serverType);
//                startActivity(upanel);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class InterestedNotification extends AsyncTask<String,Void,JSONObject> {
        private ProgressDialog pDialog;
        private JSONObject resultserver=null;
        private String type2,quantity2,price2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = MyCustomProgressDialog.ctor(UpdateQuote.this);
            pDialog.setCancelable(false);
            pDialog.show();
            type2=type.getText().toString();
            quantity2=quantity.getText().toString();
            price2=price.getText().toString();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONObject jsonIn = new JSONObject();
            try {
                jsonIn.put("quoteId",quoteid1);
                jsonIn.put("type",type2);
                jsonIn.put("quantity",quantity2);
                jsonIn.put("price",price2);
                RequestQueue que = Volley.newRequestQueue(getApplicationContext());
                String urlString = getResources().getString(R.string.network_quotes)+"updatequote/";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlString, jsonIn,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    if (status.compareTo("ok") == 0) {
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
//                                        Intent upanel = new Intent(getApplicationContext(), GetAllQuotesUser.class);
//                                        upanel.putExtra("phoneno", inputphone1);
//                                        upanel.putExtra("name",serverName);
//                                        upanel.putExtra("address",serverAddress);
//                                        upanel.putExtra("type",serverType);
//                                        startActivity(upanel);
                                        pDialog.dismiss();
                                        finish();
                                    }else if(status.compareTo("err") == 0){
                                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                });
                que.add(jsonObjReq);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
                return null;
            }
            return resultserver;
        }
        @Override
        protected void onPostExecute(JSONObject response) {

        }
    }
}

