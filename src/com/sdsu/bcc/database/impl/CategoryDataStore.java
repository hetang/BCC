package com.sdsu.bcc.database.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.DatabaseImpl;
import com.sdsu.bcc.database.DatabaseOperation;
import com.sdsu.bcc.database.data.CategoryInformation;

public class CategoryDataStore implements DatabaseOperation<CategoryInformation>, BCCConstants {
	
	private DatabaseImpl m_objDB = null;
	private boolean isCloseDB = true;
	
	public CategoryDataStore(Context context, boolean isCloseDB) {
		m_objDB = new DatabaseImpl(context);
		this.isCloseDB = isCloseDB;
	}
	
	public CategoryDataStore(DatabaseImpl db, boolean isCloseDB) {
		m_objDB = db;
		this.isCloseDB = isCloseDB;
	}
	
	@Override
	public boolean insertRecords(List<CategoryInformation> newRecords)
			throws Exception {
		
		Iterator<CategoryInformation> itrRecords = newRecords.iterator();
		while(itrRecords.hasNext()) {
			CategoryInformation data = itrRecords.next(); 
			this.insertRecords(data);
		}
		return true;
	}

	@Override
	public boolean updateRecords(List<CategoryInformation> existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<CategoryInformation> getRecords(String[] queryArgs)
			throws Exception {
		List<CategoryInformation> records = new ArrayList<CategoryInformation>();
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		Cursor result = db.rawQuery(sqlGetAllCategory, queryArgs);
		int rowCount = result.getCount();
		if(rowCount > 0) {
			result.moveToFirst();
			for(int i=0;i<rowCount;i++){
				result.moveToPosition(i);
				CategoryInformation objCategoryInfo = new CategoryInformation();
				objCategoryInfo.setId(result.getInt(0));
				objCategoryInfo.setType(result.getString(1));
				records.add(objCategoryInfo);
			}
		}
		result.close();
		db.close();
		return records;
	}

	@Override
	public CategoryInformation viewRecord(String[] queryArgs) throws Exception {
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		Cursor result = db.rawQuery(sqlGetAllCategory, queryArgs);
		int rowCount = result.getCount();
		if(rowCount > 0) {
			result.moveToFirst();
			CategoryInformation objCategoryInfo = new CategoryInformation();
			objCategoryInfo.setId(result.getInt(0));
			objCategoryInfo.setType(result.getString(1));
			result.close();
			db.close();
			return objCategoryInfo;
		}
		result.close();
		db.close();
		return null;
	}

	@Override
	public boolean deleteRecords(List<CategoryInformation> removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNextId() throws Exception {
		int nextId = 0;
		if(null != m_objDB) {
			SQLiteDatabase db = m_objDB.getWritableDatabase();			
			Cursor result = db.rawQuery(sqlMaxCategoryId, null);
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
	public boolean insertRecords(CategoryInformation newRecords)
			throws Exception {
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement(sqlInsertCategoryData);
		stmt.bindDouble(1, this.getNextId());
		stmt.bindString(2, newRecords.getType());
		stmt.execute();
		stmt.close();
		if(isCloseDB)
			db.close();
		return true;
	}

	@Override
	public boolean updateRecords(CategoryInformation existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRecords(CategoryInformation removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
