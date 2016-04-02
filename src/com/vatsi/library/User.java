package com.vatsi.library;

public class User {
	private String userName;
	private int ID;
	private String eMail;
	private String password;
	
	public User(){
		userName = eMail = password = null;
		ID = 0;
	}

	public User(String userName, int iD, String eMail, String password) {
		this.userName = userName;
		ID = iD;
		this.eMail = eMail;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String geteMail() {
		return eMail;
	}

	public void seteMail(String eMail) {
		this.eMail = eMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
