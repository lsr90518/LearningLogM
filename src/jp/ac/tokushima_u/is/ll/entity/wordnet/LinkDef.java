package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class LinkDef implements Serializable {
	private static final long serialVersionUID = 3624425515173374059L;

	private Long id;
	private String pos;
	private String lang;// Language
	private String def;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}
}
