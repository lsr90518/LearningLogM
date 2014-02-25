package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import com.google.common.primitives.Doubles;
import com.google.protobuf.InvalidProtocolBufferException;

public class OpenCVUtils {
	
	public static Mat convertBufferedImageToMat(BufferedImage image){
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
		return mat;
	}
	
	public static BufferedImage convertMatToBufferedImage(Mat mat){
	    MatOfByte matOfByte = new MatOfByte();
	    Highgui.imencode(".jpg", mat, matOfByte);
	    byte[] byteArray = matOfByte.toArray();
	    BufferedImage bufImage = null;
	    try {
	        InputStream in = new ByteArrayInputStream(byteArray);
	        bufImage = ImageIO.read(in);
	        return bufImage;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public static byte[] convertMatToBytes(Mat mat){
	    MatOfByte matOfByte = new MatOfByte();
	    Highgui.imencode(".jpg", mat, matOfByte);
	    byte[] byteArray = matOfByte.toArray();
	    return byteArray;
	}
	
	public static Mat convertBytesToMat(byte[] byteArray){
		BufferedImage bi;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(byteArray));
		} catch (IOException e) {
			e.printStackTrace();
			return new Mat();
		}
		return convertBufferedImageToMat(bi);
	}
	
	public static byte[] serialize(Mat mat){
		MatModelProtos.MatModel.Builder builder = MatModelProtos.MatModel.newBuilder();
		builder.setRows(mat.rows());
		builder.setCols(mat.cols());
		builder.setType(mat.type());
		for(int i=0;i<mat.rows();i++){
			for(int j=0;j<mat.cols();j++){
				MatModelProtos.MatModel.MatData.Builder data = MatModelProtos.MatModel.MatData.newBuilder();
				data.setRow(i);
				data.setCol(j);
				data.addAllData(Doubles.asList(mat.get(i, j)));
				builder.addDatas(data);
			}
		}
		MatModelProtos.MatModel model = builder.build();
		return model.toByteArray();
	}
	
	public static Mat deserialize(byte[] s) throws InvalidProtocolBufferException{
		MatModelProtos.MatModel matModel = MatModelProtos.MatModel.parseFrom(s);
		Mat mat = new Mat(matModel.getRows(), matModel.getCols(), matModel.getType());
		for(MatModelProtos.MatModel.MatData data: matModel.getDatasList()){
			mat.put(data.getRow(), data.getCol(), Doubles.toArray(data.getDataList()));
		}
		return mat;
	}
	
	public static void main(String[] args) throws Exception{
		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String file = "/Users/houbin/Desktop/00021947.JPG";
		Mat mat = Highgui.imread(file);
		for(int i=0;i<10;i++){
			long s = System.currentTimeMillis();
			byte[] b = OpenCVUtils.serialize(mat);
			System.out.println(System.currentTimeMillis()-s);
			System.out.println(b.length);
			s = System.currentTimeMillis();
			Mat mat1 = OpenCVUtils.deserialize(b);
			System.out.println(System.currentTimeMillis()-s);
			Highgui.imwrite("/Users/houbin/Desktop/abc.jpg", mat1);
			System.out.println();
		}
	}
}
