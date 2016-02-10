package com.micen.suppliers.module.message;

import java.io.Serializable;

public class MessageContentFiles implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4070915942700962128L;
	public String fileName;
	public String fileId;
	public String fileType;
	public String fileUrl;
	public String fileLocalPath;
	public Long fileSize;
}
