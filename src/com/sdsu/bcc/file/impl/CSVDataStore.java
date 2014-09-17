package com.sdsu.bcc.file.impl;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.ContactInformation;
import com.sdsu.bcc.database.data.FilesInformation;
import com.sdsu.bcc.database.impl.FilesDataStore;
import com.sdsu.bcc.file.FileDataStoreOperations;

public class CSVDataStore implements FileDataStoreOperations<List<ContactInformation>>, BCCConstants{
	private static final String TAG = "CSVDataStore";
	private Activity m_activity = null;
	private String m_csvFileType = "Default";
	private String m_fileName = null;
	private boolean m_storeToSDCard = false;
	private FilesDataStore m_fileStore = null;
	
	@Override
	public boolean saveData(List<ContactInformation> pObjData) throws Exception {
		File rootDir = null;
		File file = null;
		FileWriter Writer = null;
		
		try {
			Log.v("CSVDataStore","Inside saveData : pObjData = " + pObjData);
			
			if(pObjData.size() > 0) {
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.m_activity);
				this.m_csvFileType = sharedPrefs.getString("csv_file_format", "Default");
				Log.v(TAG,"File Type = " + sharedPrefs.getString("csv_file_format", "Default"));
				this.m_fileName = sharedPrefs.getString("change_file_name", "export_file_");
				this.m_storeToSDCard = sharedPrefs.getBoolean("save_to_sd_card", true);
				
				Log.v("CSVDataStore", "this.m_fileName = " + this.m_fileName);
				
				if(m_storeToSDCard) {
					rootDir = new File(Environment.getExternalStorageDirectory()+EXPORT_PATH);
					rootDir.mkdirs();
				} else {
					ContextWrapper cw = new ContextWrapper(this.m_activity);
					rootDir = cw.getDir("media", Context.MODE_WORLD_READABLE);
				}
				
				String fileName = getFileName(this.m_fileName);
				Log.v("CSVDataStore","fileName = " + fileName);
				file = new File(rootDir, fileName);
				Log.v("","File Path = " + file.getAbsolutePath());
	
				Writer = new FileWriter(file);
				Writer.append(getCSVContent(pObjData));
				
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				
				FilesInformation objFileInfo = new FilesInformation();
				objFileInfo.setFileName(fileName);
				objFileInfo.setCreationDate(dateFormat.format(cal.getTime()).toString());
				objFileInfo.setFilePath(file.getAbsolutePath());
				
				m_fileStore.insertRecords(objFileInfo);
			} else {
				return false;
			}
		} finally {
			if(Writer != null) {
				Writer.flush();
				Writer.close();
				Writer = null;
			}
		}
		
		return true;
	}

	private String getCSVContent(List<ContactInformation> pObjData) {
		Log.v(TAG,"File Type = " + m_csvFileType);
		CSVFileType type = CSVFileType.valueOf(m_csvFileType);
		
		switch(type) {
			case Default:
				return getDefaultCSVContent(pObjData);
			case Google: 
				return getGoogleCSVContent(pObjData);
			case Outlook:
				return getOutlookCSVContent(pObjData);
			case Apple:
				return getAppleVcardContent(pObjData);
		}
		
		return null;
	}

	private String getAppleVcardContent(List<ContactInformation> pObjData) {
		StringBuffer result = new StringBuffer();
		for (int i=0;i<pObjData.size();i++){
			ContactInformation data = pObjData.get(i);
			result.append("BEGIN:VCARD\n");
			result.append("VERSION:3.0\n");
			result.append("FN:").append(data.getName()).append("\n");
			
			List<Map<String,String>> emailData = data.getEmails();
			if(emailData.size() > 0) {
				result.append("EMAIL;TYPE=");
			}
			for(int j=0;j<emailData.size();j++) {
				Map<String,String> emails = emailData.get(j);
				result.append(emails.get("emailType")).append(":").append(emails.get("emailId")).append(";");
			}
			if(emailData.size() > 0) {
				result.append("\n");
			}
			
			List<Map<String,String>> phoneData = data.getPhone();
			if(phoneData.size() > 0) {
				result.append("TEL;TYPE=");
			}
			for(int j=0;j<phoneData.size();j++) {
				Map<String,String> phones = phoneData.get(j);
				result.append(phones.get("phoneType")).append(":").append(phones.get("phoneNumber")).append(";");
			}
			if(phoneData.size() > 0) {
				result.append("\n");
			}
			result.append("END:VCARD\n");
		}
		return result.toString();
	}

	private String getOutlookCSVContent(List<ContactInformation> pObjData) {
		StringBuffer result = new StringBuffer();
		//StringBuffer data = new StringBuffer();
		result.append(outlookCSVHeader);
		result.append("\n");
		for (int i=0;i<pObjData.size();i++){
			ContactInformation data = pObjData.get(i);
			result.append(data.getName());
			result.append(",,,,,,,,,,,,,");
			
			List<Map<String,String>> emailData = data.getEmails();
			for(int j=0;j<3;j++) {
				if (emailData.size() > j) {
					Map<String,String> emails = emailData.get(j);
					result.append(emails.get("emailId"));
				}
				result.append(",");
			}
			
			List<Map<String,String>> phoneData = data.getPhone();
			for(int j=0;j<6;j++) {
				if (phoneData.size() > j) {
					Map<String,String> phones = phoneData.get(j);
					//result.append(phones.get("phoneType"));
					//result.append(",");
					result.append(phones.get("phoneNumber"));
				}
				result.append(",");
			}
			
			result.append(",,,,,,,,,,,,,,,,,,,");
			result.append(data.getCompany());
			result.append(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n");
		}
		return result.toString();
	}

	private String getGoogleCSVContent(List<ContactInformation> pObjData) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getDefaultCSVContent(List<ContactInformation> pObjData) {
		StringBuffer result = new StringBuffer();
		//StringBuffer data = new StringBuffer();
		result.append(defaultCSVHeader);
		result.append("\n");
		
		for (int i=0;i<pObjData.size();i++){
			ContactInformation data = pObjData.get(i);
			result.append(data.getName());
			result.append(",");
			result.append(data.getCompany());
			result.append(",");
			
			List<Map<String,String>> emailData = data.getEmails();
			for(int j=0;j<3;j++) {
				if (emailData.size() > j) {
					Map<String,String> emails = emailData.get(j);
					result.append(emails.get("emailType"));
					result.append(",");
					result.append(emails.get("emailId"));
				} else {
					result.append(",");
				}
				result.append(",");
			}
			
			List<Map<String,String>> phoneData = data.getPhone();
			for(int j=0;j<6;j++) {
				if (phoneData.size() > j) {
					Map<String,String> phones = phoneData.get(j);
					result.append(phones.get("phoneType"));
					result.append(",");
					result.append(phones.get("phoneNumber"));
				} else {
					result.append(",");
				}
				result.append(",");
			}
			result.deleteCharAt(result.length()-1);
			result.append("\n");
		}
		Log.v("CSVDataStore : getDefaultCSVContent","Result String is : " + result.toString());
		return result.toString();
	}

	@Override
	public List<ContactInformation> getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivity(Activity activity) {
		this.m_activity = activity;
		m_fileStore = new FilesDataStore(activity,true);
	}
	
	private String getFileName(String pInitialName) {
		StringBuffer fileName = new StringBuffer(pInitialName);
		
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		Calendar cal = Calendar.getInstance();
		fileName.append(dateFormat.format(cal.getTime()).toString());
		Log.v(TAG,"File Type = " + m_csvFileType);
		CSVFileType type = CSVFileType.valueOf(m_csvFileType);
		
		switch(type) {
			case Default: case Google: case Outlook:
				fileName.append(".csv");
				break;
			case Apple:
				fileName.append(".vcf");
				break;
		}
		
		return fileName.toString();
	}
}
