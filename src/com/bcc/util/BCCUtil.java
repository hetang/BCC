package com.bcc.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.ContactInformation;
import com.sdsu.bcc.database.data.FilesInformation;

public class BCCUtil implements BCCConstants{
	private static final String TAG = "BCCUtil";
	
	public static List<FilesInformation> buildFileList(String TAG, List<String> fileType, String path) throws NameNotFoundException {
		List<FilesInformation> fileInfos = new ArrayList<FilesInformation>();
        //File sharedPrefsDir = new File(appInfo.dataDir + "/shared_prefs");
		File sharedPrefsDir = null;
		if(null != path) {
			sharedPrefsDir = new File(Environment.getExternalStorageDirectory()+path);
		} else {
			sharedPrefsDir = Environment.getExternalStorageDirectory();
		}
        Log.v(TAG,"sharedPrefsDir = " + sharedPrefsDir);
        
        FileFilterImpl filter = new FileFilterImpl(fileType);
        
        File[] prefFiles = sharedPrefsDir.listFiles(filter);
        Log.v(TAG,"prefFiles = " + prefFiles);
        if(prefFiles != null) {
	        Log.v(TAG,"File List = " + prefFiles.length);
	        for (File f : prefFiles) { 
	        	FilesInformation fileInfo = new FilesInformation();
	        	fileInfo.setFilePath(f.getAbsolutePath());
	        	fileInfo.setFileName(f.getName());
	        	fileInfos.add(fileInfo);
	        }
        }
        return fileInfos;
    }
	
	public static long getPhoneContactId(Context context, String name) {
		// find "reader"'s contact 
		String select = String.format("%s=? AND %s='%s'", 
		        Data.DISPLAY_NAME, Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		String[] project = new String[] { Data.RAW_CONTACT_ID };
		Cursor c = context.getContentResolver().query(
		        Data.CONTENT_URI, project, select, new String[] { name }, null);

		long rawContactId = -1;
		if(c.moveToFirst()){
		    rawContactId = c.getLong(c.getColumnIndex(Data.RAW_CONTACT_ID));
		}
		c.close();
		
		return rawContactId;
	}
	
	public static boolean addUpdateContactToPhone(Context context, ContactInformation objContact) throws RemoteException, OperationApplicationException {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
		    .withValue(RawContacts.ACCOUNT_TYPE, null)
		    .withValue(RawContacts.ACCOUNT_NAME, null)
		    .build());
		ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
		    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
		    .withValue(StructuredName.DISPLAY_NAME, objContact.getName())
		    .build());
		
		List<Map<String,String>> phoneData = objContact.getPhone();
		for(int j=0;j<phoneData.size();j++) {
			Map<String,String> phones = phoneData.get(j);
			
			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
				    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				    .withValue(Phone.NUMBER, phones.get("phoneNumber"))
				    .withValue(Phone.TYPE, PhoneType.valueOf(phones.get("phoneType")).getPhoneTypeCode())                
				    .build());
		}
		
		List<Map<String,String>> emailData = objContact.getEmails();
		for(int j=0;j<emailData.size();j++) {
			Map<String,String> emails = emailData.get(j);
			
			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
				    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				    .withValue(Email.DATA, emails.get("emailId"))
				    .withValue(Email.TYPE, EmailType.valueOf(emails.get("emailType")).getEmailTypeCode())                
				    .build());
		}
		Log.v(TAG, "ops = " + ops);
		// For brevity, the try-catch statement is ignored.
		// Normally it's needed.
		context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        
		return true;
	}
}

class FileFilterImpl implements FileFilter {
	//private static final String TAG = "FileFilterImpl";
	private List<String> m_fileType = null;
	
	public FileFilterImpl(List<String> fileType) {
		m_fileType = fileType;
	}
	
	@Override
	public boolean accept(File pathname) {
    	String name = pathname.getName();
    	//Log.v(TAG, "name = " + name);
    	String extension = name.substring(name.lastIndexOf('.')+1);
    	//Log.v(TAG, "extension = " + extension);
    	if( null != extension) {
    		extension = extension.toLowerCase();
    		//Log.v(TAG, "m_fileType.contains(extension) = " + m_fileType.contains(extension));
        	if(m_fileType.contains(extension)) {
        		return true;
        	}
    	} 
		return false;
    }
	
}
