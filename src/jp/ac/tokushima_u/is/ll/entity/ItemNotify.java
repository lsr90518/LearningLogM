package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class ItemNotify implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String authorId;
	private Date createTime;
	private Integer feedback;
	private Integer alarmType;
	private String quizid;
	private Integer notifyMode;

	public Integer getNotifyMode() {
		return notifyMode;
	}

	public void setNotifyMode(Integer notifyMode) {
		this.notifyMode = notifyMode;
	}

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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getFeedback() {
		return feedback;
	}

	public void setFeedback(Integer feedback) {
		this.feedback = feedback;
	}

	public Integer getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(Integer alarmType) {
		this.alarmType = alarmType;
	}

	public String getQuizid() {
		return quizid;
	}

	public void setQuizid(String quizid) {
		this.quizid = quizid;
	}
}
