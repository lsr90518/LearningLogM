package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author lemonrain
 */
public class Mail implements Serializable {

	private static final long serialVersionUID = 3860165061809007813L;
	private Long mailid;
	private String messageid;
	private String sender;
	private String receiver;
	private String subject;
	private String content;
	private Integer testtypeid;
	private Date sendDate;
	private Users userid;

	public Users getUserid() {
		return userid;
	}

	public void setUserid(Users userid) {
		this.userid = userid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getMailid() {
		return mailid;
	}

	public void setMailid(Long mailid) {
		this.mailid = mailid;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Integer getTesttypeid() {
		return testtypeid;
	}

	public void setTesttypeid(Integer testtypeid) {
		this.testtypeid = testtypeid;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (mailid != null ? mailid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Mail)) {
			return false;
		}
		Mail other = (Mail) object;
		if ((this.mailid == null && other.mailid != null)
				|| (this.mailid != null && !this.mailid.equals(other.mailid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.Mail[id=" + mailid + "]";
	}
}
