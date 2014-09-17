package com.sdsu.bcc.database.impl;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.DatabaseImpl;
import com.sdsu.bcc.database.DatabaseOperation;
import com.sdsu.bcc.database.data.ImageInformation;

public class ImageDataStore implements DatabaseOperation<ImageInformation>, BCCConstants {
	private DatabaseImpl m_objDB = null;
	private boolean isCloseDB = true;
	
	public ImageDataStore(Context context, boolean isCloseDB) {
		m_objDB = new DatabaseImpl(context);
		this.isCloseDB = isCloseDB;
	}
	
	public ImageDataStore(DatabaseImpl db, boolean isCloseDB) {
		m_objDB = db;
		this.isCloseDB = isCloseDB;
	}
	
	@Override
	public boolean insertRecords(List<ImageInformation> newRecords)
			throws Exception {
		
		Iterator<ImageInformation> itrRecords = newRecords.iterator();
		while(itrRecords.hasNext()) {
			ImageInformation data = itrRecords.next(); 
			this.insertRecords(data);
		}
		return true;
	}

	@Override
	public boolean updateRecords(List<ImageInformation> existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ImageInformation> getRecords(String[] queryArgs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageInformation viewRecord(String[] queryArgs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRecords(List<ImageInformation> removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNextId() throws Exception {
		int nextId = 0;
		if(null != m_objDB) {
			SQLiteDatabase db = m_objDB.getWritableDatabase();			
			Cursor result = db.rawQuery(sqlMaxImageId, null);
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
	public boolean insertRecords(ImageInformation newRecords) throws Exception {
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement(sqlInsertImageData);
		stmt.bindDouble(1, this.getNextId());
		stmt.bindString(2, newRecords.getImagePath());
		stmt.execute();
		stmt.close();
		if(isCloseDB)
			db.close();
		return true;
	}

	@Override
	public boolean updateRecords(ImageInformation existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRecords(ImageInformation removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
