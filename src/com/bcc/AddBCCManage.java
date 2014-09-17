package com.bcc;

import java.util.ArrayList;
import java.util.List;

import com.bcc.util.Facade;
import com.bcc.util.adaptor.ManageBCCListAdaptor;
import com.sdsu.bcc.database.data.CardInformation;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AddBCCManage extends ListActivity {
	
	private static Facade m_store = new Facade();
	private ProgressDialog m_ProgressDialog = null;
	private ManageBCCListAdaptor m_adapter = null;
	private List<CardInformation> m_values = new ArrayList<CardInformation>();
	private Runnable fetchList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbcc_manage_list_view);
		
		RelativeLayout layout = (RelativeLayout)findViewById(android.R.id.empty);
		layout.setBackgroundColor(Color.parseColor("#FF001E2F"));
		
		ListView list = (ListView)findViewById(android.R.id.list);
		list.setBackgroundColor(Color.parseColor("#FF001E2F"));
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header, list, false);
		
		list.addHeaderView(header, null, false);
		
		m_adapter = new ManageBCCListAdaptor(this,m_values);
		setListAdapter(m_adapter);
		
		fetchList = new Runnable(){
            @Override
            public void run() {
                getList();
            }
        };
        Thread thread =  new Thread(null, fetchList, "ImportExportListFetchBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(AddBCCManage.this,    
              "Please wait...", "Retrieving data ...", true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
	
	private void getList(){
		try{
			m_values = m_store.getCardList(this);
			Thread.sleep(500);
			Log.i("ARRAY", ""+ m_values.size());
		} catch (Exception e) { 
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnResponse);
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
}
