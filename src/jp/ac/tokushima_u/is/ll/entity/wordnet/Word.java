package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Word implements Serializable {
	private static final long serialVersionUID = -2350714043289310504L;

	private Long wordid;
	private String lang;// Language
	private String lemma;
	private String pron;
	private String pos;

	public Long getWordid() {
		return wordid;
	}

	public void setWordid(Long wordid) {
		this.wordid = wordid;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPron() {
		return pron;
	}

	public void setPron(String pron) {
		this.pron = pron;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

}
