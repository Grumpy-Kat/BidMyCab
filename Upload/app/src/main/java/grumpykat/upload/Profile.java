package grumpykat.upload;

/**
 * Created by Venkatesh on 2/19/2015.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Profile extends ActionBarActivity {
    TextView tvupload, res;
    Button pUpload, next;
    String serverResponse1 = null;
    SmartImageView setPic, setPic2;
    String selectedPath1 = "NONE";
    String driv_id;
    ProgressDialog progressDialog;
    EditText DriverName, PhNo;
    HttpEntity resEntity;
    String jdri_name, jdri_mobile_no, jprofile_pic_dest, jsmall_prof_pic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        tvupload = (TextView) findViewById(R.id.tvUp);
        setPic = (SmartImageView) findViewById(R.id.bSelectPic);


        pUpload = (Button) findViewById(R.id.bUp);
        res = (TextView) findViewById(R.id.tvres);
        next = (Button) findViewById(R.id.bnext);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            driv_id = extras.getString("DriverID");


            Log.d("driver id is ", driv_id);

        }
        new con2().execute();
        setPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        pUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedPath1.trim().equalsIgnoreCase("NONE"))) {
                    progressDialog = ProgressDialog.show(Profile.this, "", "Uploading files to server.....", false);
                    Thread thread = new Thread(new Runnable() {
                        public void run() {

                            doFileUpload();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            });
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a file to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FileUploadTest.class);
                i.putExtra("TravellingId", driv_id);
                startActivity(i);
            }
        });


    }

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }

    private void doFileUpload() {

        File file1 = new File(selectedPath1);

        String urlString = "http://referajob.in/bidacab/driverpics/profilepic.php";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            FileBody bin1 = new FileBody(file1);

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("uploadedfile1", bin1);
            reqEntity.addPart("driv_id", new StringBody(driv_id));


            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            final String response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Log.i("RESPONSE", response_str);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            res.setTextColor(Color.BLUE);
                            res.setText("n Response from server : n " + response_str);
                            Toast.makeText(getApplicationContext(), "Upload Complete. Check the server uploads directory.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Case of Camera Button 1
            if (requestCode == 1) {


                Bitmap photo = (Bitmap) data.getExtras().get("data");


                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                selectedPath1 = (getRealPathFromURI(tempUri));
                setPic.setImageURI(Uri.parse(selectedPath1));


            }

            //Case of Gallery Button 1

            if (requestCode == 2) {

                Uri selectedImageUri = data.getData();

                selectedPath1 = getPath(selectedImageUri);
                setPic.setImageURI(Uri.parse(selectedPath1));


            }
        }


    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public class con2 extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost("http://referajob.in/bidacab/driverpics/enqdrivdts.php");
                json.put("driv_id", driv_id);


                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                //*Checking response *//*
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    serverResponse1 = convertStreamToString(in);
                    Log.d("serverResponse", serverResponse1);
                    JSONArray arr = new JSONArray(serverResponse1);
                    JSONObject jObj = arr.getJSONObject(0);
                    jdri_name = jObj.getString("dri_name");
                    jdri_mobile_no = jObj.getString("dri_mobile_no");
                    jprofile_pic_dest = jObj.getString("profile_pic_dest");
                    jsmall_prof_pic = jObj.getString("small_prof_pic");


                    Log.d("jdl_no is ", jdri_name);

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


            setPic.setImageUrl(jprofile_pic_dest);

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