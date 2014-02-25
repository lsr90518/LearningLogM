package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

/**
 * 
 * @author lemonrain
 */
public class MyQuizChoice implements Serializable {

	private static final long serialVersionUID = 1432226372228624840L;
	private String id;
	private String content;
	private Integer number;
	private String note;
	private String myquizId;
	private String itemId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getMyquizId() {
		return myquizId;
	}

	public void setMyquizId(String myquizId) {
		this.myquizId = myquizId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
