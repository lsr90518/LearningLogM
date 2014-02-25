package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class ItemTitle implements Serializable {

	private static final long serialVersionUID = 6794261676719764732L;
	private String id;
	private String content;
	private String language;
	private String item;

//	public  String ItemTitle(Item item2) {
//		// TODO Auto-generated constructor stub
//		return content;
//		
//	}
//	public void ItemTitle2(Item item3) {
//		// TODO Auto-generated constructor stub
//		item3.setContent(content);
//		
//	}
	
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

}
