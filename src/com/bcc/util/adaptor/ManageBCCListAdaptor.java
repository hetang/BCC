package com.bcc.util.adaptor;

import java.util.List;

import com.bcc.R;
import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.CardInformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageBCCListAdaptor extends ArrayAdapter<CardInformation> implements BCCConstants {
	
	private final Context context;
	private final List<CardInformation> values;
	private static final int THUMBNAIL_HEIGHT = 62;
	private static final int THUMBNAIL_WIDTH = 86;
	private static final String TAG = "ManageBCCListAdaptor";
	
	public ManageBCCListAdaptor(Context context, List<CardInformation> values) {
		super(context, R.layout.addbcc_manage, values);
		this.context = context;
		this.values = values;
	}
	
	static class ViewHolder {
		public ImageView imagePath;
		public TextView Name;
		public TextView companyName;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View rowView = convertView;
		
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.addbcc_manage, null, true);
			holder = new ViewHolder();
			holder.imagePath = (ImageView) rowView.findViewById(R.id.thumImageView);
			//holder.fileName.setTextColor(Color.parseColor(this.fileNameColor));
			holder.Name = (TextView) rowView.findViewById(R.id.personNameText);
			holder.companyName = (TextView) rowView.findViewById(R.id.companyText);
			holder.companyName.setTextColor(Color.GRAY);
			rowView.setTag(holder);
			holder.imagePath.setTag(values.get(position));
		} else {
			holder = (ViewHolder)rowView.getTag();
		}
		
		CardInformation info = values.get(position);
		if(info.getImage() != null && info.getImage().getImagePath() != null) {
			Log.v(TAG,"Image Path = " + info.getImage().getImagePath());
			Bitmap m_bmOCRBitmapIntern = BitmapFactory.decodeFile(info.getImage().getImagePath());
			Log.v(TAG,"m_bmOCRBitmapIntern = " + m_bmOCRBitmapIntern);
			if(null != m_bmOCRBitmapIntern){
				Float width  = new Float(m_bmOCRBitmapIntern.getWidth());
				Float height = new Float(m_bmOCRBitmapIntern.getHeight());
				Float ratio = width/height;
	        	Bitmap tumbNailImage = Bitmap.createScaledBitmap(m_bmOCRBitmapIntern, (int)(THUMBNAIL_HEIGHT*ratio), THUMBNAIL_HEIGHT, false);
			
	        	int padding = (THUMBNAIL_WIDTH - tumbNailImage.getWidth())/2;
	        	holder.imagePath.setPadding(padding, 0, padding, 0);
	        	holder.imagePath.setImageBitmap(tumbNailImage);
			} else {
				holder.imagePath.setImageResource(R.drawable.icon);
			}
		} else {
			holder.imagePath.setImageResource(R.drawable.icon);
		}
		
		if(info.getContact() != null) {
			holder.Name.setText(info.getContact().getName());
			holder.companyName.setText(info.getContact().getCompany());
		} else {
			holder.Name.setText("No Name");
			holder.companyName.setText("");
		}
		

		return rowView;
	}
}
