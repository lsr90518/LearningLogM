package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * 
 * @author houbin
 */
public class Item implements Serializable {
	
	public static final int SHARE_LEVEL_PUBLIC = 0;
	public static final int SHARE_LEVEL_PRIVATE = 1;

	private static final long serialVersionUID = -3583581414285655593L;
	private String id;
	private String note;
	private String barcode;
	private String qrcode;
	private String rfid;
	private String image;
	private String place;
	private Double itemLat;
	private Double itemLng;
	private Float speed;
	private Integer itemZoom;
	private Integer disabled;
	private Boolean locationbased;
	private Date createTime;
	private Date updateTime;
	private String authorId;
	private Integer shareLevel;
	private Double rating;
	private Boolean teacherConfirm;

	private Integer wrongtimes = 0;
	private Integer righttimes = 0;
	private Integer pass = 0;
	private String relogItem;
	private String category;
	private String content;

	/*-------------Quiz------------*/
	private String questionId;
	private Boolean questionResolved;

	/*-------------Quiz end------------*/
	
	
	

	@Override
	public boolean equals(Object object) {
		if (object == null) { return false; }
		   if (object == this) { return true; }
		   if (object.getClass() != getClass()) {
		     return false;
		   }
		   Item rhs = (Item) object;
		   return new EqualsBuilder()
		                 .appendSuper(super.equals(object))
		                 .append(this.getId(), rhs.getId())
		                 .isEquals();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Double getItemLat() {
		return itemLat;
	}

	public void setItemLat(Double itemLat) {
		this.itemLat = itemLat;
	}

	public Double getItemLng() {
		return itemLng;
	}

	public void setItemLng(Double itemLng) {
		this.itemLng = itemLng;
	}

	public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Integer getItemZoom() {
		return itemZoom;
	}

	public void setItemZoom(Integer itemZoom) {
		this.itemZoom = itemZoom;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public Boolean getLocationbased() {
		return locationbased;
	}

	public void setLocationbased(Boolean locationbased) {
		this.locationbased = locationbased;
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

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public Integer getShareLevel() {
		return shareLevel;
	}

	public void setShareLevel(Integer shareLevel) {
		this.shareLevel = shareLevel;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Boolean getTeacherConfirm() {
		return teacherConfirm;
	}

	public void setTeacherConfirm(Boolean teacherConfirm) {
		this.teacherConfirm = teacherConfirm;
	}

	public Integer getWrongtimes() {
		return wrongtimes;
	}

	public void setWrongtimes(Integer wrongtimes) {
		this.wrongtimes = wrongtimes;
	}

	public Integer getRighttimes() {
		return righttimes;
	}

	public void setRighttimes(Integer righttimes) {
		this.righttimes = righttimes;
	}

	public Integer getPass() {
		return pass;
	}

	public void setPass(Integer pass) {
		this.pass = pass;
	}

	public String getRelogItem() {
		return relogItem;
	}

	public void setRelogItem(String relogItem) {
		this.relogItem = relogItem;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public Boolean getQuestionResolved() {
		return questionResolved;
	}

	public void setQuestionResolved(Boolean questionResolved) {
		this.questionResolved = questionResolved;
	}

//	public String getContent() {
//		ItemTitle itemtitle=new ItemTitle();
//		return itemtitle.getContent();
//	}
//
//	public void setContent(String content) {
//		ItemTitle itemtitle=new ItemTitle();
//		itemtitle.setContent(this.content);
//	}
}
