package com.bcc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bcc.util.BCCUtil;
import com.bcc.util.adaptor.ManageListAdaptor;
import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.ContactInformation;
import com.sdsu.bcc.database.data.FilesInformation;
import com.sdsu.bcc.database.impl.ContactDataStore;
import com.sdsu.bcc.file.FileDataStoreFactory;
import com.sdsu.bcc.file.FileDataStoreOperations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ImportExportBCC extends Activity implements Button.OnClickListener, BCCConstants{
	private static final String TAG = "com.bcc.ImportExportBCC";
	
	private Button importButton;
	private Button exportButton;
	private Button manageButton;
	private String m_fileType = null;
	private boolean m_contactExtract = true;
	private Runnable fetchList;
	private ProgressDialog m_ProgressDialog = null;
	private ContactDataStore m_contactStore = null;
	private List<FilesInformation> fileInfos = new ArrayList<FilesInformation>();
	private List<String> fileType = new ArrayList<String> ();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.import_export);
		
		importButton = (Button)findViewById(R.id.import_button);
		importButton.setOnClickListener(this);
		
		exportButton = (Button)findViewById(R.id.export_button);
		exportButton.setOnClickListener(this);
		
		manageButton = (Button)findViewById(R.id.manage_ie_button);
		manageButton.setOnClickListener(this);
		
		fileType.add("csv");
		fileType.add("vcf");
		fileType.add("xml");
		fileType.add("json");
	}
	
	

	@Override
	protected void onResume() {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		m_fileType = sharedPrefs.getString("file_format", "CSV");
		m_contactExtract = sharedPrefs.getBoolean("create_contact", true);
		Log.v("ImportExportBCC","m_fileType = " + m_fileType);
		
		m_contactStore = new ContactDataStore(this,true);
		
		try {
			fileInfos = BCCUtil.buildFileList(TAG,fileType,EXPORT_PATH);
			Log.v(TAG,"fileInfos = " + fileInfos);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		super.onResume();
	}



	@Override
	public void onClick(View v) {
		System.out.println("View v = " + v);
		Log.d("View v = " + v,"Button Log");
		
		if(v == manageButton) {
			Intent myIntent = new Intent(v.getContext(), ImportExportManage.class);
			startActivityForResult(myIntent, 0);
		} else if(v == exportButton) {
			fetchList = new Runnable(){
	            @Override
	            public void run() {
	            	try {
						createExportFile();
					} catch (Exception e) {
						//Log.e(TAG,e.getMessage());
						e.printStackTrace();
						runOnUiThread(returnResponse);
					}
	            }
	        };
	        Thread thread =  new Thread(null, fetchList, "ExportListFetchBackground");
	        thread.start();
	        m_ProgressDialog = ProgressDialog.show(ImportExportBCC.this,    
	              "Please wait...", "Exporting Data to File ...", true);
		} else if(v == importButton) {
			populateImportFiles();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
	
	private Runnable returnResponse = new Runnable() {
        @Override
        public void run() {
            m_ProgressDialog.dismiss();
        }
    };
	
	private void createExportFile() throws Exception {
		List<ContactInformation> phoneContactInfo = null;
		if(m_contactExtract) {
			phoneContactInfo = getContactsFromPhone();
			Log.v("ImportExportBCC","contactInfo = " + phoneContactInfo);
		}
		
		List<ContactInformation> appContactInfo = m_contactStore.getRecords(null);
		
		List<ContactInformation> contactInfo = new ArrayList<ContactInformation>();
		contactInfo.addAll(appContactInfo);
		if(null != phoneContactInfo) {
			contactInfo.addAll(phoneContactInfo);
		}
		
		FileDataStoreFactory objFactory = FileDataStoreFactory.getFileStore(this);
		@SuppressWarnings("unchecked")
		FileDataStoreOperations<List<ContactInformation>> objStore = (FileDataStoreOperations<List<ContactInformation>>) objFactory.getFileStore(m_fileType,this);
		objStore.saveData(contactInfo);
		
		Thread.sleep(500);
		runOnUiThread(returnResponse);
	}
	
	private List<ContactInformation> getContactsFromPhone() {
		List<ContactInformation> result = new ArrayList<ContactInformation>();
		
		Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null); 
		
		while (cursor.moveToNext()) {
			ContactInformation objContact = new ContactInformation();
			
			String contactId = cursor.getString(cursor.getColumnIndex( 
			ContactsContract.Contacts._ID));
			int nameFieldColumnIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameFieldColumnIndex);
			
			objContact.setName(contact);
			
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
			Log.v("ImportExportBCC","hasPhone = " + hasPhone);
			if (Integer.parseInt(hasPhone) > 0) {
				Log.v("ImportExportBCC","Inside Has Phone");
				List<Map<String,String>> phoneList = new ArrayList<Map<String,String>>();
				// You know it has a number so now query it like this
				Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
				while (phones.moveToNext()) {
					Map<String,String> phoneData = new HashMap<String,String>();
					String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
					int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
					Log.v("ImportExportBCC", "phoneNumber = " + phoneNumber);
					phoneData.put("phoneNumber",phoneNumber);
					phoneData.put("phoneType",String.valueOf(phoneType));
					phoneList.add(phoneData);
				} 
				phones.close();
				objContact.setPhone(phoneList);
			}
			
			List<Map<String,String>> emailList = new ArrayList<Map<String,String>>();
			Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
			while (emails.moveToNext()) {
				// This would allow you get several email addresses
				Map<String,String> emailData = new HashMap<String,String>();
				String emailAddress = emails.getString( 
				emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				int emailType = emails.getInt(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				emailData.put("emailId", emailAddress);
				emailData.put("emailType", String.valueOf(emailType));
				emailList.add(emailData);
			} 
			emails.close();
			objContact.setEmails(emailList);
			result.add(objContact);
		}
		cursor.close(); 
		return result;
	}
	
	private void populateImportFiles() {
		ManageListAdaptor arrayAdapter = new ManageListAdaptor(this,fileInfos);
		arrayAdapter.setFileNameColor("#000000");
		arrayAdapter.setCreationDateColor("#0001C1");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.import_contacts);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), fileInfos.get(which).getFileName(), Toast.LENGTH_SHORT).show();
				return;
			}
            
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	@Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
	}
	
	/*private void buildFileList() throws NameNotFoundException {
		fileInfos = new ArrayList<FilesInformation>();
        //File sharedPrefsDir = new File(appInfo.dataDir + "/shared_prefs");
        File sharedPrefsDir = Environment.getExternalStorageDirectory();
        
        FileFilter filter = new FileFilter() { 
            public boolean accept(File pathname) {
            	String name = pathname.getName().toLowerCase();
            	if(name.lastIndexOf("csv") != -1) {
            		return true;
            	} else if(name.lastIndexOf("xml") != -1) {
            		return true;
            	} else if(name.lastIndexOf("json") != -1) {
            		return true;
            	} 
				return false;
            } 
        }; 
        File[] prefFiles = sharedPrefsDir.listFiles(filter);
        Log.v(TAG,"File List = " + prefFiles.length);
        for (File f : prefFiles) { 
        	FilesInformation fileInfo = new FilesInformation();
        	fileInfo.setFilePath(f.getAbsolutePath());
        	fileInfo.setFileName(f.getName());
        	fileInfos.add(fileInfo);
        }
    } */
}
