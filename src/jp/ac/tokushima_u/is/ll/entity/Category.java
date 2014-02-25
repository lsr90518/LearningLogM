package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class Category implements Serializable {

	private static final long serialVersionUID = 7395483729271687885L;
	private String id;
	private String name;
	private String note; // 備考
	private String parent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

}
