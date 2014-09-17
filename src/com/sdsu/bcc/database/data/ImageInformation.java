package com.sdsu.bcc.database.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ImageInformation implements Serializable {
	private int imageId = -1;
	private String imagePath;
	
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
