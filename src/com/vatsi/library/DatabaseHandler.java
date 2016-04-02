package com.vatsi.library;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
	public static DatabaseHandler db=null;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "login";
    
  //item table name
  	private static final String TABLE_ITEMS="items";
  	
    // users table name
    private static final String TABLE_LOGIN = "users";
    
    // currentuser table name
    private static final String TABLE_CURRENTUSER = "currentuser";

    // users Table Columns names
    public static final String USER_ID= "user_id";
	public static final String USER_NAME= "user_name";
	public static final String USER_PASSWORD_HASH= "user_password_hash";
	public static final String USER_EMAIL= "user_email";
	
	//item table columns names
	public static final String ITEM_ID= "Item_id";
	//user_name will be there
	public static final String ITEM_NAME= "Item_name";
	public static final String ITEM_DESCRIPTION= "Item_description";
	public static final String ITEM_DOB= "Item_DOB";
	public static final String ITEM_LASTTRACKED= "Item_lastTracked";
	public static final String ITEM_PICTURE= "Item_picture";
	public static final String ITEM_ISLOST= "Item_isLost";
	public static final String ITEM_ELEASHON= "Item_eLeashOn";
	public static final String ITEM_ELEASHTYPE= "Item_eLeashType";
	public static final String ITEM_ELEASHRANGE= "Item_eLeashRange";
	public static final String ITEM_MACADDRESS= "Item_macAddress";
	public static final String ITEM_ISUPDATED= "Item_isUpdated";
	
	// Creating Tables
		String CREATE_TABLE_LOGIN="CREATE TABLE "+TABLE_LOGIN+"("
				+ USER_ID + " INTEGER NOT NULL PRIMARY KEY, "
				+ USER_NAME + " TEXT NOT NULL, "
				+ USER_PASSWORD_HASH + " TEXT DEFAULT NULL, "
				+ USER_EMAIL + " TEXT NOT NULL);"
				;
		
		String CREATE_TABLE_ITEMS="CREATE TABLE "+TABLE_ITEMS+"("
				+ USER_NAME + " TEXT NOT NULL, "
				+ ITEM_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ ITEM_NAME + " TEXT NOT NULL, "
				+ ITEM_DESCRIPTION + " TEXT DEFAULT NULL, "
				+ ITEM_DOB + " TEXT NOT NULL, "
				+ ITEM_LASTTRACKED + " TEXT NOT NULL, "
				+ ITEM_PICTURE + " TEXT DEFAULT NULL, "
				+ ITEM_ISLOST + " INTEGER NOT NULL, "
				+ ITEM_ELEASHON + " INTEGER NOT NULL, "
				+ ITEM_ELEASHTYPE + " INTEGER DEFAULT NULL, "
				+ ITEM_ELEASHRANGE + " INTEGER DEFAULT NULL, "
				+ ITEM_MACADDRESS + " TEXT NOT NULL, "
				+ ITEM_ISUPDATED + " INTEGER NOT NULL);"
				;
		
	    String CREATE_TABLE_CURRENTUSER =  "CREATE TABLE "+TABLE_CURRENTUSER+"("
		+ USER_ID + " INTEGER NOT NULL, "
		+ USER_NAME  + " TEXT );" ;
		
		
		
	
	public DatabaseHandler(Context context) {
		//Singleton Class to be used.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	public static DatabaseHandler getDatabaseObject(Context context)
	{
		if(db==null)
			db=new DatabaseHandler(context);
		return db;
	}
    @Override
    public void onCreate(SQLiteDatabase db) {
		
    	db.execSQL(CREATE_TABLE_LOGIN);
    	db.execSQL(CREATE_TABLE_ITEMS);
    	db.execSQL(CREATE_TABLE_CURRENTUSER);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENTUSER);
        // Create tables again
        onCreate(db);
    }

    public String getCurrentUser(){
       String temp="";
 	   SQLiteDatabase db = this.getReadableDatabase();
 	  String[] col=new String[]{USER_ID,USER_NAME};
 	   Cursor c = db.query(TABLE_CURRENTUSER, col, null,null,null,null,null);
 	  if(c!=null)
 	  {
 		  c.moveToFirst();
 		  temp=c.getString(c.getColumnIndex(col[1]));
 	  }
 	  c.close();
 	  db.close();
 	  return temp;
    }
    
   public int getCurrentUserId(String name)
   {
	   if(name==null)
		   return 0;
	   else
	   {
		   int temp=0;
		   SQLiteDatabase db = this.getReadableDatabase();
	   	   Cursor c = db.query(TABLE_LOGIN, new String[]{USER_NAME,USER_ID}, null,null,null,null,null);
		   for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		  	   if((c.getString(c.getColumnIndex(USER_NAME)).equals(name)))
	   		   {
	   		   		temp=c.getInt(c.getColumnIndex(USER_ID));
	   		   		break;
	   		   }
		   c.close();
		   db.close();
		   return temp;
	   }
   }
    
    public void updateCurrentUser(int id, String name){
    	//code to be written\
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	ContentValues values = new ContentValues();
	    	System.out.println("id="+id);
	    	System.out.println("name="+name);
	    	values.put(USER_ID, id);
	    	values.put(USER_NAME, name);
	    	db.delete(TABLE_CURRENTUSER,null,null);
	    	db.insert(TABLE_CURRENTUSER,null,values);
	    	db.close();
    }
    
    public String getCurrentUserData() {
		// TODO Auto-generated method stub
		String s="";
		String[] col=new String[]{USER_ID,USER_NAME};
		SQLiteDatabase OurDatabase=this.getReadableDatabase();
		Cursor c=OurDatabase.query(TABLE_CURRENTUSER, col, null,null,null,null,null);
		int []a=new int[2];
		for(int i=0;i<2;i++)
			a[i]=c.getColumnIndex(col[i]);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
			s=s+c.getString(a[0])+" "+c.getString(a[1])+"   1\n";
		c.close();
		OurDatabase.close();
		return s;
	}
    
    public void addUser(User user) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID,user.getID());
		values.put(USER_NAME,user.getUserName());
		values.put(USER_PASSWORD_HASH,hashByMD5(user.getPassword()));
		values.put(USER_EMAIL,user.geteMail());
        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }
    
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
   
    private String hashByMD5(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
    	MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashedBytes = digest.digest(password.getBytes("UTF-8"));
        return convertByteArrayToHexString(hashedBytes);
    }
    
    public int ValidateUser(String user_name,String password,String rememberme) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
    	//0 if not exists.
    	//1 if exists and valid.
    	//2 if user_name exists but not validated.
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	String temp_un,temp_pw;
        password=hashByMD5(password);
    	if(cursor.moveToFirst()){
    		do{
    			temp_un = cursor.getString(cursor.getColumnIndex(USER_NAME));
    			temp_pw = cursor.getString(cursor.getColumnIndex(USER_PASSWORD_HASH));
    			if(temp_un.equals(user_name) && temp_pw.equals(password))
    			{
    				cursor.close();
    		    	db.close();
    				return 1;
    			}
    			else if(temp_un.equals(user_name))
    			{
    				cursor.close();
    		    	db.close();
    				return 2;
    			}
    		}while(cursor.moveToNext());
    	}
    	cursor.close();
    	db.close();
    	return 0;
    }
    
    public String getUserData() {
		// TODO Auto-generated method stub
		String s="";
		String[] col=new String[]{USER_ID,USER_NAME,USER_PASSWORD_HASH,USER_EMAIL};
		SQLiteDatabase OurDatabase=this.getReadableDatabase();
		Cursor c=OurDatabase.query(TABLE_LOGIN, col, null,null,null,null,null);
		int []a=new int[4];
		for(int i=0;i<4;i++)
			a[i]=c.getColumnIndex(col[i]);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
			s=s+c.getString(a[0])+" "+c.getString(a[1])+" "+c.getString(a[2])+" "+c.getString(a[3])+"\n";
		c.close();
		OurDatabase.close();
		//System.out.println("inside getData()");
		//0System.out.println(s);
		return s;
	}
    
    
    public String getItemData() {
		// TODO Auto-generated method stub
		String s="";
		String[] col=new String[]{USER_NAME,ITEM_ID,ITEM_NAME,ITEM_DOB};
		SQLiteDatabase OurDatabase=this.getReadableDatabase();
		Cursor c=OurDatabase.query(TABLE_ITEMS, col, null,null,null,null,null);
		int []a=new int[4];
		for(int i=0;i<4;i++)
			a[i]=c.getColumnIndex(col[i]);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
			s=s+c.getString(a[0])+" "+c.getString(a[1])+" "+c.getString(a[2])+" "+c.getString(a[3])+"\n";
		c.close();
		OurDatabase.close();
		//System.out.println("inside getData()");
		//System.out.println(s);
		return s;
	}
    
    public List<ListObject> getDataForListView()
    {
        List<ListObject> codeLearnChaptersList = new ArrayList<ListObject>();
        codeLearnChaptersList.clear();
        String cur_user = getCurrentUser();
        
        String[] col=new String[]{USER_NAME,ITEM_NAME,ITEM_LASTTRACKED,ITEM_PICTURE};
		SQLiteDatabase OurDatabase=this.getReadableDatabase();
		Cursor c=OurDatabase.query(TABLE_ITEMS, col, null,null,null,null,null);
		if(c!=null)
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				
				if(cur_user.equals(c.getString(c.getColumnIndex(USER_NAME)))){
					ListObject chapter = new ListObject();
					////
					chapter.setObjectName(c.getString(c.getColumnIndex(ITEM_NAME)));
					chapter.setDateAndTime(c.getString(c.getColumnIndex(ITEM_LASTTRACKED)));
					chapter.setObjectPicture(c.getBlob(c.getColumnIndex(ITEM_PICTURE)));
					///
					codeLearnChaptersList.add(chapter);
				}
			}
		c.close();
		OurDatabase.close();
        return codeLearnChaptersList;
    }


    public void addItem(Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, item.getUserName());
        values.put(ITEM_NAME, item.getName());
        values.put(ITEM_DESCRIPTION, item.getDescription());
        values.put(ITEM_DOB, item.getDOB());
        values.put(ITEM_LASTTRACKED, item.getLastTracked());
        values.put(ITEM_PICTURE, item.getPicture());
        values.put(ITEM_ISLOST, item.isLost());
        values.put(ITEM_ELEASHON, item.iseLeashOn());
        values.put(ITEM_ELEASHTYPE, item.geteLeashType());
        values.put(ITEM_ELEASHRANGE, item.geteLeashRange());
        values.put(ITEM_MACADDRESS, item.getMacAddress());
        values.put(ITEM_ISUPDATED, 1);
        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }
    
    public void deleteItem(String name){
    	String uname = getCurrentUser();
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(TABLE_ITEMS, USER_NAME+" = ?, "+ ITEM_NAME+" = ?", new String[]{uname,name});
    	db.close();
    }

    public int getRowCount(String TABLE) {
        String countQuery = "SELECT  * FROM " + TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        return rowCount;
    }

    public void resetTables(String TABLE){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE, "1", null);
        db.close();
    }
}