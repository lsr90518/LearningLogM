/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 * 
 * @author lemonrain
 */
public class MailSubject implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long mailsubjectid;
	private String subjectpattern;
	private Integer testtypeid;
	private Integer typ;
	private Integer state;

	public Long getMailsubjectid() {
		return mailsubjectid;
	}

	public void setMailsubjectid(Long mailsubjectid) {
		this.mailsubjectid = mailsubjectid;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getSubjectpattern() {
		return subjectpattern;
	}

	public void setSubjectpattern(String subjectpattern) {
		this.subjectpattern = subjectpattern;
	}

	public Integer getTesttypeid() {
		return testtypeid;
	}

	public void setTesttypeid(Integer testtypeid) {
		this.testtypeid = testtypeid;
	}

	public Integer getTyp() {
		return typ;
	}

	public void setTyp(Integer typ) {
		this.typ = typ;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (mailsubjectid != null ? mailsubjectid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof MailSubject)) {
			return false;
		}
		MailSubject other = (MailSubject) object;
		if ((this.mailsubjectid == null && other.mailsubjectid != null)
				|| (this.mailsubjectid != null && !this.mailsubjectid
						.equals(other.mailsubjectid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.MailSubject[id=" + mailsubjectid
				+ "]";
	}

}
