package jp.ac.tokushima_u.is.ll.dto.pacall;

import java.io.Serializable;
import java.util.Date;

public class SensorDataItem implements Serializable, Comparable<SensorDataItem>{
	private static final long serialVersionUID = -2194454141618598537L;
	
	private Date date;
	private String flag;
	private String v1;
	private String v2;
	private String v3;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getV1() {
		return v1;
	}
	public void setV1(String v1) {
		this.v1 = v1;
	}
	public String getV2() {
		return v2;
	}
	public void setV2(String v2) {
		this.v2 = v2;
	}
	public String getV3() {
		return v3;
	}
	public void setV3(String v3) {
		this.v3 = v3;
	}
	@Override
	public int compareTo(SensorDataItem o) {
		if(this.date == null){
			return 0;
		}
		return this.date.compareTo(o.getDate());
	}
}