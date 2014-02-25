package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import com.google.protobuf.InvalidProtocolBufferException;

public class FaceDetectionUtil {
	
//	private static final Logger logger = LoggerFactory.getLogger(FaceDetectionUtil.class);

	public static List<Rect> detect(File file, double minSize) {
		CascadeClassifier faceDetector = new CascadeClassifier(FaceDetectionUtil.class.getResource("/jp/ac/tokushima_u/is/ll/util/imgproc/haarcascade_frontalface_alt2.xml").getPath());
	    Mat image = Highgui.imread(file.getPath());
	    // Detect faces in the image.
	    // MatOfRect is a special container class for Rect.
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);
	    List<Rect> rectList = new ArrayList<>();
	    for(Rect rect: faceDetections.toList()){
	    	if(rect.width>minSize && rect.height>minSize){
	    		rectList.add(rect);
	    	}
	    }
	    return rectList;
	}
	
	public static void main(String[] args){
		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File file = new File("/Users/houbin/Desktop/122.png");
		Mat image = Highgui.imread(file.getPath());
		long s = System.currentTimeMillis();

		byte[] ser = OpenCVUtils.serialize(image);
		System.out.println(System.currentTimeMillis()-s);
		s = System.currentTimeMillis();
		try {
			Mat mat = OpenCVUtils.deserialize(ser);
			System.out.println(System.currentTimeMillis() -s);
			Highgui.imwrite("/Users/houbin/Desktop/122_cop.png", mat);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//Highgui.imwrite("/Users/houbin/Desktop/122_copy.png", m.toMat());
		
		
//		List<Rect> faceDetections = FaceDetectionUtil.detect(file);
//		
//		Mat image = Highgui.imread(file.getPath());
//		System.out.println(String.format("Detected %s faces", faceDetections.size()));
//	    // Draw a bounding box around each face.
//		System.out.println(new Gson().toJson(faceDetections));
//	    for (Rect rect : faceDetections) {
//	        Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
//	    }
//	    // Save the visualized detection.
//	    String filename = "/Users/houbin/Desktop/faceDetection.png";
//	    System.out.println(String.format("Writing %s", filename));
//	    Highgui.imwrite(filename, image);
	}
}
