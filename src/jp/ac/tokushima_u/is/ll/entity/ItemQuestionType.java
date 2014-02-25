package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class ItemQuestionType implements Serializable {
	private static final long serialVersionUID = -5220512088806591048L;

	private String id;
	private String itemId;
	private Long questiontypeId;
	private String languageId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Long getQuestiontypeId() {
		return questiontypeId;
	}

	public void setQuestiontypeId(Long questiontypeId) {
		this.questiontypeId = questiontypeId;
	}

	public String getLanguageId() {
		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

}
