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
import android.util.Log;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.DatabaseImpl;
import com.sdsu.bcc.database.DatabaseOperation;
import com.sdsu.bcc.database.data.CardInformation;
import com.sdsu.bcc.database.data.CategoryInformation;
import com.sdsu.bcc.database.data.ContactInformation;
import com.sdsu.bcc.database.data.ImageInformation;

public class BCCDataStore implements DatabaseOperation<CardInformation>, BCCConstants {
	private static String TAG = "BCCDataStore";
	private DatabaseImpl m_objDB = null;
	private boolean isCloseDB = true;
	
	public BCCDataStore(Context context, boolean isCloseDB) {
		m_objDB = new DatabaseImpl(context);
		this.isCloseDB = isCloseDB;
	}
	
	@Override
	public boolean insertRecords(List<CardInformation> newRecords)
			throws Exception {
		
		
		Iterator<CardInformation> itrCardInfo = newRecords.iterator();
		while(itrCardInfo.hasNext()) {
			CardInformation data = itrCardInfo.next();
			this.insertRecords(data);
		}
		return false;
	}

	@Override
	public boolean updateRecords(List<CardInformation> existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<CardInformation> getRecords(String[] queryArgs) throws Exception {
		List<CardInformation> records = new ArrayList<CardInformation>();
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		Cursor result = db.rawQuery(sqlGetAllContactsTransaction, queryArgs);
		int rowCount = result.getCount();
		Log.v(TAG,"rowCount = " + rowCount);
		if(rowCount > 0) {
			result.moveToFirst();
			for(int i=0;i<rowCount;i++){
				result.moveToPosition(i);
				CardInformation objCardInfo = new CardInformation();
				CategoryInformation objCategory = new CategoryInformation();
				ContactInformation objContact = new ContactInformation();
				ImageInformation objImage = new ImageInformation();
				
				int contactId = result.getInt(1);
				objCardInfo.setTransactionId(result.getInt(0));
				objContact.setContactId(contactId);
				objImage.setImageId(result.getInt(2));
				objCategory.setId(result.getInt(3));
				objContact.setName(result.getString(4));
				objContact.setCompany(result.getString(5));
				objImage.setImagePath(result.getString(6));
				objCategory.setType(result.getString(7));
				
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
				
				objCardInfo.setCategory(objCategory);
				objCardInfo.setImage(objImage);
				objCardInfo.setContact(objContact);
				records.add(objCardInfo);
			}
		}
		result.close();
		db.close();
		return records;
	}

	@Override
	public CardInformation viewRecord(String[] queryArgs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRecords(List<CardInformation> removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNextId() throws Exception {
		int nextId = 0;
		if(null != m_objDB) {
			SQLiteDatabase db = m_objDB.getWritableDatabase();			
			Cursor result = db.rawQuery(sqlMaxTransactionId, null);
			if(result.moveToFirst()) {
				nextId = result.getInt(0) + 1;
				result.close();
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
	public boolean insertRecords(CardInformation newRecords) throws Exception {
		ContactDataStore objContactStore = new ContactDataStore(m_objDB,false);
		ImageDataStore objImageStore = new ImageDataStore(m_objDB,false);
		CategoryDataStore objCategoryStore = new CategoryDataStore(m_objDB,false);
		
		int categoryId = -1;
		int imageId = -1;
		int contactId = objContactStore.getNextId();
		
		objContactStore.insertRecords(newRecords.getContact()); // If Error Comes for Database already Closed then See db.close() method Inside Contact Store
		
		if(newRecords.getImage() != null && newRecords.getImage().getImageId() == -1) {
			imageId = objImageStore.getNextId();
			objImageStore.insertRecords(newRecords.getImage());
		}
		
		if(newRecords.getCategory() != null && newRecords.getCategory().getId() == -1) {
			categoryId = objCategoryStore.getNextId();
			objCategoryStore.insertRecords(newRecords.getCategory());
		}
		
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement(sqlInsertContactTransaction);
		stmt.bindDouble(1, this.getNextId());
		stmt.bindDouble(2, contactId);
		stmt.bindDouble(3, imageId);
		stmt.bindDouble(4, categoryId);
		stmt.execute();
		
		stmt.close();
		
		if(isCloseDB)
			db.close();
		return false;
	}

	@Override
	public boolean updateRecords(CardInformation existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRecords(CardInformation removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
