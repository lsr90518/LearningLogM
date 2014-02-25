package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.Serializable;

public class MatData implements Serializable{
	private static final long serialVersionUID = -8685887627686587040L;
	
	private int row;
	private int col;
	private double[] data;
	
	public MatData(){}
	
	public MatData(int row, int col, double[] data) {
		super();
		this.row = row;
		this.col = col;
		this.data = data;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public double[] getData() {
		return data;
	}
	public void setData(double[] data) {
		this.data = data;
	}
}
