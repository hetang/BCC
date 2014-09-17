package com.sdsu.bcc.database.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CardInformation implements Serializable {
	private int transactionId = -1;
	
	private CategoryInformation category;
	private ContactInformation contact;
	private ImageInformation image;
	
	public int getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	
	public ContactInformation getContact() {
		return contact;
	}
	public void setContact(ContactInformation contact) {
		this.contact = contact;
	}
	
	public CategoryInformation getCategory() {
		return category;
	}
	public void setCategory(CategoryInformation category) {
		this.category = category;
	}
	public ImageInformation getImage() {
		return image;
	}
	public void setImage(ImageInformation image) {
		this.image = image;
	}
	
}
