package com.sdsu.bcc.database.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.DatabaseImpl;
import com.sdsu.bcc.database.DatabaseOperation;
import com.sdsu.bcc.database.data.ContactInformation;

public class ContactDataStore implements DatabaseOperation<ContactInformation>, BCCConstants {
	
	private DatabaseImpl m_objDB = null;
	private boolean isCloseDB = true;
	
	public ContactDataStore(Context context, boolean isCloseDB) {
		m_objDB = new DatabaseImpl(context);
		this.isCloseDB = isCloseDB;
	}
	
	public ContactDataStore(DatabaseImpl db, boolean isCloseDB) {
		m_objDB = db;
		this.isCloseDB = isCloseDB;
	}
	
	@Override
	public boolean insertRecords(List<ContactInformation> newRecords)
			throws Exception {
		
		Iterator<ContactInformation> itrRecords = newRecords.iterator();
		while(itrRecords.hasNext()) {
			ContactInformation data = itrRecords.next();
			this.insertRecords(data);
		}
		return true;
	}

	@Override
	public boolean updateRecords(List<ContactInformation> existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ContactInformation> getRecords(String[] queryArgs)
			throws Exception {
		List<ContactInformation> records = new ArrayList<ContactInformation>();
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		Cursor result = db.rawQuery(sqlGetAllContacts, queryArgs);
		int rowCount = result.getCount();
		if(rowCount > 0) {
			result.moveToFirst();
			for(int i=0;i<rowCount;i++){
				result.moveToPosition(i);
				ContactInformation objContact = new ContactInformation();
				
				int contactId = result.getInt(0);
				objContact.setContactId(contactId);
				objContact.setName(result.getString(1));
				objContact.setCompany(result.getString(2));
				
				Cursor resultEmail = db.rawQuery(sqlGetEmailForContact, new String[] {String.valueOf(contactId)});
				int rowCountEmail = resultEmail.getCount();
				if(rowCountEmail > 0) {
					resultEmail.moveToFirst();
					for(int j=0;j<rowCountEmail;j++){
						Map<String,String> emailData = new HashMap<String,String>();
						emailData.put("emailId", resultEmail.getString(0));
						emailData.put("emailType", resultEmail.getString(1));
						objContact.addEmail(emailData);
					}
				}
				resultEmail.close();
				
				Cursor resultPhone = db.rawQuery(sqlGetPhoneForContact, new String[] {String.valueOf(contactId)});
				int rowCountPhone = resultPhone.getCount();
				if(rowCountPhone > 0) {
					resultPhone.moveToFirst();
					for(int j=0;j<rowCountPhone;j++){
						Map<String,String> phoneData = new HashMap<String,String>();
						phoneData.put("phoneNumber",resultPhone.getString(0));
						phoneData.put("phoneType",resultPhone.getString(1));
						objContact.addPhone(phoneData);
					}
				}
				resultPhone.close();
				
				Cursor resultUrl = db.rawQuery(sqlGetUrlForContact, new String[] {String.valueOf(contactId)});
				int rowCountUrl = resultUrl.getCount();
				if(rowCountUrl > 0) {
					resultUrl.moveToFirst();
					for(int j=0;j<rowCountUrl;j++){
						objContact.addUrl(resultUrl.getString(0));
					}
				}
				resultUrl.close();
				records.add(objContact);
			}
		}
		
		result.close();
		db.close();
		return records;
	}

	@Override
	public ContactInformation viewRecord(String[] queryArgs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRecords(List<ContactInformation> removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNextId() throws Exception {
		int nextId = 0;
		if(null != m_objDB) {
			SQLiteDatabase db = m_objDB.getWritableDatabase();			
			Cursor result = db.rawQuery(sqlMaxContactsId, null);
			if(result.moveToFirst()) {
				nextId = result.getInt(0) + 1;
				result.close();
				//db.close();
			}
		}else {
			throw new Exception("Database Object is not Created");
		}
		return nextId;
	}
	
	@Override
	public void finalize() {
		if(m_objDB != null) {
			m_objDB.close();
			m_objDB = null;
		}
	}

	@Override
	public boolean insertRecords(ContactInformation newRecords)
			throws Exception {
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement(sqlInsertContactData);
		SQLiteStatement stmtEmail = db.compileStatement(sqlInsertContactEmailData);
		SQLiteStatement stmtPhone = db.compileStatement(sqlInsertContactPhoneData);
		SQLiteStatement stmtUrl = db.compileStatement(sqlInsertContactUrlData);
		
		int id = this.getNextId();
		stmt.bindDouble(1, id);
		stmt.bindString(2, newRecords.getName());
		stmt.bindString(3, newRecords.getCompany());
		stmt.execute();
		
		int index = 1;
		Iterator<Map<String,String>> itrEmails = newRecords.getEmails().iterator();
		while(itrEmails.hasNext()){
			stmtEmail.bindDouble(1, id);
			Map<String,String> emailData = itrEmails.next();
			stmtEmail.bindString(2, emailData.get("emailId"));
			stmtEmail.bindString(3, emailData.get("emailType"));
			stmtEmail.bindDouble(4, index++);
			stmtEmail.execute();
		}
		
		index = 1;
		Iterator<Map<String,String>> itrPhone = newRecords.getPhone().iterator();
		while(itrPhone.hasNext()){
			stmtPhone.bindDouble(1, id);
			Map<String,String> phoneData = itrPhone.next();
			stmtPhone.bindString(2, phoneData.get("phoneNumber"));
			stmtPhone.bindString(3, phoneData.get("phoneType"));
			stmtEmail.bindDouble(4, index++);
			stmtPhone.execute();
		}
		
		index = 1;
		Iterator<String> itrUrl = newRecords.getUrls().iterator();
		while(itrUrl.hasNext()){
			stmtUrl.bindDouble(1, id);
			stmtUrl.bindString(2, itrUrl.next());
			stmtEmail.bindDouble(3, index++);
			stmtUrl.execute();
		}
		
		stmt.close();
		stmtEmail.close();
		stmtPhone.close();
		stmtUrl.close();
		if(isCloseDB)
			db.close();
		
		return true;
	}

	@Override
	public boolean updateRecords(ContactInformation existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRecords(ContactInformation removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
