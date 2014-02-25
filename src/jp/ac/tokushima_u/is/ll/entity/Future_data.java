package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

import jp.ac.tokushima_u.is.ll.util.FilenameUtil;

//import org.apache.commons.lang.StringUtils;

/**
 * @author mouri　未来館のプログラム
 */

public class Future_data implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3630425322932724912L;

	private String id;

	private String nitizi;

	private String aite;

	private String place;

	private String old;

	private String ninzu;

	private String action;

	private String important;

	private String opinion;

	private String image;

	private String user_name;

	private String nitizi_end;

	private String groupshow;

	private String interaction;

	private String comentary;

	public String getNitizi() {
		return nitizi;
	}

	public void setNitizi(String nitizi) {
		this.nitizi = nitizi;
	}

	public String getAite() {
		return aite;
	}

	public void setAite(String aite) {
		this.aite = aite;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getOld() {
		return old;
	}

	public void setOld(String old) {
		this.old = old;
	}

	public String getNinzu() {
		return ninzu;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNinzu(String ninzu) {
		this.ninzu = ninzu;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setaite(String aite) {
		this.aite = aite;
	}

	public void setplace(String place) {
		this.place = place;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getNitizi_end() {
		return nitizi_end;
	}

	public void setNitizi_end(String nitizi_end) {
		this.nitizi_end = nitizi_end;
	}

	public String getInteraction() {
		return interaction;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public String getComentary() {
		return comentary;
	}

	public void setComentary(String comentary) {
		this.comentary = comentary;
	}

	public String getGroupshow() {
		return groupshow;
	}

	public void setGroupshow(String groupshow) {
		this.groupshow = groupshow;
	}

}