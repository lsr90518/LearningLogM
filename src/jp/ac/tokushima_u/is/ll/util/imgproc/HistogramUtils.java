package jp.ac.tokushima_u.is.ll.util.imgproc;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.google.common.collect.Lists;

/**
 * Compare two images, Using histogram comparison
 * @see http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/histograms/histogram_comparison/histogram_comparison.html
 * @author houbin
 *
 */
public class HistogramUtils {
	
	private static double compareImage(Mat mat1, Mat mat2){
		Mat hist1 = HistogramUtils.extractHist(mat1);
		Mat hist2 = HistogramUtils.extractHist(mat2);
		//Using Correlation, larger is better
		return Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
	}
	
	public static double compareHist(Mat hist1, Mat hist2){
		return Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CORREL);
	}
	
	public static Mat extractHist(Mat mat){
		Mat hsv = new Mat();
		Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
		
		// 对hue通道使用30个bin,对saturatoin通道使用32个bin
		int hBins = 50, sBins = 60;
		MatOfInt histSize = new MatOfInt(hBins, sBins);
		
		// hue的取值范围从0到256, saturation取值范围从0到180
		MatOfFloat ranges = new MatOfFloat(0, 256, 0, 180);
		MatOfInt channels = new MatOfInt(0, 1);
		Mat hist = new Mat();
		Imgproc.calcHist(Lists.newArrayList(hsv), channels, new Mat(), hist, histSize, ranges, false);
		Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX, -1, new Mat());
		return hist;
	}
	
	public static void main(String[] args){
		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mat mat1 = Highgui.imread("/Users/houbin/Desktop/testphotos/021.png");
		Mat mat2 = Highgui.imread("/Users/houbin/Desktop/testphotos/022.png");
		
		Mat hist1 = HistogramUtils.extractHist(mat1);
		Mat hist2 = HistogramUtils.extractHist(mat2);
		System.out.println(HistogramUtils.compareHist(hist1, hist2));
	}
}
