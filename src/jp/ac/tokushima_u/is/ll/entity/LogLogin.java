package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

public class LogLogin implements Serializable {

	private static final long serialVersionUID = 4524018797934251311L;

	public static final int DEVICE_MOBILE = 0;
	public static final int DEVICE_WEB = 1;

	private String id;
	private String user;
	private Date loginTime;
	private Integer loginDevice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Integer getLoginDevice() {
		return loginDevice;
	}

	public void setLoginDevice(Integer loginDevice) {
		this.loginDevice = loginDevice;
	}

}
