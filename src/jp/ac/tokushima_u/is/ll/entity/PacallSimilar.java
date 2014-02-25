package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

import jp.ac.tokushima_u.is.ll.dto.ItemDTO;

public class PacallSimilar implements Serializable {

	private static final long serialVersionUID = 7564770389323433129L;

	public static final int REASON_TEXT = 0;
	public static final int REASON_FEATURE = 1;

	private String id;
	private String photoId;
	private String itemId;
	private Integer reason;
	private String extra;

	// =======================================
	private ItemDTO item;

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

	public Integer getReason() {
		return reason;
	}

	public void setReason(Integer reason) {
		this.reason = reason;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public ItemDTO getItem() {
		return item;
	}

	public void setItem(ItemDTO item) {
		this.item = item;
	}

}
