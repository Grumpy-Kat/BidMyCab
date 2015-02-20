package grumpykat.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * Created by Venkatesh on 2/5/2015.
 */


public class FileUploadTest extends Activity implements View.OnClickListener {

    private static final int SELECT_FILE1 = 1;
    private static final int SELECT_FILE2 = 2;
    String selectedPath1 = "NONE";
    String selectedPath2 = "NONE";
    ProgressDialog progressDialog;
    Button b3;
    SmartImageView b1 = null, b2 = null;
    EditText e1, e2, e3, e4, e5;
    String driver_id, dl_no, dl_name, dl_add, dl_exp, driv_id;
    HttpEntity resEntity;
    String serverResponse = null;
    String jdriver_id, jdl_no, jdl_name, jdl_add, jexpiry_date, jdlFr, jdlBk;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driv_id = "9992";

        b1 = (SmartImageView) findViewById(R.id.Button01);
        b2 = (SmartImageView) findViewById(R.id.Button02);
        b3 = (Button) findViewById(R.id.upload);
        e1 = (EditText) findViewById(R.id.etdriver);
        e2 = (EditText) findViewById(R.id.etdlno);
        e3 = (EditText) findViewById(R.id.etdlname);
        e4 = (EditText) findViewById(R.id.etdladd);
        e5 = (EditText) findViewById(R.id.etexp);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);


//        final String uri = "http://referajob.in/bidacab/enqdrvdocs.php";
//        final String body = String.format("{\"driv_id\" :9992}");


        new con().execute();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage1();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedPath1.trim().equalsIgnoreCase("NONE")) && !(selectedPath2.trim().equalsIgnoreCase("NONE"))) {
                    progressDialog = ProgressDialog.show(FileUploadTest.this, "", "Uploading files to server.....", false);
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            driver_id = e1.getText().toString();
                            dl_no = e2.getText().toString();
                            dl_name = e3.getText().toString();
                            dl_add = e4.getText().toString();
                            dl_exp = e5.getText().toString();
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
                    Toast.makeText(getApplicationContext(), "Please select two files to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
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
                b1.setImageURI(Uri.parse(selectedPath1));


            }

            //Case of Gallery Button 1

            if (requestCode == 2) {

                Uri selectedImageUri = data.getData();

                selectedPath1 = getPath(selectedImageUri);
                b1.setImageURI(Uri.parse(selectedPath1));


            }
        }

        //Case of Gallery Button 2

        if (requestCode == 22) {

            Uri selectedImageUri = data.getData();

            selectedPath2 = getPath(selectedImageUri);
            b2.setImageURI(Uri.parse(selectedPath2));


        }
        //Case of Camera Button 2

        if (requestCode == 11) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            selectedPath2 = (getRealPathFromURI(tempUri));
            b2.setImageURI(Uri.parse(selectedPath2));


        }

    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void doFileUpload() {

        File file1 = new File(selectedPath1);
        File file2 = new File(selectedPath2);
        String urlString = "http://referajob.in/bidacab/driverpics/twophuplds.php";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            FileBody bin1 = new FileBody(file1);
            FileBody bin2 = new FileBody(file2);
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("uploadedfile1", bin1);
            reqEntity.addPart("uploadedfile2", bin2);
            reqEntity.addPart("driv_id", new StringBody(driver_id));
            reqEntity.addPart("dl_no", new StringBody(dl_no));
            reqEntity.addPart("dl_nme", new StringBody(dl_name));
            reqEntity.addPart("dl_addr", new StringBody(dl_add));
            reqEntity.addPart("dl_exp", new StringBody(dl_exp));
            post.setEntity(reqEntity);
            HttpResponse response = client.execute(post);
            resEntity = response.getEntity();
            final String response_str = EntityUtils.toString(resEntity);
            if (resEntity != null) {
                Log.i("RESPONSE", response_str);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            //res.setTextColor(Color.BLUE);
                            //res.setText("n Response from server : n " + response_str);
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

//Getting json with a request
    public class con extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost("http://referajob.in/bidacab/enqdrvdocs.php");
                json.put("driv_id", 9992);


                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);

                //*Checking response *//*
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    serverResponse = convertStreamToString(in);
                    Log.d("serverResponse", serverResponse);
                    JSONArray arr = new JSONArray(serverResponse);
                    JSONObject jObj = arr.getJSONObject(0);
                    jdl_no = jObj.getString("dri_licnce_no");
                    jdl_name = jObj.getString("driver_dl_name");
                    jdl_add = jObj.getString("dri_address");
                    jexpiry_date = jObj.getString("dl_exp_dt");
                    jdlFr = jObj.getString("dl_fr_pic_dest");
                    jdlBk = jObj.getString("dl_bk_pic_dest");


                    Log.d("jdl_no is ", jdl_no);

                }
            } catch (ClientProtocolException e/*| UnsupportedEncodingException e*/) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return serverResponse;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            e2.setText(jdl_no);
            e3.setText(jdl_name);
            e4.setText(jdl_add);
            e5.setText(jexpiry_date);
            b1.setImageUrl(jdlBk);
            b2.setImageUrl(jdlFr);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Button01:
                selectImage();
                break;
            case R.id.Button02:
                selectImage1();
                break;

          /*  case R.id.ratingBar:
                startDriverRatingActivity();
                break;*/
        }
    }

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadTest.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
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

    private void selectImage1() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadTest.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 11);
                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 22);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }

        });

        builder.show();

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

}