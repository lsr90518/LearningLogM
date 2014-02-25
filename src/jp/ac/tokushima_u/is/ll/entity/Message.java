package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	private String content;
	private Integer typ;
	
	public Integer getTyp() {
		return typ;
	}

	public void setTyp(Integer typ) {
		this.typ = typ;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
}
