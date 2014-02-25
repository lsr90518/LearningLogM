package jp.ac.tokushima_u.is.ll.entity;

import java.io.Serializable;

public class PacallTag implements Serializable {

	private static final long serialVersionUID = 1310773647402015053L;
	
	public static final String TAG_DARK = "dark";
	public static final String TAG_DEFOCUSED = "defocused";
	public static final String TAG_DUPLICATED = "duplicated";
	public static final String TAG_FEATURE = "feature";
	public static final String TAG_MANUAL = "manual";
	public static final String TAG_TEXT = "text";
	public static final String TAG_FACE = "face";

	private String id;
	private String tag;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
