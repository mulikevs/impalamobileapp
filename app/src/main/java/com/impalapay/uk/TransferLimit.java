package com.impalapay.uk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransferLimit extends FragmentActivity {
    Button start_camera_button, upload_button;
    String ba1;
    String mCurrentPhotoPath, userid,base,country_iso2;
    ImageView picture_image_preview;
    Spinner document_type_sp;
    EditText document_number_et;
    ProgressDialog dialog;
    private TextView back_tv;
    private TextView next_tv;
    private String[] doc_type_string_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.transfer_limit);

        userid = Common_data.getPreferences(TransferLimit.this, "userid");
         base=Common_data.getPreferences(TransferLimit.this, "base");
        if(base.equalsIgnoreCase("CAD")){
            country_iso2="ca";
        }else if(base.equalsIgnoreCase("USD")){
            country_iso2="us";
        }else{

        }

        start_camera_button = (Button) findViewById(R.id.start_camera_button);
        picture_image_preview = (ImageView) findViewById(R.id.picture_image_preview);
        start_camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        getDocumentTypes();
        upload_button = (Button) findViewById(R.id.upload_button);
        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        back_tv=(TextView) findViewById(R.id.back_tv);
        next_tv=(TextView) findViewById(R.id.done_tv);
        back_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
                Intent i = new Intent(TransferLimit.this, MainActivity.class);
                startActivity(i);
            }
        });

        next_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                upload();
            }
        });

        document_type_sp = (Spinner) findViewById(R.id.document_type);
        document_number_et = (EditText) findViewById(R.id.document_number);

        document_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                // TODO Auto-generated method stub

                String val = (String) document_type_sp.getItemAtPosition(position);

                Toast.makeText(TransferLimit.this, "" + val, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void upload() {
        boolean flag = false;

        if (0 == document_type_sp.getSelectedItemPosition()) {

            Toast.makeText(TransferLimit.this, "Please Select Document Type.", Toast.LENGTH_SHORT).show();
        }
        /*else if (document_number_et.getText().toString().trim().equals("")) {

            Toast.makeText(TransferLimit.this, "Enter the Document Number.", Toast.LENGTH_SHORT).show();
        }*/
        else if (null == picture_image_preview.getDrawable()) {

            Toast.makeText(TransferLimit.this, "Please take a Photo of the Document.", Toast.LENGTH_SHORT).show();
        }else{
            flag = true;
        }

        if(flag){
            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
            byte[] ba = bao.toByteArray();
            ba1 = Base64.encodeToString(ba,Base64.DEFAULT);//encodeBytes(ba);

            // Upload image to server
            uploadImageToServer();
        }
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setPic();
        }
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = picture_image_preview.getWidth();
        int targetH = picture_image_preview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        int scaleFactor = 1;

        try{
            if ((targetW > 0) && (targetH > 0)) {
                scaleFactor = Math.min(photoW/targetW, photoH/targetH);
                bmOptions.inSampleSize = scaleFactor;
            }
        }
        catch(Exception e){
            Log.d("Exception", e.getMessage().toString());
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;

        bmOptions.inPurgeable = true;

        Bitmap resized = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        //Bitmap resized = Bitmap.createScaledBitmap(bitmap, targetW, targetH, true);

        picture_image_preview.setImageBitmap(resized);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;
    }

    private void uploadImageToServer() {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {
            object.put("user_id", userid);
            object.put("base_64", ba1);
            object.put("image_name",System.currentTimeMillis() + ".jpg");
            object.put("document_type", document_type_sp.getSelectedItem().toString());
            object.put("document_number", document_number_et.getText().toString());

            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/uploadPic", params, new UploadPicHandler(), Login.token);
    }

    class UploadPicHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {

            super.onStart();
            dialog = ProgressDialog.show(TransferLimit.this,null, "Uploading image... ");

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {

            super.onSuccess(result);

            Log.d("onSuccess", "onSuccess");

            if (result.length() > 0) {
                Log.d("response", result);
                try {
                    JSONObject json = new JSONObject(result);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if(1001 == response_code)
                        b = true;
                    final String msg = json.optString("message");

                    if (b) {
                        final int ref_id = json.getInt("image_ref");
						/*JSONArray array = jsonObject.getJSONArray("data");
						JSONObject object = array.getJSONObject(0);

						Common_data.setPreference(TransferLimit.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("carddetails");
						Common_data.setPreference(TransferLimit.this,"carddetails_array",carddetails_array.toString());
						*/
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                //finish();
                                //startActivity(getIntent());

                                Intent i=new Intent(TransferLimit.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else if(1020 == response_code){
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText("Your session has expired. Please Login Again.");
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                Intent i = new Intent(TransferLimit.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else if(1030 == response_code){
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(getString(R.string.app_store_url)));
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(TransferLimit.this)
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        uploadImageToServer();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        //finish();
                                        //startActivity(getIntent());
                                    }
                                }).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            //Common_data.showAccountVerifyDialog(TransferLimit.this);
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", "onFailure");
            super.onFailure(statusCode, error, content);
            Toast.makeText(getApplicationContext(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(TransferLimit.this)
                    .setTitle("Info")
                    .setMessage("Network Error. Please retry!")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            uploadImageToServer();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            //finish();
                            //startActivity(getIntent());
                        }
                    }).show();
        }
    }


    private void getDocumentTypes() {
        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();
        try
        {
            object.put("user_id", userid);
            object.put("country_iso2", country_iso2);

            params.put("request", object.toString());
            Log.d("getDocumentTypes", params.toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("auth/getDocumentTypes", params, new TransferLimitsHandler(), Login.token);
    }

    class TransferLimitsHandler extends AsyncHttpResponseHandler{


        ProgressDialog pdialog;

        @Override
        public void onStart() {
            super.onStart();
            pdialog=ProgressDialog.show(TransferLimit.this, "", "Getting Document Types...");
        }

        @Override
        @Deprecated
        public void onSuccess(String content) {
            super.onSuccess(content);

            if(content.length()>0){

                try {

                    JSONObject json=new JSONObject(content);
                    Log.d("Response array", json.toString());
                    //boolean b=json.optBoolean("result");
                    boolean b = false;
                    int response_code = json.getInt("code");
                    String msg = json.getString("message");
                    Log.d("Response Code", String.valueOf(response_code));
                    if(1001 == response_code)
                        b = true;

                    if(b)
                    {
                        JSONArray doc_type_data = json.getJSONArray("data");
                        Log.d("doc_type jsonarray", doc_type_data.toString());
                        doc_type_string_array = new String[doc_type_data.length()+1];
                        doc_type_string_array[0] = "Select Document Type";

                        for (int i = 0; i < doc_type_data.length(); i++) {
                            JSONObject doc_type_data_object = doc_type_data.getJSONObject(i);
                            doc_type_string_array[i+1] = doc_type_data_object.getString("name");
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(TransferLimit.this, R.layout.custom_spinner_right_text, doc_type_string_array);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner
                        document_type_sp.setAdapter(dataAdapter);
                    }
                    else if(1010 == response_code){
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("Retry");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                Intent i = new Intent(TransferLimit.this, MainActivity.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else if(1030 == response_code){
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(getString(R.string.app_store_url)));
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else if(1020 == response_code){
                        final Dialog d=new Dialog(TransferLimit.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                Intent i = new Intent(TransferLimit.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    }
                }catch(Exception e){

                }
            }
        }
        @Override
        public void onFinish() {
            super.onFinish();
            if(pdialog.isShowing())
                pdialog.dismiss();



        }
        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            Log.d("onFailure", "onFailure");
            super.onFailure(error);
            Toast.makeText(getApplicationContext(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(TransferLimit.this)
                    .setTitle("Info")
                    .setMessage("Network Error. Please retry!")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            getDocumentTypes();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            finish();
                            Intent i = new Intent(TransferLimit.this, MainActivity.class);
                            startActivity(i);
                        }
                    }).show();
        }
    }


}