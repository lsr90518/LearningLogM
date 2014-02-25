package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BrightnessMeasureUtils {
	public static double judgeBrightness(InputStream is) throws IOException {
		BufferedImage image = ImageIO.read(is);
		double totalY = 0;
		int count = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color c = new Color(image.getRGB(x, y));
				totalY+=  (0.144 * c.getBlue() + 0.587 * c.getGreen() + 0.299 * c.getRed());// BGR
				count++;
			}
		}
		image = null;
		return totalY/count;
	}
	
	//Using OpenCV, Slower than JAVA
	public static double judgeBrightness(Mat mat) throws IOException {
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2YCrCb);
		double totalY = 0;
		int count = 0;
		for(int row = 0;row<mat.rows();row++){
			for(int col = 0;col<mat.cols();col++){
				totalY += mat.get(row, col)[0];
				count++;
			}
		}
		if(count ==0) return 0;
		return totalY/count;
	}
	
	public static void main(String[] args){
		try {
			System.out.println(BrightnessMeasureUtils.judgeBrightness(new FileInputStream("/Users/houbin/Documents/Vicon Revue Data/453C8352-57E0-A6CD-A037-1A0760045495/B000096f00000216f20110823084113F.JPG")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
