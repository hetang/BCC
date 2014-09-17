package com.bcc.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.CardInformation;
import com.sdsu.bcc.database.data.FilesInformation;
import com.sdsu.bcc.database.impl.BCCDataStore;
import com.sdsu.bcc.database.impl.FilesDataStore;

public class Facade implements BCCConstants {

	public List<FilesInformation> getFilesList(Context context) {
		
		try {
			FilesDataStore store = new FilesDataStore(context,true);
			return store.getRecords(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<FilesInformation>();
		}
	}
	
	public List<CardInformation> getCardList(Context context) {
		try {
			BCCDataStore store = new BCCDataStore(context,true);
			return store.getRecords(null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<CardInformation>();
		}
	}
}
