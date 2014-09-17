package com.sdsu.bcc.database.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ContactInformation implements Serializable {
	
	private int contactId = -1;
	private String name = "";
	private String company = "";
	private List<Map<String,String>> emails = null;
	private List<Map<String,String>> phone = null;
	private List<String> urls = null;
	
	public ContactInformation() {
		emails = new ArrayList<Map<String,String>>();
		phone = new ArrayList<Map<String,String>>();
		urls = new ArrayList<String>();
	}
	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		if(null != company) {
			return company;
		} else {
			return "";
		}
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public List<Map<String,String>> getEmails() {
		return emails;
	}
	public void setEmails(List<Map<String,String>> emails) {
		this.emails = emails;
	}
	public List<Map<String,String>> getPhone() {
		return phone;
	}
	public void setPhone(List<Map<String,String>> phone) {
		this.phone = phone;
	}
	public List<String> getUrls() {
		return urls;
	}
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	
	public void addEmail(Map<String,String> email){
		this.emails.add(email);
	}
	
	public void addEmail(Map<String,String> email, int order){
		this.emails.add(order,email);
	}
	
	public void addPhone(Map<String,String> phone){
		this.phone.add(phone);
	}
	
	public void addPhone(Map<String,String> phone, int order) {
		this.phone.add(order,phone);
	}
	
	public void addUrl(String url){
		this.urls.add(url);
	}
	
	public void addUrl(String url,int order) {
		this.urls.add(order,url);
	}
	
	@Override
	public String toString() {
		return "name = " + name + " phone = " + phone + " emails = " + emails;
	}
}
