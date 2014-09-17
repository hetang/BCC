package com.bcc;

import java.util.ArrayList;
import java.util.List;

import com.bcc.util.BCCUtil;
import com.bcc.util.adaptor.ManageListAdaptor;
import com.itwizard.mezzofanti.Mezzofanti;
import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.FilesInformation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AddBCC extends Activity implements Button.OnClickListener, BCCConstants {
	private static String TAG = "AddBCC";
	
	private Button importFromCamera;
	private Button importFromImage;
	private Button manageContacts;
	private List<String> fileType = new ArrayList<String> ();
	private List<FilesInformation> fileInfos = new ArrayList<FilesInformation>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbcc);
		
		fileType.add("bmp");
		fileType.add("png");
		fileType.add("jpg");
		fileType.add("jpeg");
		
		importFromCamera = (Button)findViewById(R.id.captureImage);
		importFromCamera.setOnClickListener(this);
		
		importFromImage = (Button)findViewById(R.id.importImage);
		importFromImage.setOnClickListener(this);
		
		manageContacts = (Button)findViewById(R.id.manageContacts);
		manageContacts.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		try {
			fileInfos = BCCUtil.buildFileList(TAG,fileType,IMAGE_PATH);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onClick(View v) {
		if(v == importFromCamera) {
			Intent myIntent = new Intent(v.getContext(), Mezzofanti.class);
			startActivityForResult(myIntent, 0);
		} else if(v == importFromImage)  {
			populateImportImage();
		} else if(v == manageContacts) {
			Intent myIntent = new Intent(v.getContext(), AddBCCManage.class);
			startActivityForResult(myIntent, 0);
		}
	}
	
	private void populateImportImage() {
		ManageListAdaptor arrayAdapter = new ManageListAdaptor(this,fileInfos);
		arrayAdapter.setFileNameColor("#000000");
		arrayAdapter.setCreationDateColor("#0001C1");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.import_contacts);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(), fileInfos.get(which).getFileName(), Toast.LENGTH_SHORT).show();
				Intent ocrResult = new Intent(getApplicationContext(), OCRResult.class);
				Bundle bun = new Bundle();
				bun.putBoolean("performOCR", true);
				bun.putString("filePath", fileInfos.get(which).getFilePath());
				ocrResult.putExtras(bun);
				startActivityForResult(ocrResult,0);
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
}
