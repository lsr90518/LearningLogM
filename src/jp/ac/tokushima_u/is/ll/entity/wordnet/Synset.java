package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Synset implements Serializable {
	private static final long serialVersionUID = 6883505536652090641L;

	private Long id;
	private String synset;
	private String pos;
	private String name;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
