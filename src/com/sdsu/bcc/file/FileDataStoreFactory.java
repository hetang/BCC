package com.sdsu.bcc.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.bcc.R;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class FileDataStoreFactory {
	private static FileDataStoreFactory m_store = null;
	private Map<String,ParserInfo> parserInfos = new HashMap<String,ParserInfo>();
	
	private FileDataStoreFactory(Activity activity) throws XmlPullParserException, IOException {
		parseFileMetaData(activity);
	}
	
	public static FileDataStoreFactory getFileStore(Activity activity) throws XmlPullParserException, IOException {
		if(m_store == null) {
			m_store = new FileDataStoreFactory(activity);
		}
		
		return m_store;
	}
	
	private void parseFileMetaData(Activity activity) throws XmlPullParserException, IOException {
		ParserInfo parserInfo = null;
		boolean isName = false;
		boolean isType = false;
		boolean isClassName = false;
		
		Resources res = activity.getResources();
		XmlResourceParser xpp = res.getXml(R.xml.filestoremetadata);
		xpp.next();
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("FileStore".equals(xpp.getName())) {
						parserInfo = new ParserInfo();
					} else if ("Name".equals(xpp.getName())) {
						isName = true;
					}else if ("Type".equals(xpp.getName())) {
						isType = true;
					}else if ("ClassName".equals(xpp.getName())) {
						isClassName = true;
					}
					break;
				case XmlPullParser.END_TAG:
					if ("FileStore".equals(xpp.getName())) {
						this.parserInfos.put(parserInfo.getType(), parserInfo);
					} else if ("Name".equals(xpp.getName())) {
						isName = false;
					}else if ("Type".equals(xpp.getName())) {
						isType = false;
					}else if ("ClassName".equals(xpp.getName())) {
						isClassName = false;
					}
					break;
				case XmlPullParser.TEXT:
					if (isName) {
						parserInfo.setName(xpp.getText());
					} else if (isType) {
						parserInfo.setType(xpp.getText());
					} else if (isClassName) {
						parserInfo.setClassName(xpp.getText());
					}
					break;
			}
			eventType = xpp.next();
		}
	}
	
	public FileDataStoreOperations<?> getFileStore(String type, Activity activity) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		ParserInfo parserInfo = this.parserInfos.get(type);
		if(parserInfo == null) {
			throw new ClassNotFoundException("File Store of Type " + type + " is not available. Please check File Store Meta Data File.");
		}
		
		FileDataStoreOperations<?> fileStore = (FileDataStoreOperations<?>) Class.forName(parserInfo.getClassName()).newInstance();
		fileStore.setActivity(activity);
		
		return fileStore;
	}
	
	private static class ParserInfo {
		private String name = null;
		private String type = null;
		private String className = null;
		
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
			
		}
	}
}
