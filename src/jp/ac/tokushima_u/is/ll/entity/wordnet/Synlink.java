package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Synlink implements Serializable {
	private static final long serialVersionUID = 3470316448674115070L;

	private Long id;
	private String synset1;
	private String synset2;
	private String link;
	private String src;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSynset1() {
		return synset1;
	}

	public void setSynset1(String synset1) {
		this.synset1 = synset1;
	}

	public String getSynset2() {
		return synset2;
	}

	public void setSynset2(String synset2) {
		this.synset2 = synset2;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
