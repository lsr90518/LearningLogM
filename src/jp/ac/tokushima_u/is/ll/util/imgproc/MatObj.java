package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;

public class MatObj implements Serializable{

	private static final long serialVersionUID = -6612223006658139103L;

	private int rows;
	private int cols;
	private int type;
	private List<MatData> datas;
	
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getCols() {
		return cols;
	}
	public void setCols(int cols) {
		this.cols = cols;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<MatData> getDatas() {
		return datas;
	}
	public void setDatas(List<MatData> datas) {
		this.datas = datas;
	}
	
	public Mat toMat(){
		Mat mat = new Mat(this.rows, this.cols, this.type);
		for(MatData data: this.datas){
			mat.put(data.getRow(), data.getCol(), data.getData());
		}
		return mat;
	}
	
	public static MatObj fromMat(Mat mat){
		MatObj obj = new MatObj();
		obj.setCols(mat.cols());
		obj.setRows(mat.rows());
		obj.setType(mat.type());
		List<MatData> datas = new ArrayList<>();
		for(int i=0;i<mat.rows();i++){
			for(int j=0;j<mat.cols();j++){
				double[] data = mat.get(i, j);
				datas.add(new MatData(i, j, data));
			}
		}
		obj.setDatas(datas);
		return obj;
	}
}
