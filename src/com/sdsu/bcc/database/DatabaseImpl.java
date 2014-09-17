package com.sdsu.bcc.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.sdsu.bcc.BCCConstants;

public class DatabaseImpl extends SQLiteOpenHelper implements BCCConstants {

	public DatabaseImpl(Context context, CursorFactory factory) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public DatabaseImpl(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		try {
			backupDb();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/* Base version of the Database */
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CATEGORY(ID INTEGER PRIMARY KEY, TYPE TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_IMAGE_INFO(ID INTEGER PRIMARY KEY, PATH TEXT, DATA BLOB)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS(ID INTEGER PRIMARY KEY, NAME TEXT, COMPANY TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_EMAIL(CONTACT_ID INTEGER, EMAIL TEXT, TYPE TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_PHONE(CONTACT_ID INTEGER, PHONE TEXT, TYPE TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_URL(CONTACT_ID INTEGER, URL TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_TRANSACTION(ID INTEGER PRIMARY KEY, CONTACT_ID INTEGER, IMAGE_ID INTEGER, CATEGORY_ID INTEGER, TRASACTION_DATE TEXT, FOREIGN KEY(IMAGE_ID) REFERENCES BCC_TB_IMAGE_INFO(ID), FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID), FOREIGN KEY(CATEGORY_ID) REFERENCES BCC_TB_CATEGORY(ID))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_FILES(ID INTEGER PRIMARY KEY, NAME TEXT,DESCRIPTION TEXT,CREATION_DATE TEXT, PATH TEXT)");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		//Log.d("Open Database", "Inside onOpen method");
		super.onOpen(db);
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CATEGORY(ID INTEGER PRIMARY KEY, TYPE TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_IMAGE_INFO(ID INTEGER PRIMARY KEY, PATH TEXT, DATA BLOB)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS(ID INTEGER PRIMARY KEY, NAME TEXT, COMPANY TEXT)");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_EMAIL(CONTACT_ID INTEGER, EMAIL TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_PHONE(CONTACT_ID INTEGER, PHONE TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_CONTACTS_URL(CONTACT_ID INTEGER, URL TEXT, RANK INTEGER, FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID))");
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_TRANSACTION(ID INTEGER PRIMARY KEY, CONTACT_ID INTEGER, IMAGE_ID INTEGER, CATEGORY_ID INTEGER, TRASACTION_DATE TEXT, FOREIGN KEY(IMAGE_ID) REFERENCES BCC_TB_IMAGE_INFO(ID), FOREIGN KEY(CONTACT_ID) REFERENCES BCC_TB_CONTACTS(ID), FOREIGN KEY(CATEGORY_ID) REFERENCES BCC_TB_CATEGORY(ID))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS BCC_TB_FILES(ID INTEGER PRIMARY KEY, NAME TEXT,DESCRIPTION TEXT,CREATION_DATE TEXT, PATH TEXT)");
		//Log.d("Open Database", "Returning onOpen method After Creating Tables");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private void backupDb() throws IOException {
	    File sd = Environment.getExternalStorageDirectory();
	    File data = Environment.getDataDirectory();

	    if (sd.canWrite()) {

	        String currentDBPath = "/data/com.bcc/databases/"+DATABASE_NAME;
	        String backupDBPath = "/bccdata/databases/"+DATABASE_NAME;

	        File currentDB = new File(data, currentDBPath);
	        File backupDB = new File(sd, backupDBPath);

	        if (backupDB.exists())
	            backupDB.delete();

	        if (currentDB.exists()) {
	            makeLogsFolder();

	            copy(currentDB, backupDB);
	       }

	        //dbFilePath = backupDB.getAbsolutePath();
	   }
	}
	
	private void makeLogsFolder() {
	    try {
	        File sdFolder = new File(Environment.getExternalStorageDirectory(), "/bccdata/databases/");
	        sdFolder.mkdirs();
	    }
	    catch (Exception e) {}
	}
	
	private void copy(File from, File to) throws FileNotFoundException, IOException {
	    FileChannel src = null;
	    FileChannel dst = null;
	    try {
	        src = new FileInputStream(from).getChannel();
	        dst = new FileOutputStream(to).getChannel();
	        dst.transferFrom(src, 0, src.size());
	    }
	    finally {
	        if (src != null)
	            src.close();
	        if (dst != null)
	            dst.close();
	    }
	}
}
