package jp.ac.tokushima_u.is.ll.form;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class TaskScriptForm implements Serializable {

    private static final long serialVersionUID = 5957045426756171964L;
    private String taskId;
    private Double lat;
    private Double lng;
    private Integer zoom;
    private Boolean locationBased = Boolean.TRUE;
    private String script;
    private MultipartFile image;
    private Integer num;
    private String image_name;
    
    private String process1;
    private String process2;
    private String process3;
    private String process4;
    private String process5;
    private String process6;
    private String process7;
    private String process8;
    private String process9;
    private String process10;
    private String process11;
    
    public void clear(){
    	this.lat = null;
    	this.taskId = null;
    	this.lng = null;
    	this.zoom = null;
    	this.locationBased = Boolean.TRUE;
    	this.script = null;
    	this.image = null;
    	this.num = null;
    	this.setImage_name(null);
    };
    
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Boolean getLocationBased() {
		return locationBased;
	}
	public void setLocationBased(Boolean locationBased) {
		this.locationBased = locationBased;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
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

	public String getImage_name() {
		return image_name;
	}

	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}

	public String getProcess1() {
		return process1;
	}

	public void setProcess1(String process1) {
		this.process1 = process1;
	}

	public String getProcess2() {
		return process2;
	}

	public void setProcess2(String process2) {
		this.process2 = process2;
	}

	public String getProcess3() {
		return process3;
	}

	public void setProcess3(String process3) {
		this.process3 = process3;
	}

	public String getProcess5() {
		return process5;
	}

	public void setProcess5(String process5) {
		this.process5 = process5;
	}

	public String getProcess4() {
		return process4;
	}

	public void setProcess4(String process4) {
		this.process4 = process4;
	}

	public String getProcess6() {
		return process6;
	}

	public void setProcess6(String process6) {
		this.process6 = process6;
	}

	public String getProcess7() {
		return process7;
	}

	public void setProcess7(String process7) {
		this.process7 = process7;
	}

	public String getProcess8() {
		return process8;
	}

	public void setProcess8(String process8) {
		this.process8 = process8;
	}

	public String getProcess9() {
		return process9;
	}

	public void setProcess9(String process9) {
		this.process9 = process9;
	}

	public String getProcess10() {
		return process10;
	}

	public void setProcess10(String process10) {
		this.process10 = process10;
	}

	public String getProcess11() {
		return process11;
	}

	public void setProcess11(String process11) {
		this.process11 = process11;
	}

}
