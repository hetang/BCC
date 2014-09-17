package com.bcc;

import java.util.ArrayList;
import java.util.List;

import com.bcc.util.Facade;
import com.bcc.util.adaptor.ManageListAdaptor;
import com.sdsu.bcc.database.data.FilesInformation;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImportExportManage extends ListActivity {
	private static Facade m_store = new Facade();
	private ProgressDialog m_ProgressDialog = null;
	private List<FilesInformation> m_values = new ArrayList<FilesInformation>();
	private ManageListAdaptor m_adapter = null;
	private Runnable fetchList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_export_manage_list_view);
		
		RelativeLayout layout = (RelativeLayout)findViewById(android.R.id.empty);
		layout.setBackgroundColor(Color.parseColor("#FF001E2F"));
		
		ListView list = (ListView)findViewById(android.R.id.list);
		list.setBackgroundColor(Color.parseColor("#FF001E2F"));
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, list, false);
		
		list.addHeaderView(header, null, false);
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(ImportExportManage.this,
						"Item in position " + position + " clicked",
						Toast.LENGTH_LONG).show();
				// Return true to consume the click event. In this case the
				// onListItemClick listener is not called anymore.
				return true;
			}
		});
		
		m_adapter = new ManageListAdaptor(this, m_values);
		setListAdapter(m_adapter);
		
		fetchList = new Runnable(){
            @Override
            public void run() {
                getList();
            }
        };
        Thread thread =  new Thread(null, fetchList, "ImportExportListFetchBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(ImportExportManage.this,    
              "Please wait...", "Retrieving data ...", true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
	
	 private Runnable returnResponse = new Runnable() {

        @Override
        public void run() {
            if(m_values != null && m_values.size() > 0){
                m_adapter.notifyDataSetChanged();
                for(int i=0;i<m_values.size();i++)
                m_adapter.add(m_values.get(i));
            }
            m_ProgressDialog.dismiss();
            m_adapter.notifyDataSetChanged();
        }
    };
	    
    private void getList(){
		try{
			m_values = m_store.getFilesList(this);
			Thread.sleep(500);
			Log.i("ARRAY", ""+ m_values.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnResponse);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FilesInformation item = (FilesInformation) getListAdapter().getItem(position-1);
		Toast.makeText(this, item.getFileName() + " selected", Toast.LENGTH_LONG).show();
	}
}
