package com.sdsu.bcc.database;

import java.util.List;

public interface DatabaseOperation<E> {
	public boolean insertRecords(List<E> newRecords) throws Exception;
	public boolean insertRecords(E newRecords) throws Exception;
	public boolean updateRecords(List<E> existingRecords) throws Exception;
	public boolean updateRecords(E existingRecords) throws Exception;
	public List<E> getRecords(String[] queryArgs) throws Exception;
	public E viewRecord(String[] queryArgs) throws Exception;
	public boolean deleteRecords(List<E> removeRecords) throws Exception;
	public boolean deleteRecords(E removeRecords) throws Exception;
	public int getNextId() throws Exception;
}
