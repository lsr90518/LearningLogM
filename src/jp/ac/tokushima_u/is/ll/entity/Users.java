package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author houbin
 */
public class Users implements Serializable {

	public static final int USERS_AUTH_MEMBER = 0;
	public static final int USERS_AUTH_ADMIN = 1;

	private static final long serialVersionUID = 8594687786650062343L;
	private String id;
	private String pcEmail;
	private String password;
	private Integer auth;
	private Boolean enabled;
	private Boolean accountNotLocked;
	private String activecode;
	private Date lastLogin;
	private Date createTime;
	private Date updateTime;
	private String nickname;
	private String firstName;
	private String lastName;
	private String avatar;// Avatar ID
	private String defaultCategory;
	private Boolean receiveWeeklyNotification;
	private Integer userLevel;
	private Integer experiencePoint;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPcEmail() {
		return pcEmail;
	}

	public void setPcEmail(String pcEmail) {
		this.pcEmail = pcEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAuth() {
		return auth;
	}

	public void setAuth(Integer auth) {
		this.auth = auth;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountNotLocked() {
		return accountNotLocked;
	}

	public void setAccountNotLocked(Boolean accountNotLocked) {
		this.accountNotLocked = accountNotLocked;
	}

	public String getActivecode() {
		return activecode;
	}

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getDefaultCategory() {
		return defaultCategory;
	}

	public void setDefaultCategory(String defaultCategory) {
		this.defaultCategory = defaultCategory;
	}

	public Boolean getReceiveWeeklyNotification() {
		return receiveWeeklyNotification;
	}

	public void setReceiveWeeklyNotification(Boolean receiveWeeklyNotification) {
		this.receiveWeeklyNotification = receiveWeeklyNotification;
	}

	public Integer getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(Integer userLevel) {
		this.userLevel = userLevel;
	}

	public Integer getExperiencePoint() {
		return experiencePoint;
	}

	public void setExperiencePoint(Integer experiencePoint) {
		this.experiencePoint = experiencePoint;
	}

	//
	//
	//
	//
	// @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
	// private List<Item> myItemList = new ArrayList<Item>();
	// @OneToMany(mappedBy = "author")
	// private List<Answer> answerList = new ArrayList<Answer>();

	// ■wakebe レベルと経験値の追加

	// @ManyToMany(cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	// @IndexColumn(name="lang_order")
	// private List<Language> myLangs = new ArrayList<Language>();
	// @IndexColumn(name="lang_order")
	// @ManyToMany(cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	// private List<Language> studyLangs = new ArrayList<Language>();

	// @ManyToMany(
	// targetEntity=Interest.class,
	// cascade = {CascadeType.PERSIST, CascadeType.MERGE},
	// fetch=FetchType.LAZY)
	// @JoinTable(
	// name="r_users_interest",
	// joinColumns={@JoinColumn(name="user_id")},
	// inverseJoinColumns={@JoinColumn(name="interest_id")}
	// )
	// @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	// @OrderBy(value = "name asc")
	// private List<Interest> interests;

	// @OneToMany(mappedBy = "author")
	// private List<Mail> maillist = new ArrayList<Mail>();
	// @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	// private List<SendTime> sendTimeList = new ArrayList<SendTime>();
	// @OneToMany(mappedBy = "author")
	// private List<AutoSender> autoSenderList = new ArrayList<AutoSender>();
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	// private Set<ItemRating> itemRatingList;
	// @OneToOne(mappedBy = "author", fetch=FetchType.LAZY)
	// private Setting setting;

	// @OneToMany(mappedBy="author", cascade={CascadeType.ALL})
	// private List<MyQuiz> myquizList = new ArrayList<MyQuiz>();
	//
	// @ManyToMany
	// private List<Category> myCategoryList = new ArrayList<Category>();
	//
	// @OneToMany(mappedBy="author")
	// private List<StudySpeed> speedList = new ArrayList<StudySpeed>();
	//
	// @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	// private List<ItemState> myItemStateList = new ArrayList<ItemState>();
}
