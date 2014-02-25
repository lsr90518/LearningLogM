package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Sense implements Serializable {
	private static final long serialVersionUID = -2747900537842751589L;

	private Long id;
	private String synset;
	private Long wordid;
	private String lang;// Language
	private String rank;
	private Integer lexid;
	private Integer freq;
	private String src;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSynset() {
		return synset;
	}

	public void setSynset(String synset) {
		this.synset = synset;
	}

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

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public Integer getLexid() {
		return lexid;
	}

	public void setLexid(Integer lexid) {
		this.lexid = lexid;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
