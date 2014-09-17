package com.bcc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bcc.util.BCCUtil;
import com.itwizard.mezzofanti.OCR;
import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.CardInformation;
import com.sdsu.bcc.database.data.ContactInformation;
import com.sdsu.bcc.database.data.ImageInformation;
import com.sdsu.bcc.database.impl.BCCDataStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class OCRResult extends Activity implements OnClickListener, BCCConstants, Runnable {
	private static final String TAG = "OCRResult.java: ";
	
	private String m_sOCRResultLineMode = "";
	private boolean m_bLineMode = false;
	private boolean isSaveImage = true;
	private boolean isSaveContactToPhone = true;
	private boolean m_bHorizDispAtPicTaken = true;
	
	private static final int THUMBNAIL_HEIGHT = 62;
	private static final int THUMBNAIL_WIDTH = 86;
	private String[] items = new String[] {"Discard","Name","Company","Email", "Phone","Urls","Address"/*,"City","State","Zip Code"*/};
	
	private List<Spinner> spinnerList = null;
	private List<EditText> editTextList = null;
	
	private Button saveButton = null;
	private Button cancelButton = null;
	private ProgressDialog m_pdOCRInProgress;
	private Bitmap m_bmOCRBitmapIntern = null;
	
	private BCCDataStore objDataStore = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ocrresult);
		
		saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.button_cancel);
		cancelButton.setOnClickListener(this);
		
		objDataStore = new BCCDataStore(this,true);
		
		// set the layout to the xml definition
        GetDataFromMezzofanti();
        
        Bundle bun = getIntent().getExtras();
        
        Log.v(TAG, "Image Path = " + bun.getString("filePath"));
        
        m_bmOCRBitmapIntern = BitmapFactory.decodeFile(bun.getString("filePath"));
        
        Float width  = new Float(m_bmOCRBitmapIntern.getWidth());
        Float height = new Float(m_bmOCRBitmapIntern.getHeight());
        Float ratio = width/height;
        Bitmap tumbNailImage = Bitmap.createScaledBitmap(m_bmOCRBitmapIntern, (int)(THUMBNAIL_HEIGHT*ratio), THUMBNAIL_HEIGHT, false);
        
        ImageView imageView = (ImageView)findViewById(R.id.captureImage);
        
        int padding = (THUMBNAIL_WIDTH - tumbNailImage.getWidth())/2;
        imageView.setPadding(padding, 0, padding, 0);
        imageView.setImageBitmap(tumbNailImage);
        
        if(bun.getBoolean("performOCR")) {
		
			m_pdOCRInProgress = ProgressDialog.show(this, getString(R.string.mezzofanti_ocr_processing_title),
					getString(R.string.mezzofanti_ocr_processing_body_begin) +" "+ OCR.mConfig.GetLanguageMore() + " " + getString(R.string.mezzofanti_ocr_processing_body_end),
					true, true);
			m_pdOCRInProgress.setOnCancelListener( new OnCancelListener() {
				public void onCancel(DialogInterface dialog) 
				{
					android.os.Process.killProcess(android.os.Process.myPid());
				}    		    		
			});
			
			Thread theOCRthread = new Thread(this);
			theOCRthread.start();
			
        } else {
	        
	        EnterResutsMode(m_bmOCRBitmapIntern);
        }
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		isSaveImage = sharedPrefs.getBoolean("save_photo_key", true);
		isSaveContactToPhone = sharedPrefs.getBoolean("create_contact", true);
		
		Log.v(TAG,"isSaveContactToPhone = " + isSaveContactToPhone);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// do nothing when keyboard open
		super.onConfigurationChanged(newConfig);
	}
	
	/**
     * Get params-data from the caller.
     */
    private void GetDataFromMezzofanti()
    {
        Bundle bun = getIntent().getExtras();
        if (null == bun) 
        {
        	Log.v(TAG,"Bundle is null");
        }
        else
        {
        	 m_bLineMode = bun.getBoolean("bLineMode");
             m_sOCRResultLineMode = bun.getString("LineModeText");
        }    	
    }
    
    /**
     * From capture-mode enters results-mode.
     * @param bm the bitmap to be processed by the OCR.
     */
	public void EnterResutsMode(Bitmap bm)
	{
		Log.v(TAG, "enter enterResutsMode");
		
    	// start the new layout
    	DisplayOcrResult(); 
    	if (bm != null)
    	{
	    	// display the captured image
	    	//ImageView ViewResult = (ImageView)findViewById(R.id.resultsactivity_slidingDrawerPicture);
			try {
				//ViewResult.setImageBitmap(bm);
			} catch (Exception e) {
				e.printStackTrace();
				Log.v(TAG, e.toString());
			}		
    	}
    	
    	//m_sdPicture = (SlidingDrawer)findViewById(R.id.resultsactivity_drawer);
    	//m_sdPicture.setOnDrawerOpenListener(drawerOpenCallback);
    	//m_sdPicture.setOnDrawerCloseListener(drawerCloseCallback);
	    
    	Log.v(TAG, "exit enterResutsMode");
	}     
	
	/**
	 * Display the OCR results according to the settings.
	 */
	private void DisplayOcrResult()
	{		
		if (!m_bLineMode && OCR.m_iMeanConfidence < OCR.mConfig.m_iMinOveralConfidence)
			m_LocalMessageHandler.sendEmptyMessageDelayed(R.id.resultsactivity_displayWarning, 1000);
		
		//m_etNonEditableText.requestFocus();
		try 
		{
			if (!m_bLineMode) {
				//m_etNonEditableText.setText(OCR.m_ssOCRResult);
				Log.v(TAG, "OCR Result = " + OCR.m_sOCRResult);
				displayResult(OCR.m_sOCRResult);
			} else {
				//m_etNonEditableText.setText(m_sOCRResultLineMode);
				Log.v(TAG, "Line Mode Reusult = " + m_sOCRResultLineMode);
				displayResult(m_sOCRResultLineMode);
			}
				
		} catch (Exception e) {
			Log.v(TAG, e.toString());
			e.printStackTrace();
		}		

	}
	
	/*
	 * ----------------------------------------------------------------------------------------------------
	 * Events Handler 
	 * ---------------------------------------------------------------------------------------------------- 
	 */
		
	private void displayResult(String p_sOCRResult) {
		// TODO Auto-generated method stub
		//String[] lines = p_sOCRResult.split(System.getProperty("line.separator"));
		if(p_sOCRResult != null){
			String[] lines = p_sOCRResult.split("\n");
			System.out.println("lines = " + lines.length);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		            (LayoutParams.FILL_PARENT), (LayoutParams.WRAP_CONTENT));
			LinearLayout ll = (LinearLayout) findViewById(R.id.ocrResultLayout);
			
			int count = 1001;
			
			spinnerList = new ArrayList<Spinner>();
			editTextList = new ArrayList<EditText>();
			
			for (int i=0;i<lines.length;i++) {
				String value = lines[i].trim();
				if(value.length() > 0) {
					RelativeLayout relative = new RelativeLayout(getApplicationContext());
					relative.setLayoutParams(lp);
				
					//TextView tv = new TextView(getApplicationContext());
					
					RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(
				            (LayoutParams.WRAP_CONTENT), (LayoutParams.WRAP_CONTENT));
					
					Spinner spinner = new Spinner(this);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					            R.layout.spinnerlayout, items);
					spinner.setAdapter(adapter);
					spinner.setLayoutParams(slp);
					spinner.setId(count++);
					spinnerList.add(spinner);
					
					RelativeLayout.LayoutParams elp = new RelativeLayout.LayoutParams(
				            (LayoutParams.FILL_PARENT), (LayoutParams.WRAP_CONTENT));
					elp.addRule(RelativeLayout.RIGHT_OF, spinner.getId());
					
					EditText edittv = new EditText(this);
					edittv.setLayoutParams(elp);
					edittv.setText(lines[i]);
					edittv.setId(count++);
					editTextList.add(edittv);
					
					relative.addView(spinner);
					relative.addView(edittv);
					ll.addView(relative);
				}
			}
			
			Log.v(TAG,"spinnerList = " + spinnerList.size());
		}
	}

	private Handler m_LocalMessageHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{					
				// ---------------------------------------------------------------	
				// on m_etNonEditableText click
				case R.id.resultsactivity_displayWarning:
					AlertDialog.Builder builder = new AlertDialog.Builder(OCRResult.this);
				    builder.setIcon(R.drawable.alert32);
				    builder.setTitle(R.string.resultsactivity_ocrbadresults_title);
				    builder.setMessage(getString(R.string.resultsactivity_ocrbadresults_msg1) + " " + OCR.m_iMeanConfidence +" " + getString(R.string.resultsactivity_ocrbadresults_msg2));
				    builder.setPositiveButton(R.string.resultsactivity_ocrbadresults_ok, null);
				    builder.show();	
				    break;
				case R.id.mezzofanti_ocrFinished:
					EnterResutsMode(m_bmOCRBitmapIntern);
					break;
			}
			
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onClick(View v) {
		if(v == saveButton) {
			Log.v(TAG,"Inside Save: spinnerList = " + spinnerList.size());
			
			if(spinnerList.size() > 0) {
				CardInformation objInfo = new CardInformation();
				ContactInformation objContact = new ContactInformation();
				for (int i=0;i<spinnerList.size();i++) {
					Spinner obj = spinnerList.get(i);
					EditText objText = editTextList.get(i);
					
					switch(obj.getSelectedItemPosition()){
						case 0: continue;
						case 1: 
							objContact.setName(objText.getText().toString());
							break;
						case 2:
							objContact.setCompany(objText.getText().toString());
							break;
						case 3:
							Map<String,String> emails = new HashMap<String,String>();
							emails.put("emailType", "Home");
							emails.put("emailId", objText.getText().toString());
							objContact.addEmail(emails);
							break;
						case 4:
							Map<String,String> phone = new HashMap<String,String>();
							phone.put("phoneNumber", objText.getText().toString());
							phone.put("phoneType",String.valueOf(PhoneType.HOME));
							objContact.addPhone(phone);
							break;
						case 5:
							objContact.addUrl(objText.getText().toString());
							break;
					}
				}
				Log.v(TAG,"objContact = " + objContact.toString());
				
				if(isSaveContactToPhone) {
					//Log.v(TAG,"Inside saving contact to phone");
					try {
						BCCUtil.addUpdateContactToPhone(this, objContact);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (OperationApplicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Log.v(TAG,"isSaveImage = " + isSaveImage);
				if(isSaveImage) {
					OutputStream outStream = null;
					try {
						ImageInformation objImage = new ImageInformation();
						
						DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
						String imageFileName = formatter.format(new Date()) + ".png";
						
						Bundle bun = getIntent().getExtras();
				        Bitmap m_bmOCRBitmapIntern = BitmapFactory.decodeFile(bun.getString("filePath"));
				        
				        Log.v(TAG,"Inside Save Image : m_bmOCRBitmapIntern = " + m_bmOCRBitmapIntern);
				        
				        File fileOutputDir = new File(Environment.getExternalStorageDirectory()+IMAGE_PATH);
				        fileOutputDir.mkdirs();
				        
				        File f = new File(fileOutputDir, imageFileName);
				        outStream = new FileOutputStream(f);
					    m_bmOCRBitmapIntern.compress(Bitmap.CompressFormat.PNG, 90, outStream);
					    
					    outStream.flush();
					    outStream.close();
					    
					    Log.v(TAG,"Saved Image Path = " + Environment.getExternalStorageDirectory() + IMAGE_PATH + imageFileName);
					    objImage.setImagePath(Environment.getExternalStorageDirectory() + IMAGE_PATH + imageFileName);
					    objInfo.setImage(objImage);
					    
					} catch (Exception e) {
					       e.printStackTrace();
					}
				}
				
				objInfo.setContact(objContact);
				objInfo.setCategory(null);
				
				try {
					objDataStore.insertRecords(objInfo);
					
					AlertDialog alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("Saving Contact");
					alertDialog.setMessage("Record Saved Successfully");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					   public void onClick(DialogInterface dialog, int which) {
					      Intent mainPage = new Intent(getApplicationContext(),BCCActivity.class);
					      startActivity(mainPage);
					   }
					});
					alertDialog.setIcon(R.drawable.icon);
					alertDialog.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle("Saving Contact");
				alertDialog.setMessage("There are no selection of the information. Please click cancel to go to previous menu.");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog, int which) {
				      // here you can add functions
				   }
				});
				alertDialog.setIcon(R.drawable.icon);
				alertDialog.show();
			}
		} else if(v == cancelButton) {
			finish();
		}
	}

	@Override
	public void run() {
		if(null != m_bmOCRBitmapIntern) {
			// called by the OCR thread
			int iPicWidth  = m_bmOCRBitmapIntern.getWidth();
			int iPicHeight = m_bmOCRBitmapIntern.getHeight();
			int[] iImage = null;
			try
			{
				iImage = new int[iPicWidth * iPicHeight];
				Log.v(TAG, "allocated img buffer: " +iPicWidth + ", "+iPicHeight);
				m_bmOCRBitmapIntern.getPixels(iImage, 0, iPicWidth, 0, 0, iPicWidth, iPicHeight);
				Log.v(TAG, "pix1="+Integer.toHexString(iImage[0]));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				//Log.v(TAG, "exception: run():" + ex.toString());
				m_bmOCRBitmapIntern = null;
				System.gc();			
			}


			if (iImage != null)
			{
				String m_sOCRResult = OCR.get().ImgOCRAndFilter(iImage, iPicWidth, iPicHeight, m_bHorizDispAtPicTaken, m_bLineMode);			
				Log.v(TAG, "ocr done text= [" + m_sOCRResult +"]");
				// force free the mem
				iImage = null;
				m_bmOCRBitmapIntern = null;
				System.gc();			

				// bad results, get the (internal) image
				OCR.get().SaveMeanConfidence();

				Log.v(TAG, "starting results handler");
			}
			
			m_pdOCRInProgress.dismiss();
			System.gc();
			m_LocalMessageHandler.sendEmptyMessage(R.id.mezzofanti_ocrFinished);
			Log.i(TAG, "pcjpg - finish startPreview()");
		}
	}
}
