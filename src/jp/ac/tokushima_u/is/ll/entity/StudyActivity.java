package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author li
 */
public class StudyActivity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;

	private Double latitude;
	private Double longitude;
	private Float speed;
	private Date createtime;
	private Integer activity_type;
	private String authorId;

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public Integer getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(Integer activity_type) {
		this.activity_type = activity_type;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

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

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof StudyActivity)) {
			return false;
		}
		StudyActivity other = (StudyActivity) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.StudyActivity[id=" + id + "]";
	}

}
