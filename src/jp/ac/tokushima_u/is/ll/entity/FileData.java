package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

import jp.ac.tokushima_u.is.ll.util.FilenameUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author houbin
 */
public class FileData implements Serializable {

	private static final long serialVersionUID = -7509406549928484484L;
	private String id;
	private String origName;
	private String fileType;
	private Date createAt;
	private String mailId;
	private String md5;
	private String fileId;

	public String getFileType() {
		if (fileType == null || fileType.length() == 0) {
			if (!StringUtils.isBlank(this.getOrigName())) {
				fileType = FilenameUtil.checkMediaType(this.getOrigName());
			}
		}
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMailId() {
		return mailId;
	}

	public void setMailId(String mailId) {
		this.mailId = mailId;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrigName() {
		return origName;
	}

	public void setOrigName(String origName) {
		this.origName = origName;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
