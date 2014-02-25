package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.util.ArrayList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * from http://docs.opencv.org/doc/tutorials/imgproc/shapedescriptors/bounding_rects_circles/bounding_rects_circles.html
 * @author houbin
 *
 */
public class BoxesDetectionUtils {
	
	public static List<Rect> detect(Mat mat, double minSize){
		Mat srcGray = new Mat();
		Imgproc.cvtColor(mat, srcGray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(srcGray, srcGray, new Size(3, 3));
		
		Mat thresholdOutput = new Mat();
		Imgproc.threshold(srcGray, thresholdOutput, 100, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
		
		Highgui.imwrite("/Users/houbin/Pictures/com.tencent.ScreenCapture/b.png", thresholdOutput);
		
		Mat hierarchy = new Mat();
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(thresholdOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
		
		List<Rect> rectList = new ArrayList<>();
		for(int i=0;i<contours.size();i++){
			MatOfPoint2f contours2f = new MatOfPoint2f();
			contours2f.fromList(contours.get(i).toList());
			MatOfPoint2f rect2f = new MatOfPoint2f();
			Imgproc.approxPolyDP(contours2f, rect2f, 3, true);
			MatOfPoint rectMatOfPoint = new MatOfPoint();
			rectMatOfPoint.fromList(rect2f.toList());;
			Rect rect = Imgproc.boundingRect(rectMatOfPoint);
			if(rect.width>minSize && rect.height>minSize){
				rectList.add(rect);
			}
		}
		return rectList;
	}
	
	public static void main(String[] args){
		
		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat mat = Highgui.imread("/Users/houbin/Desktop/testphotos/00000001.JPG");
		Mat drawing = mat.clone();
		List<Rect> rectList = BoxesDetectionUtils.detect(mat, 30);
		System.out.println(rectList.size());
		int i=0;
		for(Rect rect:rectList){
			if(rect.width>30 && rect.height>30){
				Scalar color = new Scalar(0, 133, 0);
				Core.rectangle(drawing, rect.tl(), rect.br(), color, 2);
				Mat sub = mat.submat(rect);
				Highgui.imwrite("/Users/houbin/Desktop/testphotos/123_"+i+".png", sub);
				i++;
			}
		}
		
	}
}
