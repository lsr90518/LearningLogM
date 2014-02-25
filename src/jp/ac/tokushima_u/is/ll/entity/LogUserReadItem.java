package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Houbin
 */
public class LogUserReadItem implements Serializable {

	private static final long serialVersionUID = 732284046230958259L;
	private String id;
	private String userId;
	private String itemId;
	private Date createTime;
	private Double latitude;
	private Double longitude;
	private Float speed;
	private String relogItem;

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getRelogItem() {
		return relogItem;
	}

	public void setRelogItem(String relogItem) {
		this.relogItem = relogItem;
	}

}
