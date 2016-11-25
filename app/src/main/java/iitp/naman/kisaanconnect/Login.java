package iitp.naman.kisaanconnect;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public class Login extends Activity
{

    Button btnLogin;
    Button btnRegister;
    Button btnReset;
    EditText inputPhone;
    EditText inputPassword;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhone = (EditText) findViewById(R.id.phone);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.signup);
        btnLogin = (Button) findViewById(R.id.login);
        btnReset = (Button)findViewById(R.id.forgotpassword);

        btnReset.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent myIntent = new Intent(getApplicationContext(), PasswordReset.class);
                startActivity(myIntent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Log.i("bt register","on click");
                Intent myIntent = new Intent(getApplicationContext(), Register.class);
                startActivity(myIntent);
            }
        });
        /**
         * Login button click event
         * A Toast is set to alert when the Email and Password field is empty
         **/
        btnLogin.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {

                if (  ( !inputPhone.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
                {
                    NetAsync(view);
                }
                else if ( ( !inputPhone.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Password field empty", Toast.LENGTH_LONG).show();
                }
                else if ( ( !inputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "Phone field empty", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Phone and Password field are empty", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Async Task to check whether internet connection is working.
     **/

    private class NetCheck extends AsyncTask<String, Void, Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            nDialog = new ProgressDialog(Login.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args)
        {

        /**
         * Gets current device state and checks for working internet connection by trying Google.
         **/
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
        {
            try
            {
                URL url = new URL(getResources().getString(R.string.network_check));
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200)
                {
                    return true;
                }
            }
            catch (MalformedURLException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }
    @Override
    protected void onPostExecute(Boolean th)
    {

        if(th == true)
        {
            nDialog.dismiss();
            new ProcessLogin().execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Cannot Connect to Network",
                    Toast.LENGTH_LONG).show();

        }
    }
}

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String,Void,JSONObject>
    {

        private ProgressDialog pDialog;
        String inputPhone1,inputPassword1;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            inputPhone1 = inputPhone.getText().toString();
            inputPassword1 = inputPassword.getText().toString();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args)
        {

            JSONObject jsonIn = new JSONObject();
            try
            {
                jsonIn.put("phone", inputPhone1);
                jsonIn.put("password", inputPassword1);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "login Failed", Toast.LENGTH_LONG).show();
                return null;
            }
            return jsonIn;
        }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            RequestQueue que = Volley.newRequestQueue(getApplicationContext());
            Log.i("Login Success : ", "getting profile jsonIn :"+json.toString());

            String urlString = getResources().getString(R.string.network_url) + "signin/";
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, urlString, json,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    if (status.compareTo("ok") == 0) {

                                        pDialog.setMessage("Loading User Space");
                                        pDialog.setTitle("Getting Data");
                                        Intent upanel = new Intent(getApplicationContext(), Home.class);
                                        upanel.putExtra("phoneno", inputPhone1);
                                        pDialog.dismiss();
                                        startActivity(upanel);
                                    }else if(status.compareTo("err") == 0){
                                        Toast.makeText(getApplicationContext(),
                                                response.getString("message") ,
                                                Toast.LENGTH_LONG).show();
                                        pDialog.dismiss();
                                    }
                                    else{
                                        pDialog.setMessage("Server Connection Denied");
                                        pDialog.setTitle("Exit");
                                        pDialog.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),
                                getString(R.string.login_failed),
                                Toast.LENGTH_LONG).show();
                    }
                });
            que.add(jsonObjReq);
            pDialog.dismiss();

        }
    }
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}