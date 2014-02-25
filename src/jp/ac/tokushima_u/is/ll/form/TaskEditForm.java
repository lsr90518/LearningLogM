package jp.ac.tokushima_u.is.ll.form;

import java.io.Serializable;

public class TaskEditForm implements Serializable {

    private static final long serialVersionUID = 5957045426756171964L;
    private String title;
    private String taskId;
    private String place;
    private Double lat;
    private Double lng;
    private Integer zoom;
    private Integer level;
    private String languageId;
    private String tag;
    //default setting for checkbox
    private Boolean locationBased=Boolean.TRUE;
    private Boolean isPublished=Boolean.FALSE;
    
    private String number;
    private String time_limit;
    
    private Integer page;
    
    
    
	public Boolean getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(Boolean isPublished) {
		this.isPublished = isPublished;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getZoom() {
		return zoom;
	}
	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
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

	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public Boolean getLocationBased() {
		return locationBased;
	}
	public void setLocationBased(Boolean locationBased) {
		this.locationBased = locationBased;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getTime_limit() {
		return time_limit;
	}
	public void setTime_limit(String time_limit) {
		this.time_limit = time_limit;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
}
