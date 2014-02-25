package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 * 
 * @author li
 */
public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private Integer handsetcd;
	private Integer wrongdays;
	private Integer rightdays;
	private Integer adddays;
	private Boolean mylog;
	private String authorId;

	public Boolean getMylog() {
		return mylog;
	}

	public void setMylog(Boolean mylog) {
		this.mylog = mylog;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Integer getHandsetcd() {
		return handsetcd;
	}

	public void setHandsetcd(Integer handsetcd) {
		this.handsetcd = handsetcd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getWrongdays() {
		return wrongdays;
	}

	public void setWrongdays(Integer wrongdays) {
		this.wrongdays = wrongdays;
	}

	public Integer getRightdays() {
		return rightdays;
	}

	public void setRightdays(Integer rightdays) {
		this.rightdays = rightdays;
	}

	public Integer getAdddays() {
		return adddays;
	}

	public void setAdddays(Integer adddays) {
		this.adddays = adddays;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Setting)) {
			return false;
		}
		Setting other = (Setting) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.Setting[id=" + id + "]";
	}

}
