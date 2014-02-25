package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class Interest implements Serializable {

	private static final long serialVersionUID = 7834277200197322631L;

	private String id;
	private String name;
	
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
}
