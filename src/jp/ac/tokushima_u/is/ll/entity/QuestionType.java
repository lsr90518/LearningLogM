package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
/**
 * 
 * Table: D_QUESTIONTYPE
 */
public class QuestionType implements Serializable {
    private static final long serialVersionUID = -7714583775370107525L;
    
    private Long id;
    private String title;
    private String info;
    private Integer orderby;
    private Boolean checked;
    
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Integer getOrderby() {
		return orderby;
	}
	public void setOrderby(Integer orderby) {
		this.orderby = orderby;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
}
