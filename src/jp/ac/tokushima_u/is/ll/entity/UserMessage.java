package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Message from one user to another user
 * 
 * @author HOUBin
 * 
 */
public class UserMessage implements Serializable {

	private static final long serialVersionUID = 8292485003439780560L;

	private String id;
	private String content;
	private String sendFrom;
	private String sendTo;
	private Date createTime;
	private boolean readFlag = false;
	private Date readTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSendFrom() {
		return sendFrom;
	}

	public void setSendFrom(String sendFrom) {
		this.sendFrom = sendFrom;
	}

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}

	public Date getReadTime() {
		return readTime;
	}

	public void setReadFlag(boolean readFlag) {
		this.readFlag = readFlag;
	}

	public boolean isReadFlag() {
		return readFlag;
	}
}
