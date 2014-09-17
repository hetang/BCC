package com.bcc;

import com.itwizard.mezzofanti.DownloadManager;
import com.itwizard.mezzofanti.Mezzofanti;
import com.itwizard.mezzofanti.OCR;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

public class SetPrefs extends PreferenceActivity {
	
	private static final String TAG = "SetPrefs.java: ";
	
	public static final String KEY_SET_OCR_LANGUAGE = "preferences_set_OCR_language";
	public static final String KEY_DOWNLOAD_LANGUAGE = "preferences_download_language";
	public static final String KEY_SPEED_QUALITY = "preferences_speed_quality";
	
	private ListPreference fileFormat = null;
	private ListPreference csvFileFormat = null;
	private ListPreference m_lpSpeedQuality;
	private ListPreference m_lpSetOcrLanguage;
	private ListPreference m_lpDownloadLanguage;
	
	private DownloadManager m_DownloadManager = null;
	private ProgressDialog m_ProgressDialog = null;
	private SharedPreferences m_AppSharedPrefs = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        PreferenceScreen preferences = getPreferenceScreen();
        m_AppSharedPrefs = preferences.getSharedPreferences();
        
        fileFormat = (ListPreference) findPreference("file_format");
        csvFileFormat = (ListPreference) findPreference("csv_file_format");
        
        m_lpSetOcrLanguage = (ListPreference) preferences.findPreference(KEY_SET_OCR_LANGUAGE);
	    m_lpDownloadLanguage = (ListPreference) preferences.findPreference(KEY_DOWNLOAD_LANGUAGE);
	    m_lpSpeedQuality = (ListPreference) preferences.findPreference(KEY_SPEED_QUALITY);
	    
	    CharSequence entries[] = new CharSequence[2];
	    CharSequence entriesLarge[] = new CharSequence[2];
	    entriesLarge[0] = getString(R.string.preferencesactivity_imgsz_optimal);
	    entriesLarge[1] = getString(R.string.preferencesactivity_imgsz_medium);
	    entries[0] = "2";
	    entries[1] = "4";
	    m_lpSpeedQuality.setEntries(entriesLarge);
	    m_lpSpeedQuality.setEntryValues(entries);
	    m_lpSpeedQuality.setValue("" + OCR.mConfig.GetImgDivisor());
	    
        fileFormat.setOnPreferenceChangeListener(new
        		Preference.OnPreferenceChangeListener() {
        		  public boolean onPreferenceChange(Preference preference, Object newValue) {
        		    final String val = newValue.toString();
        		    int index = fileFormat.findIndexOfValue(val);
        		    if(index==0)
        		    	csvFileFormat.setEnabled(true);
        		    else
        		    	csvFileFormat.setEnabled(false);
        		    return true;
        		  }
        		});
        
        m_DownloadManager = new DownloadManager();
		m_DownloadManager.SetMessageHandler(m_LocalMessageHandler);
	}
	
	@Override
	protected void onResume() {
		CreateDownloadableLangsSubMenu();
        
        CreateValidLangsSubMenu();
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Create the downloadable-languages submenu.
	 */
	private void CreateDownloadableLangsSubMenu()
	{
		if (m_DownloadManager.DownloadLanguageBrief(Mezzofanti.DOWNLOAD_URL, "languages.txt"))
		{
			// downloaded file correctly
			OCR.ReadAvailableLanguages();
			int len = m_DownloadManager.m_ServerLanguages.length;
		    CharSequence entriesLarge[] = new CharSequence[len];
		    CharSequence entries[] = new CharSequence[len];
		    for (int i=0; i<len; i++)
		    {
		    	if (OCR.mConfig.IsLanguageInstalled(m_DownloadManager.m_ServerLanguages[i].sExtName))
		    		entriesLarge[i] = m_DownloadManager.m_ServerLanguages[i].sFullName + " - " + (m_DownloadManager.m_ServerLanguages[i].lDownloadSz/1024) + "KB" + getString(R.string.preferencesactivity_reinstall);
		    	else
		    		entriesLarge[i] = m_DownloadManager.m_ServerLanguages[i].sFullName + " - " + (m_DownloadManager.m_ServerLanguages[i].lDownloadSz/1024) + "KB";
			    entries[i] = "" + i;
		    }		    
		    m_lpDownloadLanguage.setEntries(entriesLarge);
		    m_lpDownloadLanguage.setEntryValues(entries);
		    
		    m_lpDownloadLanguage.setOnPreferenceChangeListener(
		    	new OnPreferenceChangeListener() 
			    {
					//@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) 
					{
						m_LocalMessageHandler.sendEmptyMessage(R.id.preferences_selectedLang2Download);
						return true;
					}
				}
		    );
		    
		}
		else
		{
		    CharSequence entriesLarge[] = new CharSequence[1];
		    entriesLarge[0] = getString(R.string.preferencesactivity_cannotaccessinternet);
		    m_lpDownloadLanguage.setEntries(entriesLarge);
		    m_lpDownloadLanguage.setEntryValues(entriesLarge);
			ShowAlert(getString(R.string.preferencesactivity_warning), getString(R.string.preferencesactivity_problems));
		}    	
	}
	
	/**
	   * Show an alert on the screen.
	   * @param title the alert title
	   * @param message the alert body
	   */
	  private void ShowAlert(String title, String message)
	  {
		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	      builder.setTitle(title);
	      builder.setMessage(message);
	      builder.setPositiveButton(R.string.preferences_restore_factory_settings_button_ok, null);
	      builder.show();							  
	  }
	  
	  /**
		 * create a progress dialog for the download menu
		 * @param lang the language that is downloaded
		 */
		private void CreateProgressDialog(CharSequence lang)
		{
			
			m_ProgressDialog = new ProgressDialog(this);
			m_ProgressDialog.setTitle(R.string.preferencesactivity_pd_title);
			m_ProgressDialog.setMessage(getString(R.string.preferencesactivity_pd_body1) + " " + lang);
			m_ProgressDialog.setCancelable(true);
			m_ProgressDialog.setMax(100);
			m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			m_ProgressDialog.show();
			
			m_ProgressDialog.setOnCancelListener(new OnCancelListener() {
	    		public void onCancel(DialogInterface dialog) 
	    		{
	    			m_DownloadManager.CancelDownload();    			
		        }    		    		
	    	});		
		}
		
		/**
	     * Create the available-languages submenu.
	     */
	    private void CreateValidLangsSubMenu()
	    {
	    	OCR.ReadAvailableLanguages();
		    String[] svLangs = OCR.mConfig.GetvLanguages();
		    Log.v(TAG,"svLangs = " + svLangs + " OCR.mConfig.m_asLanguages = " + OCR.mConfig.m_asLanguages);
		    m_lpSetOcrLanguage.setEntries(svLangs);
		    m_lpSetOcrLanguage.setEntryValues(OCR.mConfig.m_asLanguages);
		    m_lpSetOcrLanguage.setValue(OCR.mConfig.GetLanguage());
		    m_lpSetOcrLanguage.setOnPreferenceChangeListener(
			    	new OnPreferenceChangeListener() 
				    {
						//@Override
						public boolean onPreferenceChange(Preference preference, Object newValue) 
						{
							Log.v(TAG,"newValue = " + newValue);
							OCR.get().SetLanguage((String)newValue);
							return true;
						}
					}
			    );
	    }
	  
	  /**
		 * the local message handler
		 */
		private Handler m_LocalMessageHandler = new Handler() 
		{
			
			@Override
			public void handleMessage(Message msg) 
			{
				switch(msg.what)
				{
					// download manager started unziping
					case R.id.downloadmanager_unziping:
						m_ProgressDialog.setMessage(getString(R.string.preferencesactivity_pd_body2));					
						break;
						
					// download manager finished with an error
					case R.id.downloadmanager_downloadFinishedError:
						m_ProgressDialog.dismiss();
						ShowAlert(getString(R.string.preferencesactivity_download_title), getString(R.string.preferencesactivity_downloaderr_body));
						break;
						
					// download manager finished with an error
					case R.id.downloadmanager_downloadFinishedErrorSdcard:
						m_ProgressDialog.dismiss();
						ShowAlert(getString(R.string.preferencesactivity_download_title), getString(R.string.preferencesactivity_downloaderrsdcard_body));
						break;
						
					// download manager finished ok 
					case R.id.downloadmanager_downloadFinishedOK:
						m_ProgressDialog.dismiss();
						CreateValidLangsSubMenu();
						CreateDownloadableLangsSubMenu();
						ShowAlert(getString(R.string.preferencesactivity_download_title), getString(R.string.preferencesactivity_downloadok_body));
						int index = Integer.parseInt(m_lpDownloadLanguage.getValue());
						String lang = m_DownloadManager.m_ServerLanguages[index].sExtName;
						Log.v(TAG, "Installed " + lang);
						OCR.get().SetLanguage(lang);
						
						// save lang in file
				    	SharedPreferences.Editor spe = m_AppSharedPrefs.edit();
			        	spe.putString(KEY_SET_OCR_LANGUAGE, lang);
				    	spe.commit();    	
						
						Log.v(TAG, "mconfig lang=" + OCR.mConfig.GetLanguageMore());
						break;
						
					// user selected a language to download
					case R.id.preferences_selectedLang2Download:
						m_DownloadManager.DownloadLanguageJob(Integer.parseInt(m_lpDownloadLanguage.getValue()));
						CreateProgressDialog(m_lpDownloadLanguage.getEntry());			    	
						m_DownloadManager.SetProgressDialog(m_ProgressDialog);
						break;
				}
			}
		};
}
