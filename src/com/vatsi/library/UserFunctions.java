package com.vatsi.library;

/**
 * Author :Raj Amal
 * Email  :raj.amalw@learn2crack.com
 * Website:www.learn2crack.com
 **/

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


public class UserFunctions {

    private JSONParser jsonParser;

    //URL of the PHP API
    private static String loginURL = "http://10.0.2.2/php-login/login/login";
    private static String registerURL = "http://10.0.2.2/php-login/login/register_action";
    private static String forpassURL = "http://10.0.2.2/php-login/login/requestpasswordreset_action";
    private static String chgpassURL = "http://10.0.2.2/learn2crack_login_api/";
    private static String addItemURL = "http://10.0.2.2/php-login/login/addUserItem";
    
    private static String chgpass_tag = "chgpass";


    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * Function to Login
     **/

    public JSONObject loginUser(String email, String password, String rememberme){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_name", email));
        params.add(new BasicNameValuePair("user_password", password));
        params.add(new BasicNameValuePair("user_rememberme", rememberme));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        return json;
    }

    /**
     * Function to change password
     **/

    public JSONObject chgPass(String newpas, String email){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", chgpass_tag));

        params.add(new BasicNameValuePair("newpas", newpas));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
        return json;
    }





    /**
     * Function to reset the password
     **/

    public JSONObject forPass(String user_name){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_name", user_name));
        JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
        return json;
    }






     /**
      * Function to  Register
      **/
    public JSONObject registerUser(String name, String email, String password,String password_repeat){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        //params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("user_name", name));
        params.add(new BasicNameValuePair("user_email", email));
        params.add(new BasicNameValuePair("user_password_new", password));
        params.add(new BasicNameValuePair("user_password_repeat", password_repeat));
        JSONObject json = jsonParser.getJSONFromUrl(registerURL,params);
        return json;
    }

    public JSONObject addItem(Item item) throws UnsupportedEncodingException{
        // Building Parameters
        MultipartEntity params = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        //params.add(new BasicNameValuePair("tag", register_tag));
        params.addPart("user_name", new StringBody(item.getUserName()));
        params.addPart("item_name", new StringBody(item.getName()));
        params.addPart("item_description", new StringBody(item.getDescription()));
        params.addPart("item_DOB", new StringBody(item.getDOB()));
        params.addPart("item_lastTracked", new StringBody(item.getLastTracked()));
        params.addPart("item_isLost", new StringBody(item.isLost()+""));
        params.addPart("item_eLeashOn",new StringBody( item.iseLeashOn()+""));
        params.addPart("item_eLeashType", new StringBody(item.geteLeashType()+""));
        params.addPart("item_eLeashRange",new StringBody( item.geteLeashRange()+""));
        params.addPart("item_macAddress",new StringBody( item.getMacAddress()));
        System.out.println("U1");
        if(item.getPath()!=null)
        	params.addPart("imageFile",new FileBody(new File(item.getPath())));
        System.out.println("U2");
        JSONObject json = jsonParser.getJSONFromUrl(addItemURL,params);
        System.out.println("U3");
        return json;
    }
    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    /*public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }//*/

}

