package jp.ac.tokushima_u.is.ll.entity;

public class ChartObj {
	private int num;
	private String attr;
	
	public ChartObj(String attr, int num){
		this.num = num;
		this.attr = attr;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}
	
	

}
