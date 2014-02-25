package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;


public class TaskItem implements Serializable {

	private static final long serialVersionUID = 6744330347786261795L;

	private String id;
	private String item_id;
	private String task_id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	

}
