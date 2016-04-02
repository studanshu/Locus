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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {
	Button login,dbView;
	EditText un,pw;
	TextView bSignUp,bForgotPassword;
	CheckBox rme;
	String rememberme;
	DatabaseHandler db;
	private static String KEY_SUCCESS = "success";
	private static String KEY_MESSAGE = "error_msg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initComponents();
		login.setOnClickListener(this);
		dbView.setOnClickListener(this);
		bSignUp.setOnClickListener(this);
		rme.setOnClickListener(this);
	}
	private void initComponents() {
		// TODO Auto-generated method stub
		un=(EditText)findViewById(R.id.unTextField);
		pw=(EditText)findViewById(R.id.passwordTextField);
		login=(Button)findViewById(R.id.bLogin);
		dbView=(Button)findViewById(R.id.bdbView);
		bSignUp=(TextView)findViewById(R.id.signUp);
		bForgotPassword=(TextView)findViewById(R.id.bforgotPassword);
		rme=(CheckBox)findViewById(R.id.cbrememberme);
		db =new DatabaseHandler(this);
		rememberme="false";
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int dbreturn = 0;
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(true);
		switch(v.getId())
		{
			case R.id.cbrememberme:
				if(rme.isChecked())
					rememberme="true";
				else
					rememberme="false";
				break;
			case R.id.signUp:
				startActivity(new Intent("com.vatsi.locus.REGISTER"));
				break;
			case R.id.bdbView:
				
				startActivity(new Intent("com.vatsi.locus.SQLVIEW"));
				break;

			case R.id.bLogin:
				if( un.getText().toString().length() == 0 )
				    un.setError( "Username is required!" );
				else if( pw.getText().toString().length() == 0 )
				    pw.setError( "Password is required!" );
				else
				{
					try {
						System.out.println("i am here-1");
						dbreturn=db.ValidateUser(un.getText().toString(),pw.getText().toString(), rememberme);
						db.close();
						System.out.println("i am here0");
					} catch (NoSuchAlgorithmException
							| UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(dbreturn==1)
					{
						System.out.println("i am here1");
						db.updateCurrentUser(db.getCurrentUserId(un.getText().toString()),un.getText().toString());
						db.close();
						System.out.println("UpdatedCurrentUser");
						System.out.println(db.getRowCount("currentuser"));
						db.close();
						Intent upanel = new Intent(getApplicationContext(), ItemListPage.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(upanel);
					}
					else if(dbreturn==2)
					{
						System.out.println("i am here2");
						builder.setTitle("Error!");
						builder.setMessage("Invalid Username or password!");
						builder.show();
					}
					else
					{
						System.out.println("i am here3");
						NetAsync(v);
					}
				}
					break;
		}
	}
	private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
        	System.out.println("Check1");
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setTitle("Checking Network");
            nDialog.setMessage("Loading..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }
        /**
         * Gets current device state and checks for working internet connection by trying Google.
        **/
        @Override
        protected Boolean doInBackground(String... args){


        	System.out.println("Check2");

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
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){
        	nDialog.dismiss();
            if(th == true){
                new ProcessLogin().execute();
            }
            else{

            	System.out.println("Check3");
            	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        		builder.setCancelable(true);
            	builder.setTitle("Network Error!");
                builder.setMessage("Cannot connect to internet!");
                builder.show();
            }
        }
    }

    /**
     * Async Task to get and send data to My Sql database through JSON respone.
     **/
    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String email,password;

        @Override
        protected void onPreExecute() {
        	System.out.println("Check4");
        	super.onPreExecute();
            email = un.getText().toString();
            password = pw.getText().toString();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
        	System.out.println("Check5");
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password,rememberme);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
            	pDialog.dismiss();
               if (json.getString(KEY_SUCCESS) != null) {
            	   System.out.println("Check6");
                    String res = json.getString(KEY_SUCCESS);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setMessage("Loading User Space");
                        pDialog.setTitle("Getting Data");
                        /*DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                        
                        UserFunctions logout = new UserFunctions();
                        logout.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
                       */
                        JSONObject tmp = json.getJSONObject("user");
                        User user=new User(tmp.getString("user_name"),tmp.getInt("user_id"),
                        		tmp.getString("user_email"),pw.getText().toString());
                        db.addUser(user);
                        db.updateCurrentUser(user.getID(),user.getUserName());
                        pDialog.dismiss();
                        finish();
                        Intent upanel = new Intent(getApplicationContext(), ItemListPage.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(upanel);
                    }else{
                    	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                		builder.setCancelable(true);
                        builder.setTitle("Error!");
                        builder.setMessage(json.getString(KEY_MESSAGE));
                        builder.show();
                    }
                }
            } catch (JSONException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
                e.printStackTrace();
			}
       }
    }
    
    public void NetAsync(View view){
        new NetCheck().execute();
    }
}