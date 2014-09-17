package com.sdsu.bcc.file;

import android.app.Activity;

public interface FileDataStoreOperations<T>{
	public boolean saveData(T pObjData) throws Exception;
	public T getData() throws Exception;
	public void setActivity(Activity activity);
}
