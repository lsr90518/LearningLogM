package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author li
 */
public class StudySpeed implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private Float maxspeed;
	private Float mixspeed;
	private Integer disabled;
	private String authorId;
	private Date createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Float getMaxspeed() {
		return maxspeed;
	}

	public void setMaxspeed(Float maxspeed) {
		this.maxspeed = maxspeed;
	}

	public Float getMixspeed() {
		return mixspeed;
	}

	public void setMixspeed(Float mixspeed) {
		this.mixspeed = mixspeed;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
		if (!(object instanceof StudySpeed)) {
			return false;
		}
		StudySpeed other = (StudySpeed) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.StudyTime[id=" + id + "]";
	}

}
