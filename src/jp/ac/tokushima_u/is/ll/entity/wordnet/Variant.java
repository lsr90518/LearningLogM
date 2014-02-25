package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Variant implements Serializable {
	private static final long serialVersionUID = -2982206180200217327L;

	private Long varid;
	private Long wordid;
	private String lang;// Language
	private String lemma;
	private String vartype;

	public Long getVarid() {
		return varid;
	}

	public void setVarid(Long varid) {
		this.varid = varid;
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

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getVartype() {
		return vartype;
	}

	public void setVartype(String vartype) {
		this.vartype = vartype;
	}

}
