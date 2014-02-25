package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class PacallPhotoTag implements Serializable{

	private static final long serialVersionUID = 1740428771778964637L;

	private String id;
	private String photoId;
	private String tagId;
	private String extra;

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

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

}
