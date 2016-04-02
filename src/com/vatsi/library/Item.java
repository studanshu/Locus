package com.vatsi.library;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Item {
	private String userName,name,description,macAddress,path;
	private Drawable picture;
	private int eLeashRange,eLeashType,ID,userID;
	private Calendar DOB,lastTracked;
	private boolean isLost,eLeashOn;
	
	
	
	
	
	
	public Item() {
		this.userName = null;
		this.name = null;
		this.description = null;
		this.picture = null;
		this.macAddress = null;
		this.eLeashRange = 0;
		this.eLeashType = 0;
		ID = 0;
		this.userID = 0;
		DOB = null;
		this.lastTracked = null;
		this.isLost = false;
		this.eLeashOn = false;
	}
	
	public Item(String userName,int iD, String name, String description,
			int userID, Calendar dOB, Calendar lastTracked,Drawable picture,
			boolean isLost, boolean eLeashOn, int eLeashType, int eLeashRange,String macAddress,String path) {
		this.userName = userName;
		this.name = name;
		this.description = description;
		this.picture = picture;
		this.macAddress = macAddress;
		this.eLeashRange = eLeashRange;
		this.eLeashType = eLeashType;
		ID = iD;
		this.userID = userID;
		DOB = dOB;
		this.lastTracked = lastTracked;
		this.isLost = isLost;
		this.eLeashOn = eLeashOn;
		this.path=path;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static byte[] drawableToByteArray(Drawable d)
	{
		if(d!=null)
		{
			Bitmap ib=((BitmapDrawable) d).getBitmap();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ib.compress(Bitmap.CompressFormat.PNG,100, baos);
			byte[] bd=baos.toByteArray();
			return bd;
		}
		else
			return null;
	}
	public byte[] getPicture() {
		return drawableToByteArray(picture);
	}
	public void setPicture(Drawable picture) {
		this.picture = picture;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public int geteLeashRange() {
		return eLeashRange;
	}
	public void seteLeashRange(int eLeashRange) {
		this.eLeashRange = eLeashRange;
	}
	public int geteLeashType() {
		return eLeashType;
	}
	public void seteLeashType(int eLeashType) {
		this.eLeashType = eLeashType;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	private String getDateTime(Calendar cal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(cal.getTime());
	}
	public String getDOB() {
		return getDateTime(DOB);
	}
	public void setDOB(Calendar dOB) {
		DOB = dOB;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLastTracked() {
		return getDateTime(lastTracked);
	}
	public void setLastTracked(Calendar lastTracked) {
		this.lastTracked = lastTracked;
	}
	public boolean isLost() {
		return isLost;
	}
	public void setLost(boolean isLost) {
		this.isLost = isLost;
	}
	public boolean iseLeashOn() {
		return eLeashOn;
	}
	public void seteLeashOn(boolean eLeashOn) {
		this.eLeashOn = eLeashOn;
	}
}
