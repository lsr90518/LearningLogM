package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 * 
 * @author lemonrain
 */
public class Subscribe implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long subscribeid;
	private String userid;
	private Integer testtypeid;
	private Integer level;
	private Integer dynamiclevel;
	private Integer questionnumber;
	private Integer state;

	public Integer getDynamiclevel() {
		return dynamiclevel;
	}

	public void setDynamiclevel(Integer dynamiclevel) {
		this.dynamiclevel = dynamiclevel;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getQuestionnumber() {
		return questionnumber;
	}

	public void setQuestionnumber(Integer questionnumber) {
		this.questionnumber = questionnumber;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getSubscribeid() {
		return subscribeid;
	}

	public void setSubscribeid(Long subscribeid) {
		this.subscribeid = subscribeid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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
		hash += (subscribeid != null ? subscribeid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Subscribe)) {
			return false;
		}
		Subscribe other = (Subscribe) object;
		if ((this.subscribeid == null && other.subscribeid != null)
				|| (this.subscribeid != null && !this.subscribeid
						.equals(other.subscribeid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "jp.ac.tokushima_u.is.ll.entity.Subscribe[id=" + subscribeid
				+ "]";
	}

}
