package jp.ac.tokushima_u.is.ll.service.helper;

import java.util.Date;

import jp.ac.tokushima_u.is.ll.util.Constants;

/**
 * 
 * @author li
 */
public class QuizCondition implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String userId;
	private Integer imageLevel;
	private Double lat;
	private Double lng;
	private Float speed;
	private String quizid;
	private String answer;
	private Integer alarmtype;
	private Integer pass;
	private Integer versioncode;
	private String itemId;
	private Date createDateFrom;
	private Boolean isRecommended;

	// private String lanCode;
	// private Boolean hasImage;
	// private Integer timeflg;
	// private String myquizlogid;
	// private Integer courseid;
	// private Boolean isMyLog;

	public Float getSpeed() {
		return speed;
	}

	public Boolean getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
	}

	public Date getCreateDateFrom() {
		return createDateFrom;
	}

	public void setCreateDateFrom(Date createDateFrom) {
		this.createDateFrom = createDateFrom;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getVersioncode() {
		return versioncode;
	}

	public void setVersioncode(Integer versioncode) {
		this.versioncode = versioncode;
	}

	public Integer getPass() {
		return pass;
	}

	public void setPass(Integer pass) {
		this.pass = pass;
	}

	public Integer getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(Integer alarmtype) {
		this.alarmtype = alarmtype;
	}

	public String getQuizid() {
		return quizid;
	}

	public void setQuizid(String quizid) {
		this.quizid = quizid;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Integer getImageLevel() {
		if (imageLevel == null)
			return Constants.NormalLevel;
		return imageLevel;
	}

	public void setImageLevel(Integer imageLevel) {
		this.imageLevel = imageLevel;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
