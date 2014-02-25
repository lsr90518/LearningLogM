package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

public class FeatureDetectionUtils {
	
	public static Mat extractDescription(Mat image) throws Exception{
		Mat mat = image.clone();
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		detector.detect(mat, keypoints);
		
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
		Mat desc = new Mat();
		extractor.compute(image, keypoints, desc);
		return desc;
	}
	
	public static MatOfKeyPoint extractKeypoints(Mat image) throws Exception{
		Mat mat = image.clone();
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
		MatOfKeyPoint keypoints = new MatOfKeyPoint();
		detector.detect(mat, keypoints);
		return keypoints;
	}
	
	public static List<DMatch> matchImage(Mat desc1, Mat desc2) throws Exception{

		if(desc1.type()!=desc2.type()){
			throw new Exception("desc1.type="+desc1.type()+" not equal desc2.type="+desc2.type());
		}
		//-- Step 3: Matching descriptor vectors using FLANN matcher
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(desc1, desc2, matches);

		List<DMatch> matchesList = matches.toList();
		  
		//-- Draw only "good" matches (i.e. whose distance is less than 2*min_dist )
		//-- PS.- radiusMatch can also be used here.
		List<DMatch> goodMatches = new ArrayList<>();

		for(DMatch dmatch: matchesList){
			if(dmatch.distance<0.2){
				goodMatches.add(dmatch);
			}
		}
		MatOfDMatch good = new MatOfDMatch();
		good.fromList(goodMatches);
		return goodMatches;
	}
}
