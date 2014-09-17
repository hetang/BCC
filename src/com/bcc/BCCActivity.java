package com.bcc;

import com.itwizard.mezzofanti.OCR;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class BCCActivity extends TabActivity implements OnTabChangeListener {
	
	// 'Menu' items
	@SuppressWarnings("unused")
	private Menu m_sPreferencesMenu = null;							
	private static final int PREFERENCES_BCC_ID = 0;
	private static final int HELP_ID = Menu.FIRST;
	private static final int FEEDBACK_ID = Menu.FIRST + 1;
	private static final int ABOUT_ID = Menu.FIRST + 2;
	private static final String TAG = "BCCActivity";
	private TabHost tabHost = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        layout.setBackgroundColor(Color.parseColor("#FF001E2F"));
        
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        
        TabSpec firstTabSpec = tabHost.newTabSpec("addNew");
        firstTabSpec.setContent(new Intent(this,AddBCC.class));
        firstTabSpec.setIndicator("Add New",getResources().getDrawable(R.drawable.ic_tab_add));
        tabHost.addTab(firstTabSpec);
        
        TabSpec secondTabSpec = tabHost.newTabSpec("importExport");
        secondTabSpec.setContent(new Intent(this,ImportExportBCC.class));
        secondTabSpec.setIndicator("Import / Export",getResources().getDrawable(R.drawable.ic_tab_import_export));
        tabHost.addTab(secondTabSpec);
        
        TabSpec thirdTabSpec = tabHost.newTabSpec("settings");
        thirdTabSpec.setContent(new Intent(this,SetPrefs.class));
        thirdTabSpec.setIndicator("Preferences",getResources().getDrawable(R.drawable.ic_tab_preference));
        tabHost.addTab(thirdTabSpec);
        
        tabHost.setup();
        Log.v(TAG,"Count: In Create = " + tabHost.getTabWidget().getChildCount());
        tabHost.setOnTabChangedListener(this);
        tabHost.setCurrentTab(0);
        this.onTabChanged(null);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		OCR.mConfig.GetSettings(prefs);
    }
    
    @Override
    public void onResume() {
    	OCR.Initialize();
    	OCR.get().SetLanguage(OCR.mConfig.GetLanguage());
    	super.onResume();
    }
    
    /*
	 * ----------------------------------------------------------------------------------------
	 * The preferences menu
	 * ----------------------------------------------------------------------------------------
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);

		m_sPreferencesMenu = menu;

		menu.add(PREFERENCES_BCC_ID, HELP_ID, 0, R.string.menu_help)
		.setIcon(android.R.drawable.ic_menu_help);
		menu.add(PREFERENCES_BCC_ID, FEEDBACK_ID, 0, R.string.menu_feedback)
		.setIcon(android.R.drawable.ic_menu_send);
		menu.add(PREFERENCES_BCC_ID, ABOUT_ID, 0, R.string.menu_about)
		.setIcon(android.R.drawable.ic_menu_info_details);

		menu.setGroupVisible(PREFERENCES_BCC_ID, false);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case HELP_ID: 
			AlertDialog.Builder builderH = new AlertDialog.Builder(this);
			builderH.setIcon(R.drawable.wizard_48);
			builderH.setTitle(getString(R.string.preferences_helpTitle));
			builderH.setMessage(getString(R.string.preferences_msg_help_step1) + "\n" + getString(R.string.preferences_itwiz_url));
			builderH.setNegativeButton(R.string.preferences_button_cancel, null);
			builderH.show();
			break;

		case FEEDBACK_ID:
			Intent intent2 = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:patelzarana@gmail.com"));
			intent2.putExtra("subject", "[BusinessCardCapture]");
			intent2.putExtra("body", "");
			startActivity(intent2);
			break;

		case ABOUT_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.wizard_48);
			builder.setTitle(getString(R.string.preferences_aboutTitle));
			builder.setMessage(getString(R.string.preferences_msg_about) + "\n" + getString(R.string.preferences_company_url));
			builder.setPositiveButton(getString(R.string.preferences_button_open_browser), mAboutListener);
			builder.setNegativeButton(getString(R.string.preferences_button_cancel), null);
			builder.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * User requests to connect to company website.
	 */
	private final DialogInterface.OnClickListener mAboutListener = new DialogInterface.OnClickListener() 
	{
		public void onClick(android.content.DialogInterface dialogInterface, int i) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.preferences_company_url)));
			startActivity(intent);
		}
	};

	@Override
	public void onTabChanged(String tabId) {
		System.out.println("Count = " + tabHost.getTabWidget().getChildCount());
		Log.v(TAG,"Count = " + tabHost.getTabWidget().getChildCount());
	    for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	    {
	    	tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FF001E2F")); //unselected
	    }
	    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FF8A8889")); // selected
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
}