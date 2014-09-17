package com.sdsu.bcc;

public interface BCCConstants {
	int DATABASE_VERSION = 1;
	
	String DATABASE_NAME = "businessCards.db";
	
	/* Max ID Queries */
	String sqlMaxTransactionId = "SELECT MAX(ID) FROM BCC_TB_TRANSACTION";
	String sqlMaxContactsId = "SELECT MAX(ID) FROM BCC_TB_CONTACTS";
	String sqlMaxImageId = "SELECT MAX(ID) FROM BCC_TB_IMAGE_INFO";
	String sqlMaxCategoryId = "SELECT MAX(ID) FROM BCC_TB_CATEGORY";
	String sqlMaxFilesId = "SELECT MAX(ID) FROM BCC_TB_FILES";
	
	String sqlGetAllContactsTransaction = "select CTI.ID, CTI.CONTACT_ID, CTI.IMAGE_ID, CTI.CATEGORY_ID, CTI.NAME, CTI.COMPANY, CTI.PATH, CA.TYPE from ((BCC_TB_CONTACTS C JOIN BCC_TB_TRANSACTION T ON T.CONTACT_ID = C.ID) CT  LEFT JOIN BCC_TB_IMAGE_INFO I ON CT.ID = I.ID) CTI LEFT JOIN BCC_TB_CATEGORY CA ON CTI.ID = CA.ID ORDER BY CTI.NAME";
	String sqlGetAllContacts = "SELECT ID,NAME,COMPANY FROM BCC_TB_CONTACTS ORDER BY NAME";
	String sqlGetEmailForContact = "SELECT E.Email, E.TYPE FROM BCC_TB_CONTACTS_EMAIL E WHERE E.CONTACT_ID = ? ORDER BY RANK";
	String sqlGetPhoneForContact = "SELECT P.PHONE, P.TYPE FROM BCC_TB_CONTACTS_PHONE P WHERE P.CONTACT_ID = ? ORDER BY RANK";
	String sqlGetUrlForContact = "SELECT U.URL FROM BCC_TB_CONTACTS_URL U WHERE U.CONTACT_ID = ? ORDER BY RANK";
	String sqlGetAllCategory = "SELECT ID,TYPE FROM BCC_TB_CATEGORY ORDER BY TYPE";
	String sqlGetAllFiles = "SELECT ID,NAME,DESCRIPTION,CREATION_DATE FROM BCC_TB_FILES ORDER BY CREATION_DATE DESC";
	
	/* Insert Quries */
	String sqlInsertImageData = "INSERT INTO BCC_TB_IMAGE_INFO (ID,PATH,DATA) VALUES(?,?,null)";
	String sqlInsertCategoryData = "INSERT INTO BCC_TB_CATEGORY (ID,TYPE) VALUES(?,?)";
	String sqlInsertContactData = "INSERT INTO BCC_TB_CONTACTS (ID,NAME,COMPANY) VALUES (?,?,?)";
	String sqlInsertContactEmailData = "INSERT INTO BCC_TB_CONTACTS_EMAIL (CONTACT_ID,EMAIL,TYPE,RANK) VALUES (?,?,?,?)";
	String sqlInsertContactPhoneData = "INSERT INTO BCC_TB_CONTACTS_PHONE (CONTACT_ID,PHONE,TYPE,RANK) VALUES (?,?,?,?)";
	String sqlInsertContactUrlData = "INSERT INTO BCC_TB_CONTACTS_URL (CONTACT_ID,URL,RANK) VALUES (?,?,?)";
	String sqlInsertContactTransaction = "INSERT INTO BCC_TB_TRANSACTION (ID,CONTACT_ID,IMAGE_ID,CATEGORY_ID,TRASACTION_DATE) VALUES (?,?,?,?,datetime())";
	String sqlInsertFileInfo = "INSERT INTO BCC_TB_FILES (ID,NAME,DESCRIPTION,CREATION_DATE,PATH) VALUES(?,?,?,?,?)";
	
	/* String Constants */
	String creationDateLabel = "File Created on ";
	
	enum CSVFileType {
		Default,
		Google,
		Outlook,
		Apple
	};
	
	enum PhoneType {
		HOME(1), 
		MOBILE(2), 
		WORK(3), 
		FAX_WORK(4), 
		FAX_HOME(5), 
		PAGER(6), 
		OTHER(7), 
		CALLBACK(8), 
		CAR(9), 
		COMPANY_MAIN(10), 
		ISDN(11), 
		MAIN(12), 
		OTHER_FAX(13), 
		RADIO(14), 
		TELEX(15), 
		TTY_TDD(16),
		WORK_MOBILE(17),
		WORK_PAGER(18),
		ASSISTANT(19),
		MMS(20);
		
		private int phoneCode;
		
		PhoneType(int phoneCode) {
			this.phoneCode = phoneCode;
		}
		
		public int getPhoneTypeCode() {
			return phoneCode;
		}
	}
	
	enum EmailType{
		HOME(1),
		MOBILE(4),
		OTHER(3),
		WORK(2);
		
		private int emailCode;
		
		EmailType(int emailCode) {
			this.emailCode = emailCode;
		}
		
		public int getEmailTypeCode() {
			return emailCode;
		}
	}
	
	String defaultCSVHeader = "Name,Company,Email1-Type,Email1-Value,Email2-Type,Email2-Value,Email3-Type,Email3-Value,Phone1-Type,Phone1-Value,Phone2-Type,Phone2-Value,Phone3-Type,Phone3-Value,Phone4-Type,Phone4-Value,Phone5-Type,Phone5-Value,Phone6-Type,Phone6-Value";
	String googleCSVHeader = "Name,Given Name,Additional Name,Family Name,Yomi Name,Given Name Yomi,Additional Name Yomi,Family Name Yomi,Name Prefix,Name Suffix,Initials,Nickname,Short Name,Maiden Name,Birthday,Gender,Location,Billing Information,Directory Server,Mileage,Occupation,Hobby,Sensitivity,Priority,Subject,Notes,Group Membership";
	String outlookCSVHeader = "First Name,Middle Name,Last Name,Title,Suffix,Initials,Web Page,Gender,Birthday,Anniversary,Location,Language,Internet Free Busy,Notes,E-mail Address,E-mail 2 Address,E-mail 3 Address,Primary Phone,Home Phone,Home Phone 2,Mobile Phone,Pager,Home Fax,Home Address,Home Street,Home Street 2,Home Street 3,Home Address PO Box,Home City,Home State,Home Postal Code,Home Country,Spouse,Children,Manager's Name,Assistant's Name,Referred By,Company Main Phone,Business Phone,Business Phone 2,Business Fax,Assistant's Phone,Company,Job Title,Department,Office Location,Organizational ID Number,Profession,Account,Business Address,Business Street,Business Street 2,Business Street 3,Business Address PO Box,Business City,Business State,Business Postal Code,Business Country,Other Phone,Other Fax,Other Address,Other Street,Other Street 2,Other Street 3,Other Address PO Box,Other City,Other State,Other Postal Code,Other Country,Callback,Car Phone,ISDN,Radio Phone,TTY/TDD Phone,Telex,User 1,User 2,User 3,User 4,Keywords,Mileage,Hobby,Billing Information,Directory Server,Sensitivity,Priority,Private,Categories";
	
	public String IMAGE_PATH = "/bccdata/images/";
	public String EXPORT_PATH = "/bccdata/exports/";
}
