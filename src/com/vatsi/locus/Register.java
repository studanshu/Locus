package com.vatsi.locus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import com.vatsi.library.DatabaseHandler;
import com.vatsi.library.User;
import com.vatsi.library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Register extends Activity implements OnClickListener {

	
	EditText un,eid,pw,pwr;
	Button Reg;
	private static String KEY_SUCCESS = "success";
	AlertDialog.Builder builder;
	DatabaseHandler db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initComponents();
		Reg.setOnClickListener(this);
		
	}
	
	private void initComponents() {
		// TODO Auto-generated method stub
		un=(EditText)findViewById(R.id.etunReg);
		pw=(EditText)findViewById(R.id.etpwReg);
		pwr=(EditText)findViewById(R.id.etpwrReg);
		eid=(EditText)findViewById(R.id.eteidReg);
		Reg=(Button)findViewById(R.id.bRegister);
		builder = new AlertDialog.Builder(Register.this);
		db =new DatabaseHandler(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//use Database
		builder.setCancelable(true);
		System.out.println("Clicked!");
		
		if(isValidEntry())
		{
			//NetAsync(v);//Global_database_register
			local_database_register();
		}
	}
	private void local_database_register()
	{
        User user=new User(un.getText().toString(),db.getRowCount("users")+1,eid.getText().toString(),pw.getText().toString());
        try {
			db.addUser(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
        Intent lgin = new Intent(getApplicationContext(), MainActivity.class);
        lgin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(lgin);
          finish();
	}

	private boolean isValidEntry() {
		String user_name,user_pass,user_email,user_pass_repeat;
		user_name=un.getText().toString();
		user_pass=pw.getText().toString();
		user_email=eid.getText().toString();
		user_pass_repeat=pwr.getText().toString();
		if(user_name.equals("") || user_pass.equals("") || user_email.equals("") || user_pass_repeat.equals("")){
			builder.setTitle("Error!");
			builder.setMessage("Please fill all the Inputs.");
			builder.show();
			return false;
		}
		else if(user_pass.length()<6){
			builder.setTitle("Error!");
			builder.setMessage("Password size cannot be less than 6.");
			builder.show();
			return false;
		}
		else if(!(user_pass.equals(user_pass_repeat))){
			builder.setTitle("Error!");
			builder.setMessage("Password and Confirm-Password cannot be different.");
			builder.show();
			return false;
		}
		return true;
	}
	private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(Register.this);
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
            return false;//should be set to false in case net connection.

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessRegister().execute();
            }
            else{
            	 nDialog.dismiss();
             	 AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        		 builder.setCancelable(true);
            	 builder.setTitle("Network Error!");
                 builder.setMessage("Can't connect to internet!");
                 builder.show();
            }
        }
    }
	
    private class ProcessRegister extends AsyncTask<String, String, JSONObject> 
    {
        private ProgressDialog pDialog;

        String email,password,uname,password_repeat;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            uname= un.getText().toString();
            email=eid.getText().toString();
            password = pw.getText().toString();
            password_repeat = pwr.getText().toString();
            pDialog = new ProgressDialog(Register.this);
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
	        JSONObject json = userFunction.registerUser(uname, email,password, password_repeat);
            return json;
        }
       @Override
       protected void onPostExecute(JSONObject json) 
       {
        try 
        {
        	pDialog.dismiss();
            if (json.getString(KEY_SUCCESS) != null) 
            {
                String res = json.getString(KEY_SUCCESS);
                System.out.println(res);
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        		builder.setCancelable(true);
                if(Integer.parseInt(res) == 1)
                {
                    pDialog.setTitle("Getting Data");
                    pDialog.setMessage("Loading Info");
                    builder.setTitle("Success!");
                    builder.setMessage("Successfully registered!\n Please verify your account.");
                    builder.show();
                    Intent lgin = new Intent(getApplicationContext(), MainActivity.class);
                    lgin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pDialog.dismiss();
                    startActivity(lgin);
                      finish();
                }
                else if (Integer.parseInt(res) ==2)
                {
                    builder.setTitle("Error!");
                    builder.setMessage("User already exists");
                    builder.show();
                }
                else if (Integer.parseInt(res) ==3)
                {
                	builder.setTitle("Error!");
                    builder.setMessage("Email id already registered");
                    builder.show();
                }
                else if (Integer.parseInt(res) ==4)
                {
                	builder.setTitle("Error!");
                    builder.setMessage("Verification Email sending failed");
                    builder.show();
                }
                else
                {
                	pDialog.dismiss();
                	builder.setTitle("Error!");
                    builder.setMessage("Error occured in registration");
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
