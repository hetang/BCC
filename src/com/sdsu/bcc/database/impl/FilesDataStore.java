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
import com.sdsu.bcc.database.data.FilesInformation;

public class FilesDataStore implements DatabaseOperation<FilesInformation>,
		BCCConstants {
	
	private DatabaseImpl m_objDB = null;
	private boolean isCloseDB = true;
	
	public FilesDataStore(Context context, boolean isCloseDB) {
		m_objDB = new DatabaseImpl(context);
		this.isCloseDB = isCloseDB;
	}
	
	public FilesDataStore(DatabaseImpl db, boolean isCloseDB) {
		m_objDB = db;
		this.isCloseDB = isCloseDB;
	}
	
	@Override
	public boolean insertRecords(List<FilesInformation> newRecords)
			throws Exception {
		Iterator<FilesInformation> itrRecords = newRecords.iterator();
		while(itrRecords.hasNext()) {
			FilesInformation data = itrRecords.next(); 
			this.insertRecords(data);
		}
		return true;
	}

	@Override
	public boolean insertRecords(FilesInformation newRecords) throws Exception {
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		SQLiteStatement stmt = db.compileStatement(sqlInsertFileInfo);
		stmt.bindDouble(1, this.getNextId());
		stmt.bindString(2, newRecords.getFileName());
		stmt.bindString(3, newRecords.getDescription());
		stmt.bindString(4, newRecords.getCreationDate());
		stmt.bindString(5, newRecords.getFilePath());
		stmt.execute();
		stmt.close();
		if(isCloseDB)
			db.close();
		return true;
	}

	@Override
	public boolean updateRecords(List<FilesInformation> existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateRecords(FilesInformation existingRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<FilesInformation> getRecords(String[] queryArgs)
			throws Exception {
		List<FilesInformation> records = new ArrayList<FilesInformation>();
		SQLiteDatabase db = m_objDB.getWritableDatabase();
		Cursor result = db.rawQuery(sqlGetAllFiles, queryArgs);
		int rowCount = result.getCount();
		if(rowCount > 0) {
			result.moveToFirst();
			for(int i=0;i<rowCount;i++){
				result.moveToPosition(i);
				FilesInformation objFilesInfo = new FilesInformation();
				objFilesInfo.setId(result.getInt(0));
				objFilesInfo.setFileName(result.getString(1));
				objFilesInfo.setDescription(result.getString(2));
				objFilesInfo.setCreationDate(result.getString(3));
				records.add(objFilesInfo);
			}
		}
		result.close();
		//db.close();
		return records;
	}

	@Override
	public FilesInformation viewRecord(String[] queryArgs) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRecords(List<FilesInformation> removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteRecords(FilesInformation removeRecords)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getNextId() throws Exception {
		int nextId = 0;
		if(null != m_objDB) {
			SQLiteDatabase db = m_objDB.getWritableDatabase();			
			Cursor result = db.rawQuery(sqlMaxFilesId, null);
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
}
