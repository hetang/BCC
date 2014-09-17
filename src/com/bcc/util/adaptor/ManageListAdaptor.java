package com.bcc.util.adaptor;


import java.util.List;

import com.bcc.R;
import com.sdsu.bcc.BCCConstants;
import com.sdsu.bcc.database.data.FilesInformation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ManageListAdaptor extends ArrayAdapter<FilesInformation> implements BCCConstants{
	private final Context context;
	private final List<FilesInformation> values;
	private String fileNameColor = "#ffffff";
	private String creationDateColor = "#C4C2C3";
	
	public ManageListAdaptor(Context context, List<FilesInformation> values) {
		super(context, R.layout.import_export_manage, values);
		this.context = context;
		this.values = values;
	}
	
	static class ViewHolder {
		public TextView fileName;
		//public TextView description;
		public TextView creationDate;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View rowView = convertView;
		
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.import_export_manage, null, true);
			holder = new ViewHolder();
			holder.fileName = (TextView) rowView.findViewById(R.id.fileName);
			holder.fileName.setTextColor(Color.parseColor(this.fileNameColor));
			//holder.description = (TextView) rowView.findViewById(R.id.description);
			holder.creationDate = (TextView) rowView.findViewById(R.id.creationDate);
			holder.creationDate.setTextColor(Color.parseColor(this.creationDateColor));
			rowView.setTag(holder);
			holder.fileName.setTag(values.get(position));
		} else {
			holder = (ViewHolder)rowView.getTag();
		}
		
		FilesInformation info = values.get(position);
		holder.fileName.setText(info.getFileName());
		holder.creationDate.setText(creationDateLabel + info.getCreationDate());

		return rowView;
	}
	
	public void setFileNameColor(String color) {
		this.fileNameColor = color;
	}
	
	public void setCreationDateColor(String color) {
		this.creationDateColor = color;
	}
	@Override
	public int getCount() {
		return this.values.size();
	}

	@Override
	public FilesInformation getItem(int position) {
		return this.values.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
}
