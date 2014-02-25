/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * 
 * @author li
 */
public class AutoSender implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;

	private Time sendtime;
	private Date sendday;
	private String authorId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Date getSendday() {
		return sendday;
	}

	public void setSendday(Date sendday) {
		this.sendday = sendday;
	}

	public Time getSendtime() {
		return sendtime;
	}

	public void setSendtime(Time sendtime) {
		this.sendtime = sendtime;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof AutoSender)) {
			return false;
		}
		AutoSender other = (AutoSender) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.AutoSender[id=" + id + "]";
	}

}
