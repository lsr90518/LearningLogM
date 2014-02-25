package jp.ac.tokushima_u.is.ll.entity.wordnet;

import java.io.Serializable;

/**
 * @author uosaki
 */
public class Xlink implements Serializable {
	private static final long serialVersionUID = 8691400889922462903L;

	private Long id;
	private String synset;
	private String resource;
	private String xref;
	private String misc;
	private String mconfidence;

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

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getXref() {
		return xref;
	}

	public void setXref(String xref) {
		this.xref = xref;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

	public String getMconfidence() {
		return mconfidence;
	}

	public void setMconfidence(String mconfidence) {
		this.mconfidence = mconfidence;
	}

}
