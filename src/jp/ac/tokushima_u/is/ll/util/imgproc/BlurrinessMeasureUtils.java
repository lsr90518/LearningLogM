package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class BlurrinessMeasureUtils {
	public static double judgeBlurriness(InputStream is) throws IOException {
		Mat src = OpenCVUtils.convertBufferedImageToMat(ImageIO.read(is));
		Mat picone = new Mat(src.size(), src.type());
		Imgproc.cvtColor(src, picone, Imgproc.COLOR_BGR2YCrCb);
		double[] gety;
		double z=0, zy1=0, zy2=0, total = 0;
		double gety1=0, gety2=0;
		double result = 0;
		for(int ix=0;ix<picone.height();ix++){
			gety1=0;
			gety2=0;
			zy1=0;
			zy2=0;
			for(int jy=0;jy<picone.width();jy++){
				gety = picone.get(ix, jy);
				z=0.5*gety[0]-gety1+0.5*gety2+zy1-0.5*zy2;
				total += z;
				gety2=gety1;
				gety1=gety[0];
				zy2=zy1;
				zy1=z;
			}
		}
		picone.release();
		result = Math.abs(total/(src.height()*src.width()));
		return result;
	}
	
	/*
	 * http://answers.opencv.org/question/5395/calculate-blurness-and-sharpness-of-a-given-image/
	 */
	
	public static void main(String[] args){
//		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		Mat mat = Highgui.imread("/Users/houbin/Documents/Vicon Revue Data/453C8352-57E0-A6CD-A037-1A0760045495/B000096f00000019f20110823074836F.JPG");
//		System.out.println(BlurrinessMeasureUtils.detect(mat));
	}
}
