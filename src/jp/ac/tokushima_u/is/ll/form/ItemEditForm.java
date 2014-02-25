package jp.ac.tokushima_u.is.ll.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ItemEditForm implements Serializable {

    private static final long serialVersionUID = 5957045426756171964L;
    private String itemId;
    private String tag;
    private HashMap<String, String> titleMap = new HashMap<String, String>();
    private String barcode;
    private String qrcode;
    private String rfid;
    private String note;
    private String place;
    private Double itemLat;
    private Double itemLng;
    private Float speed;
    private Integer itemZoom;
    private MultipartFile image;
    private String question;
    private String quesLan;
    private String categoryId;
    private int shareLevel = 0;
    private Boolean locationBased;
    private List<Long>questionTypeIds=new ArrayList<Long>();
    
    /**
     * Used in update
     */
    private boolean fileExist = false;

    public ItemEditForm() {
    }
    
    public Float getSpeed() {
		return speed;
	}

	public void setSpeed(Float speed) {
		this.speed = speed;
	}

	public Boolean getLocationBased() {
		return locationBased;
	}

	public void setLocationBased(Boolean locationBased) {
		this.locationBased = locationBased;
	}

	public List<Long> getQuestionTypeIds() {
		return questionTypeIds;
	}

	public void setQuestionTypeIds(List<Long> questionTypeIds) {
		this.questionTypeIds = questionTypeIds;
	}

	public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public Integer getItemZoom() {
        return itemZoom;
    }

    public void setItemZoom(Integer itemZoom) {
        this.itemZoom = itemZoom;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getQuesLan() {
        return quesLan;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuesLan(String quesLan) {
        this.quesLan = quesLan;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isFileExist() {
        return fileExist;
    }

    public void setFileExist(boolean fileExist) {
        this.fileExist = fileExist;
    }

	public int getShareLevel() {
		return shareLevel;
	}

	public void setShareLevel(int shareLevel) {
		this.shareLevel = shareLevel;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setTitleMap(HashMap<String, String> titleMap) {
		this.titleMap = titleMap;
	}

	public HashMap<String, String> getTitleMap() {
		return titleMap;
	}
}
