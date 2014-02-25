package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class UsersMyLangs implements Serializable {

	private static final long serialVersionUID = -1144865413085135996L;

	private String tUsers;
	private String myLangs;
	private Integer langOrder;

	public String gettUsers() {
		return tUsers;
	}

	public void settUsers(String tUsers) {
		this.tUsers = tUsers;
	}

	public String getMyLangs() {
		return myLangs;
	}

	public void setMyLangs(String myLangs) {
		this.myLangs = myLangs;
	}

	public Integer getLangOrder() {
		return langOrder;
	}

	public void setLangOrder(Integer langOrder) {
		this.langOrder = langOrder;
	}

}
