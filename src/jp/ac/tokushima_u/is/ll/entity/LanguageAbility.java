package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class LanguageAbility implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id;
	private String languageId;
	private String authorId;
	private Double ability;
	private Integer righttimes;
	private Integer totaltimes;
	private Integer disabled;
	private Date createTime;

	public Integer getRighttimes() {
		return righttimes;
	}

	public void setRighttimes(Integer righttimes) {
		this.righttimes = righttimes;
	}

	public Integer getTotaltimes() {
		return totaltimes;
	}

	public void setTotaltimes(Integer totaltimes) {
		this.totaltimes = totaltimes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Double getAbility() {
		return ability;
	}

	public void setAbility(Double ability) {
		this.ability = ability;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
