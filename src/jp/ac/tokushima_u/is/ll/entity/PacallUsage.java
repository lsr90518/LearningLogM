package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class PacallUsage implements Serializable {

	private static final long serialVersionUID = -384967026770155551L;

	private String collectionId;
	private Date uploadStart;
	private Date uploadEnd;
	private Date composeStart;
	private Date composeEnd;
	private Date analyzeStart;
	private Date analyzeEnd;

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public Date getUploadStart() {
		return uploadStart;
	}

	public void setUploadStart(Date uploadStart) {
		this.uploadStart = uploadStart;
	}

	public Date getUploadEnd() {
		return uploadEnd;
	}

	public void setUploadEnd(Date uploadEnd) {
		this.uploadEnd = uploadEnd;
	}

	public Date getComposeStart() {
		return composeStart;
	}

	public void setComposeStart(Date composeStart) {
		this.composeStart = composeStart;
	}

	public Date getComposeEnd() {
		return composeEnd;
	}

	public void setComposeEnd(Date composeEnd) {
		this.composeEnd = composeEnd;
	}

	public Date getAnalyzeStart() {
		return analyzeStart;
	}

	public void setAnalyzeStart(Date analyzeStart) {
		this.analyzeStart = analyzeStart;
	}

	public Date getAnalyzeEnd() {
		return analyzeEnd;
	}

	public void setAnalyzeEnd(Date analyzeEnd) {
		this.analyzeEnd = analyzeEnd;
	}

}
