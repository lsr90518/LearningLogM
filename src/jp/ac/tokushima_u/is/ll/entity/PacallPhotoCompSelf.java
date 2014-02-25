package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class PacallPhotoCompSelf implements Serializable {

	private static final long serialVersionUID = -2803553238211839331L;

	private String id;
	private String selfId;
	private String otherId;
	private Double score;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSelfId() {
		return selfId;
	}

	public void setSelfId(String selfId) {
		this.selfId = selfId;
	}

	public String getOtherId() {
		return otherId;
	}

	public void setOtherId(String otherId) {
		this.otherId = otherId;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}
