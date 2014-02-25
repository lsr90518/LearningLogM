package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class QuestionaryHabit implements Serializable {

	private static final long serialVersionUID = 1926382760792048685L;

	private String id;

	private String userId;
	private Integer timescore;
	private Integer geoscore;
	private Integer speedscore;
	private Integer timerecommend;
	private Integer georecommend;
	private Date createTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getTimescore() {
		return timescore;
	}

	public void setTimescore(Integer timescore) {
		this.timescore = timescore;
	}

	public Integer getGeoscore() {
		return geoscore;
	}

	public void setGeoscore(Integer geoscore) {
		this.geoscore = geoscore;
	}

	public Integer getSpeedscore() {
		return speedscore;
	}

	public void setSpeedscore(Integer speedscore) {
		this.speedscore = speedscore;
	}

	public Integer getTimerecommend() {
		return timerecommend;
	}

	public void setTimerecommend(Integer timerecommend) {
		this.timerecommend = timerecommend;
	}

	public Integer getGeorecommend() {
		return georecommend;
	}

	public void setGeorecommend(Integer georecommend) {
		this.georecommend = georecommend;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
