package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PacallPhoto implements Serializable {

	private static final long serialVersionUID = -6368326323433134464L;

	// Basic Date
	private String id;
	private String userId;
	private Date createTime;
	private String hash;
	private String filename;
	private String ext;
	private Integer phototype;// 0:common, 1:SenseCam
	private Date photodate; // Date
	// Values
	private String reason;
	private Double clr; // CLR
	private Double tmp; // Temperature
	private Double accX;
	private Double accY;
	private Double accZ;
	private Double magX;
	private Double magY;
	private Double magZ;
	private Double lat;
	private Double lng;
	private String collectionId;

	// Dark
	private Double brightness;
	// Blurry
	private Double blurriness;
	// Duplicated
	private String parentId;
	// Face Positions
	private Integer facenum;
	private String facepos;
	// Text
	private String textcontent;
	private String textpos; // TextPosition
	//Feature
	private Integer featurenum;
	private String featurepos;

	// ===============================
	private List<PacallPhoto> children;
	private List<PacallSimilar> mySimilars;
	private List<PacallSimilar> otherSimilars;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getPhotodate() {
		return photodate;
	}

	public void setPhotodate(Date photodate) {
		this.photodate = photodate;
	}

	public Double getClr() {
		return clr;
	}

	public void setClr(Double clr) {
		this.clr = clr;
	}

	public Double getTmp() {
		return tmp;
	}

	public void setTmp(Double tmp) {
		this.tmp = tmp;
	}

	public Double getAccX() {
		return accX;
	}

	public void setAccX(Double accX) {
		this.accX = accX;
	}

	public Double getAccY() {
		return accY;
	}

	public void setAccY(Double accY) {
		this.accY = accY;
	}

	public Double getAccZ() {
		return accZ;
	}

	public void setAccZ(Double accZ) {
		this.accZ = accZ;
	}

	public Double getMagX() {
		return magX;
	}

	public void setMagX(Double magX) {
		this.magX = magX;
	}

	public Double getMagY() {
		return magY;
	}

	public void setMagY(Double magY) {
		this.magY = magY;
	}

	public Double getMagZ() {
		return magZ;
	}

	public void setMagZ(Double magZ) {
		this.magZ = magZ;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public Integer getPhototype() {
		return phototype;
	}

	public void setPhototype(Integer phototype) {
		this.phototype = phototype;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public Double getBrightness() {
		return brightness;
	}

	public void setBrightness(Double brightness) {
		this.brightness = brightness;
	}

	public Double getBlurriness() {
		return blurriness;
	}

	public void setBlurriness(Double blurriness) {
		this.blurriness = blurriness;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public List<PacallPhoto> getChildren() {
		return children;
	}

	public void setChildren(List<PacallPhoto> children) {
		this.children = children;
	}

	public String getFacepos() {
		return facepos;
	}

	public void setFacepos(String facepos) {
		this.facepos = facepos;
	}

	public String getTextpos() {
		return textpos;
	}

	public void setTextpos(String textpos) {
		this.textpos = textpos;
	}

	public Integer getFacenum() {
		return facenum;
	}

	public void setFacenum(Integer facenum) {
		this.facenum = facenum;
	}

	public List<PacallSimilar> getOtherSimilars() {
		return otherSimilars;
	}

	public void setOtherSimilars(List<PacallSimilar> otherSimilars) {
		this.otherSimilars = otherSimilars;
	}

	public List<PacallSimilar> getMySimilars() {
		return mySimilars;
	}

	public void setMySimilars(List<PacallSimilar> mySimilars) {
		this.mySimilars = mySimilars;
	}

	public Integer getFeaturenum() {
		return featurenum;
	}

	public void setFeaturenum(Integer featurenum) {
		this.featurenum = featurenum;
	}

	public String getFeaturepos() {
		return featurepos;
	}

	public void setFeaturepos(String featurepos) {
		this.featurepos = featurepos;
	}

	public String getTextcontent() {
		return textcontent;
	}

	public void setTextcontent(String textcontent) {
		this.textcontent = textcontent;
	}
}
