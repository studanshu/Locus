package com.vatsi.locus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.vatsi.library.DatabaseHandler;
import com.vatsi.library.Item;
import com.vatsi.library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddItemPage extends Activity implements View.OnClickListener{

	Button Done,Plus,Minus;
	TextView AddImage,eLeash;
	EditText Name,Desc;
	ImageView ivImage;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int REQUEST_CAMERA = 3;
    public static final int SELECT_FILE = 4;
    private static String KEY_SUCCESS = "success";
    DatabaseHandler db=new DatabaseHandler(this);
    private String path;
    private Item item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.additem);
		initComponents();
		Done.setOnClickListener(this);
		Plus.setOnClickListener(this);
		Minus.setOnClickListener(this);
		AddImage.setOnClickListener(this);
	}
	
	public void initComponents()
	{
		Done=(Button)findViewById(R.id.bDoneadd);
		Plus=(Button)findViewById(R.id.bPlusadd);
		Minus=(Button)findViewById(R.id.bMinusadd);
		AddImage=(TextView)findViewById(R.id.bPictureadd);
		eLeash=(TextView)findViewById(R.id.tveLeashRangeadd);
		Name=(EditText)findViewById(R.id.etNameadd);
		Desc=(EditText)findViewById(R.id.etDescriptionadd);
		ivImage=(ImageView)findViewById(R.id.ivImageadd);
		path=null;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bPlusadd:
		{
			eLeash.setText((Integer.parseInt(eLeash.getText().toString())+1)+"");
		}
		break;
		case R.id.bMinusadd:
		{
			int tmp=(Integer.parseInt(eLeash.getText().toString())-1);
			eLeash.setText(tmp>=0?(tmp+""):"0");
		}
		break;
		case R.id.bPictureadd:
		{
			selectImage();
		}
		break;
		case R.id.bDoneadd:
		{
			Calendar cal=Calendar.getInstance(Locale.getDefault());
			boolean eLeashOn=false;
			int eLeashRange=Integer.parseInt(eLeash.getText().toString());
			if(eLeashRange>0)
				eLeashOn=true;
			if(path!=null)
			{
				File f=new File(path);
				String s=f.getAbsolutePath();
				int i=s.length()-1;
				while(s.charAt(i)!='/')
					i--;
				File f2=new File((s.substring(0,i+1)+db.getCurrentUser()+"_"+Name.getText().toString()+".jpg"));
				f.renameTo(f2);
				path=(s.substring(0,i+1)+db.getCurrentUser()+"_"+Name.getText().toString()+".jpg");
			}
			//f.renameTo(new File(f.getPath().substring(0,8)+Name.getText().toString()));
			item=new Item(db.getCurrentUser(),0,Name.getText().toString(),Desc.getText().toString(),
							   db.getCurrentUserId(db.getCurrentUser()),cal,cal,ivImage.getDrawable(),
							   false,eLeashOn,1,eLeashRange,"MacAddr",path);
			//db.addItem(item);
			NetAsync(v);
		}
		break;
		}
	}
	private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
 
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemPage.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), db.getCurrentUser()+"_temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals(db.getCurrentUser()+"_temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
 
                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);
 
                     bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                    ivImage.setImageBitmap(bm);
 
                    path=f.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri, AddItemPage.this);
                System.out.println("tempPath="+tempPath);
                System.out.println("Path="+selectedImageUri.getPath());
                path=tempPath;
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                ivImage.setImageBitmap(bm);
            }
        }
    }
	
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(AddItemPage.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(30000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return true;//should be set to false in case net connection.

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessAddItem().execute();
            }
            else{
            	 nDialog.dismiss();
             	 AlertDialog.Builder builder = new AlertDialog.Builder(AddItemPage.this);
        		 builder.setCancelable(true);
            	 builder.setTitle("Network Error!");
                 builder.setMessage("Can't connect to internet!");
                 builder.show();
            }
        }
    }
	
    private class ProcessAddItem extends AsyncTask<String, String, JSONObject> 
    {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddItemPage.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) 
        {
	        UserFunctions userFunction = new UserFunctions();
	        JSONObject json = null;
			try {
				json = userFunction.addItem(item);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return json;
        }
       @Override
       protected void onPostExecute(JSONObject json) 
       {
        try 
        {
            if (json.getString(KEY_SUCCESS) != null) 
            {
                String res = json.getString(KEY_SUCCESS);
                System.out.println(res);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddItemPage.this);
        		builder.setCancelable(true);
                if(Integer.parseInt(res) == 1)
                {
                    pDialog.setTitle("Getting Data");
                    pDialog.setMessage("Loading Info");
                    builder.setTitle("Success!");
                    builder.setMessage("Successfully Added Item!");
                    builder.show();
                    /*DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    JSONObject json_user = json.getJSONObject("user");
                    UserFunctions logout = new UserFunctions();
                    logout.logoutUser(getApplicationContext());
                    db.addUser(json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));//*/
                    /**
                     * Stores registered data in SQlite Database
                     * Launch Registered screen
                     **/
                    Intent lgin = new Intent(getApplicationContext(), MainActivity.class);

                    /**
                     * Close all views before launching Registered screen
                    **/
                    lgin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pDialog.dismiss();
                    //startActivity(lgin);
                    finish();
                }
                else if (Integer.parseInt(res) ==2)
                {
                	pDialog.dismiss();
                    builder.setTitle("Error!");
                    builder.setMessage("User already exists");
                    builder.show();
                }
                else if (Integer.parseInt(res) ==3)
                {
                	pDialog.dismiss();
                	builder.setTitle("Error!");
                    builder.setMessage("Email id already registered");
                    builder.show();
                }
                else if (Integer.parseInt(res) ==4)
                {
                	pDialog.dismiss();
                	builder.setTitle("Error!");
                    builder.setMessage("Verification Email sending failed");
                    builder.show();
                }
                else
                {
                	pDialog.dismiss();
                	builder.setTitle("Error!");
                    builder.setMessage("Error occured in Adding Item");
                    builder.show();
                }
            }
        }
        catch (JSONException e) 
        {
            e.printStackTrace();
        }
       }
    }
	private void NetAsync(View v) 
	{
		// TODO Auto-generated method stub
		new NetCheck().execute();
	}
}
