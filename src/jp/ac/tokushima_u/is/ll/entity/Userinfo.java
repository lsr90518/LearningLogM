package jp.ac.tokushima_u.is.ll.entity;

public class Userinfo {
	private int id;
	private String nickname;
	private String natilanguage;
	private String gender;
	private String jlpt;
	private String month;
	private String major;
	private String model;
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNatilanguage() {
		return natilanguage;
	}
	public void setNatilanguage(String natilanguage) {
		this.natilanguage = natilanguage;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getJlpt() {
		return jlpt;
	}
	public void setJlpt(String jlpt) {
		this.jlpt = jlpt;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
