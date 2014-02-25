package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class StudyPhase implements Serializable {

	private static final long serialVersionUID = -5763799662608177058L;
	private String id;
	private String userId;
	private Date startTime;
	private Date endTime;
	private Double minlat;
	private Double minlng;
	private Double maxlat;
	private Double maxlng;
	private Integer quiznum;
	private Integer additemnum;
	private Integer viewitemnum;
	private Date createTime;

	public StudyPhase(Date startTime, Date endTime, Double minlat,
			Double minlng, Double maxlat, Double maxlng, Integer quiznum,
			Integer additemnum, Integer viewitemnum, String userId) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.minlat = minlat;
		this.minlng = minlng;
		this.maxlat = maxlat;
		this.maxlng = maxlng;
		this.quiznum = quiznum;
		this.additemnum = additemnum;
		this.viewitemnum = viewitemnum;
		this.createTime = new Date();
		this.userId = userId;
	}

	public StudyPhase() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getNum() {
		int n = 0;
		if (this.quiznum != null)
			n = n + this.quiznum;
		if (this.viewitemnum != null)
			n = n + this.viewitemnum;
		if (this.additemnum != null)
			n = n + this.additemnum;
		return n;
	}

	public Double getMinlat() {
		return minlat;
	}

	public void setMinlat(Double minlat) {
		this.minlat = minlat;
	}

	public Double getMinlng() {
		return minlng;
	}

	public void setMinlng(Double minlng) {
		this.minlng = minlng;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getMaxlat() {
		return maxlat;
	}

	public void setMaxlat(Double maxlat) {
		this.maxlat = maxlat;
	}

	public Double getMaxlng() {
		return maxlng;
	}

	public void setMaxlng(Double maxlng) {
		this.maxlng = maxlng;
	}

	public Integer getQuiznum() {
		return quiznum;
	}

	public void setQuiznum(Integer quiznum) {
		this.quiznum = quiznum;
	}

	public Integer getAdditemnum() {
		return additemnum;
	}

	public void setAdditemnum(Integer additemnum) {
		this.additemnum = additemnum;
	}

	public Integer getViewitemnum() {
		return viewitemnum;
	}

	public void setViewitemnum(Integer viewitemnum) {
		this.viewitemnum = viewitemnum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
