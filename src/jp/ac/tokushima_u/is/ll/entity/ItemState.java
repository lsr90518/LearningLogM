package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class ItemState implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String authorId;
	private String itemId;
	private Integer rememberState;
	private Integer experState;
	private String quizState;
	private Date createDate;
	private Date updateDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getRememberState() {
		return rememberState;
	}

	public void setRememberState(Integer rememberState) {
		this.rememberState = rememberState;
	}

	public Integer getExperState() {
		return experState;
	}

	public void setExperState(Integer experState) {
		this.experState = experState;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getQuizState() {
		return quizState;
	}

	public void setQuizState(String quizState) {
		this.quizState = quizState;
	}
}
