package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class PacallPhotoCompItem implements Serializable {

	private static final long serialVersionUID = 6360184133724109088L;

	private String id;
	private String photoId;
	private String itemId;
	private Double score;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

}
