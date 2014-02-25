package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class PacallItemRecord implements Serializable {

	private static final long serialVersionUID = -331174268463127828L;

	private String id;
	private String itemId;
	private String photoId;
	private Integer know;
	private Integer what;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public Integer getKnow() {
		return know;
	}

	public void setKnow(Integer know) {
		this.know = know;
	}

	public Integer getWhat() {
		return what;
	}

	public void setWhat(Integer what) {
		this.what = what;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
