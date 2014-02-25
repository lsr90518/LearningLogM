package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;


public class TaskScript implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3911381531055477753L;

	private String id;
	
	private String script;
	
	private Integer num;
	
	private Integer zoom;
	
	private Double lat;
	
	private Double lng;
	
	private String image;
	
	private String task_id;
	
	private String location_based;
	
	private String image_name;
	
	private Double related_task;
	
	private String place;
	
	private String title;
	private String level;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getZoom() {
		return zoom;
	}

	public void setZoom(Integer zoom) {
		this.zoom = zoom;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getLocation_based() {
		return location_based;
	}

	public void setLocation_based(String location_based) {
		this.location_based = location_based;
	}

	public String getImage_name() {
		return image_name;
	}

	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}

	public Double getRelated_task() {
		return related_task;
	}

	public void setRelated_task(Double related_task) {
		this.related_task = related_task;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	
	
}
