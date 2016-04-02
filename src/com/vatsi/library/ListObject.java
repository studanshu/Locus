package com.vatsi.library;

public class ListObject{
	private String objectName;
    private String dateAndTime;
    private byte[] objectPicture;
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getDateAndTime() {
		return dateAndTime;
	}
	public void setDateAndTime(String dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	public byte[] getObjectPicture() {
		return objectPicture;
	}
	public void setObjectPicture(byte[] objectPicture) {
		this.objectPicture = objectPicture;
	}
}