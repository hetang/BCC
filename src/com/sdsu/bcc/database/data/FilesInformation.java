package com.sdsu.bcc.database.data;

import java.util.Date;

public class FilesInformation {
	private int id;
	private String fileName = null;
	private String description = null;
	private String creationDate = null;
	private String filePath = null;
	
	public FilesInformation() {
		this.fileName = "default";
		this.description = "default";
		this.creationDate = String.valueOf(new Date());
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
