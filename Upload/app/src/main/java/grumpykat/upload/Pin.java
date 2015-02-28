package grumpykat.upload;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Venkatesh on 2/27/2015.
 */
public class Pin extends ActionBarActivity {
    EditText pin1,pin2;
    String mobileNumber,p1,p2,pin;
    String serverResponse1;
    Button send;
    String jdriv_id;
    Boolean next=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pinxml);
        pin1 = (EditText)findViewById(R.id.etpin1);
        pin2 = (EditText)findViewById(R.id.etpin2);
        send = (Button)findViewById(R.id.bPinNext);
        mobileNumber="9845549839";
        new con3().execute();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               p1= pin1.getText().toString();
               p2= pin2.getText().toString();
                new con3().execute();
               if(next)
               if(p1.equals(p2)){
                   next=false;
                   Intent i = new Intent(getApplicationContext(), Profile.class);
                   i.putExtra("DriverID",jdriv_id);
                   startActivity(i);
               }
                else{
                   Log.d("NOT_WORKING_GOOBE  ", jdriv_id);

               }
            }
        });


    }
    public class con3 extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost("http://referajob.in/bidacab/driverpin.php");
                json.put("user_mob", mobileNumber);
                json.put("pin",p1);
                //{"user_mob" :1111111111,"pin":5678}


                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                //*Checking response *//*
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    serverResponse1 = convertStreamToString(in);
                    next = true;
                    Log.d("serverResponse", serverResponse1);
                    //JSONArray arr = new JSONArray(serverResponse1);
                    JSONObject jObj = new JSONObject(serverResponse1);

                    jdriv_id= jObj.getString("driv_id");




                    Log.d("driver id is  ", jdriv_id);

                }
            } catch (ClientProtocolException e/*| UnsupportedEncodingException e*/) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return serverResponse1;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);




        }

    }
    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
