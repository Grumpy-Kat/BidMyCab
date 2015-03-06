package grumpykat.upload;

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
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * Created by Venkatesh on 3/6/2015.
 */
public class Pcc extends ActionBarActivity {
    String serverResponse, jpcc_doc, jpcc_valid, jpcc_iss, reg_no, driver_ty, board, city;
    EditText e1, e2, e3, e4;
    Button doc1, doc2, doc3, doc4, upDoc;
    String selectedPath1 = "NONE";
    String selectedPath2 = "NONE";
    String selectedPath3 = "NONE";
    String selectedPath4 = "NONE";
    ProgressDialog progressDialog;
    HttpEntity resEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcc);
        doc1 = (Button) findViewById(R.id.doc1);
        doc2 = (Button) findViewById(R.id.doc2);
        doc3 = (Button) findViewById(R.id.doc3);
        doc4 = (Button) findViewById(R.id.doc4);
        upDoc = (Button) findViewById(R.id.doc_upload);
        e1 = (EditText) findViewById(R.id.etregno);
        e2 = (EditText) findViewById(R.id.etcarty);
        e3 = (EditText) findViewById(R.id.etboard);
        e4 = (EditText) findViewById(R.id.etcity);

        new con3().execute();

        doc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });


        doc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage1();

            }
        });

        doc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage2();

            }
        });

        doc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage3();
            }
        });

        //Upload button Function
        upDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(selectedPath1.trim().equalsIgnoreCase("NONE")) || !(selectedPath2.trim().equalsIgnoreCase("NONE")) || (selectedPath1.trim().equalsIgnoreCase("NONE")) || (selectedPath2.trim().equalsIgnoreCase("NONE")) || !(selectedPath3.trim().equalsIgnoreCase("NONE")) || !(selectedPath4.trim().equalsIgnoreCase("NONE")) || (selectedPath3.trim().equalsIgnoreCase("NONE")) || (selectedPath4.trim().equalsIgnoreCase("NONE"))) {
                    progressDialog = ProgressDialog.show(Pcc.this, "", "Uploading files to server.....", false);
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            // driver_id = e1.getText().toString();
                            reg_no = e1.getText().toString();
                            driver_ty = e2.getText().toString();
                            board = e3.getText().toString();
                            city = e4.getText().toString();
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


    //Activity Results
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
                //b1.setImageURI(Uri.parse(selectedPath1));


            }

            //Case of Gallery Button 1

            if (requestCode == 2) {

                Uri selectedImageUri = data.getData();

                selectedPath1 = getPath(selectedImageUri);
                // b1.setImageURI(Uri.parse(selectedPath1));


            }
        }

        //Case of Gallery Button 2

        if (requestCode == 22) {

            Uri selectedImageUri = data.getData();

            selectedPath2 = getPath(selectedImageUri);
            // b2.setImageURI(Uri.parse(selectedPath2));


        }
        //Case of Camera Button 2

        if (requestCode == 11) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            selectedPath2 = (getRealPathFromURI(tempUri));
            //b2.setImageURI(Uri.parse(selectedPath2));


        }

        //Case of Camera Button 3


        if (requestCode == 111) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            selectedPath3 = (getRealPathFromURI(tempUri));
            //b1.setImageURI(Uri.parse(selectedPath1));


        }

        //Case of Gallery Button 3

        if (requestCode == 222) {

            Uri selectedImageUri = data.getData();

            selectedPath3 = getPath(selectedImageUri);
            // b2.setImageURI(Uri.parse(selectedPath2));


        }

        //Case of Camera Button 4

        if (requestCode == 1111) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");


            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            selectedPath4 = (getRealPathFromURI(tempUri));
            //b1.setImageURI(Uri.parse(selectedPath1));


        }

        //Case of Gallery Button 4

        if (requestCode == 2222) {

            Uri selectedImageUri = data.getData();

            selectedPath4 = getPath(selectedImageUri);
            // b2.setImageURI(Uri.parse(selectedPath2));


        }

    }

    //Select Image
    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Pcc.this);

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

    //2nd
    private void selectImage1() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Pcc.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
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


    //3rd
    private void selectImage2() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Pcc.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 111);
                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 222);
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }

    //4th
    private void selectImage3() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Pcc.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1111);
                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2222);
                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }


    //Async
    public class con3 extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                HttpPost post = new HttpPost("http://referajob.in/bidacab/enqdrvdocs.php");
                json.put("driv_id", 9992);

                //{"user_mob" :1111111111,"pin":5678}


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

                    //Getting Irrelevant data . dont know why.
                    jpcc_doc = jObj.getString("pcc_doc");
                    jpcc_valid = jObj.getString("pcc_valid_dt");
                    jpcc_iss = jObj.getString("pcc_iss_dt");


                    Log.d("jpcc doc is  ", jpcc_doc);

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


    //Upload Files
    private void doFileUpload() {

        File file1 = new File(selectedPath1);

        File file2 = new File(selectedPath2);
        File file3 = new File(selectedPath3);
        File file4 = new File(selectedPath4);
        String urlString = "http://referajob.in/bidacab/driverpics/pccupdate.php";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urlString);
            FileBody bin1 = new FileBody(file1);
            FileBody bin2 = new FileBody(file2);
            FileBody bin3 = new FileBody(file3);
            FileBody bin4 = new FileBody(file4);
            MultipartEntity reqEntity = new MultipartEntity();
            if (!(selectedPath1.trim().equalsIgnoreCase("NONE")))
                reqEntity.addPart("uploadedfile1", bin1);
            if (!(selectedPath2.trim().equalsIgnoreCase("NONE")))
                reqEntity.addPart("uploadedfile2", bin2);
            if (!(selectedPath2.trim().equalsIgnoreCase("NONE")))
                reqEntity.addPart("uploadedfile3", bin3);
            if (!(selectedPath2.trim().equalsIgnoreCase("NONE")))
                reqEntity.addPart("uploadedfile4", bin4);
            reqEntity.addPart("driv_id", new StringBody("9992"));
            reqEntity.addPart("reg_no", new StringBody(reg_no));
            reqEntity.addPart("car_ty", new StringBody(driver_ty));
            reqEntity.addPart("board", new StringBody(board));
            reqEntity.addPart("city", new StringBody(city));
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
}
