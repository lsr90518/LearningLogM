package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.ac.tokushima_u.is.ll.util.SystemPathUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.commons.math3.stat.StatUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Rewrite from https://github.com/jasonlfunk/ocr-text-extraction/blob/master/extract_text 
 * @author houbin
 *
 */
public class TextExtraction {
	
	private static final Logger logger = LoggerFactory.getLogger(TextExtraction.class);
	
	private Mat img;
	private int imgY;
	private int imgX;
	private List<MatOfPoint> contours;
	
	public void detect(String inputFile){
		Mat orig_img = Highgui.imread(inputFile);
		img = new Mat();
		// Add a border to the image for processing's sake
		Imgproc.copyMakeBorder(orig_img, img, 50, 50, 50, 50, Imgproc.BORDER_CONSTANT);

		// Calculate the width and height of the image
		imgY = img.rows();
		imgX = img.cols();

		logger.debug("Image is " + img.rows() + "x" + img.cols());

		//Split out each channel
		List<Mat> colors = new ArrayList<>();
		Core.split(img, colors);//TODO ?
		Mat blue = colors.get(0);
		Mat green = colors.get(1);
		Mat red = colors.get(2);
		
		
		Mat blueEdges = new Mat(), greenEdges = new Mat(), redEdges = new Mat();
		// Run canny edge detection on each channel
		Imgproc.Canny(blue, blueEdges, 200, 250);
		Imgproc.Canny(green, greenEdges, 200, 250);
		Imgproc.Canny(red, redEdges, 200, 250);

		// Join edges back into image
		
		Mat edges = new Mat();
		Core.merge(Lists.newArrayList(blueEdges, greenEdges, redEdges), edges);
		

		// Find the contours
		contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.cvtColor(edges, edges, Imgproc.COLOR_BGR2GRAY);
		Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE, new Point(0, 0));
		
		// These are the boxes that we are determing
		List<MatOfPoint> keepersPoint = new ArrayList<>();
		List<Rect> keepersRect = new ArrayList<>();
		int idx = 0;
		// For each contour, find the bounding rectangle and decide
		// if it's one we care about
		for(MatOfPoint cnt: contours){
			Rect rect = Imgproc.boundingRect(cnt);
			// Check the contour and it's bounding box 
			if(keep(cnt) && includeBox(idx, hierarchy, cnt)){
				keepersPoint.add(cnt);
				keepersRect.add(rect);
			}else{
				
			}
			idx++;
		}
		
		// Make a white copy of our image
		Mat newImage = new Mat(edges.size(), edges.type(), new Scalar(255,255,255));
		List<Rect> boxes = new ArrayList<>();

		int fg = 0;
		int bg = 0;
		// For each box, find the foreground and background intensities
		for(int i=0; i<keepersPoint.size();i++){
			MatOfPoint cnt = keepersPoint.get(i);
			Rect box = keepersRect.get(i);
			
			double fgInt = 0.0;
			for(Point p:cnt.toList()){
				fgInt+= I(Double.valueOf(p.x).intValue(), Double.valueOf(p.y).intValue());
			}
			fgInt = fgInt/cnt.rows();
			
			logger.debug("FG Intensity for //"+i+" = "+fgInt);
			
			double[] bgIntArray = new double[]{
					I(box.x-1, box.y-1),
					I(box.x-1, box.y),
					I(box.x, box.y-1),
					I(box.x+box.width+1, box.y-1),
					I(box.x+box.width, box.y-1),
					I(box.x+box.width+1, box.y),
					I(box.x-1, box.y+box.height+1),
					I(box.x-1, box.y+box.height),
					I(box.x, box.y+box.height+1),
					I(box.x+box.width+1, box.y+box.height+1),
					I(box.x+box.width, box.y+box.height+1),
					I(box.x+box.width+1, box.y+box.height)
			};
			double bgInt = StatUtils.mean(bgIntArray);
			logger.debug("BG Intensity for //"+i+" = "+bgInt);
			
			if(fgInt >= bgInt){
				fg = 255;
				bg = 0;
			}else{
				fg = 0;
				bg = 255;
			}
			
			for(int x=box.x;x<box.x+box.width;x++){
				for(int y=box.y;y<box.y+box.height;y++){
					if(y>=imgY || x>=imgX){
						logger.debug("pixel out of bounds:"+y+","+x);
						continue;
					}
					if(I(x,y)>fgInt){
						newImage.put(y, x, bg);
					}else{
						newImage.put(y, x, fg);
					}
				}
			}
		}
		// blur a bit to improve ocr accurrecy
		Imgproc.blur(newImage, newImage, new Size(2, 2));
		Highgui.imwrite("/Users/houbin/Desktop/new_test.jpg", newImage);
//		Highgui.imwrite("edges.png",edges);
//		Highgui.imwrite("processed.png",processed);
//		Highgui.imwrite("rejected.png",rejected);
	}

	private boolean includeBox(int idx, Mat h, MatOfPoint cnt) {
//		if(DEBUGGING):
//			print str(idx)+":"
//			if(is_child(idx,h,cnt)):
//				print "\tIs a child"
//				print "\tparent "+str(get_parent(idx,h,cnt))+" has " + str(count_children(get_parent(idx,h,cnt),h,cnt)) + " children"
//				print "\thas " + str(count_children(idx,h,cnt)) + " children"

		if(isChild(idx,h,cnt) && countChildren(getParent(idx,h,cnt),h,cnt) <= 2){
//			if(DEBUGGING):
//				print "\t skipping: is an interior to a letter"
			return false;
		}

		if(countChildren(idx,h,cnt) > 2){
//			if(DEBUGGING):
//				print "\t skipping, is a container of letters"
			return false; 
		}

//		if(DEBUGGING):
//			print "\t keeping"
		return true;
	}

	private int countChildren(int idx, Mat h, MatOfPoint cnt) {
		// No children
		int count = 0;
		if(h.get(idx, 2)==null || h.get(idx, 2)[0] < 0){
			return 0;
		}else{
			//If the first child is a countor we care about
			// then count it, otherwise don't
			if(keep(c(Double.valueOf(h.get(idx, 2)[0]).intValue()))){
				count = 1;
			}else{
				count = 0;
			}

			// Also count all of the child's siblings and their children
			count += countSiblings(Double.valueOf(h.get(idx, 2)[0]).intValue(), h, cnt, true);
			return count;
		}
	}

	// Get the first parent of the contour that we care about
	private int getParent(int idx, Mat h, MatOfPoint cnt) {
		int parent;
		try {
			parent = Double.valueOf(h.get(idx, 3)[0]).intValue();
		} catch (Exception e) {
			parent = 0;
		}
		while(!keep(c(parent)) && parent > 0){
			parent = Double.valueOf(h.get(parent, 3)[0]).intValue();
		}
		return parent;
	}

	private MatOfPoint c(int parent) {
		System.out.println(parent);
		if(parent<0 || parent>contours.size()-1) return null;
		return contours.get(parent);
	}
	

	private int countSiblings(int idx, Mat h, MatOfPoint cnt, boolean inc_children) {
		// Include the children if neccessary
		int count = 0;
		if(inc_children){
			count = countChildren(idx,h,cnt);
		}else{
			count = 0;
		}

		// Look behind
		int p;
		try {
			p = Double.valueOf(h.get(idx, 0)[0]).intValue();
		} catch (Exception e) {
			p = 0;
		}
		while(p > 0){
			if(keep(c(p))){
				count+=1;
			}
			if(inc_children){
				count += countChildren(p,h, cnt);
			}
			p = Double.valueOf(h.get(p, 0)[0]).intValue();
		}
		// Look ahead
		int n;
		try {
			n = Double.valueOf(h.get(idx, 1)[0]).intValue();
		} catch (Exception e) {
			n = 0;
		}
		while(n > 0){
			if(keep(c(n))){
				count+=1;
			}
			if(inc_children){
				count += countChildren(p,h,cnt);
			}
			n = Double.valueOf(h.get(n, 1)[0]).intValue();
		}
		return count;
	}

	//	Quick check to test if the contour is a child
	private boolean isChild(int idx, Mat h, MatOfPoint cnt) {
		return (getParent(idx,h, cnt) > 0);
	}

	private boolean keep(MatOfPoint cnt) {
		if(cnt==null)return false;
		return keepBox(cnt) && connected(cnt);
	}

	private boolean keepBox(MatOfPoint cnt) {
		Rect rect = Imgproc.boundingRect(cnt);
		double w = rect.width;
		double h = rect.height;
		
		// Test it's shape - if it's too oblong or tall it's
		// probably not a real character
		if(w/h < 0.1 || w/h > 10){
//			if(DEBUGGING):
//				print "\t Rejected because of shape: ("+str(x)+","+str(y)+","+str(w)+","+str(h)+")" + str(w/h)
			return false;
		}
//					
		// Test whether the box is too wide 
		if(w > imgX/5){
//					if(DEBUGGING):
//						print "\t Rejected because of width: " + str(w)
			return false;
		}

				// Test whether the box is too tall 
		if(h > imgY/5){
//					if(DEBUGGING):
//						print "\t Rejected because of height: " + str(h)
			return false;
		}
		return true;
	}

	private boolean connected(MatOfPoint cnt) {
		double[] first = cnt.get(0, 0);
		double[] last = cnt.get(cnt.rows()-1, 0);
		return Math.abs(first[0] - last[0]) <= 1 && Math.abs(first[1] - last[1]) <= 1;
	}

	/*Determine pixel intensity
	  Apparently human eyes register colors differently.
	  TVs use this formula to determine
	  pixel intensity = 0.30R + 0.59G + 0.11B
	*/ 
	private double I(int x, int y) {
		if(y >= imgY || x >= imgX){
			System.out.println("pixel out of bounds ("+y+","+x+")");
			return 0;
		}
		double[] pixel = img.get(y, x);
		return 0.30 * pixel[2] + 0.59 * pixel[1]  + 0.11 * pixel[0];
	}
	
	public static void main(String[] args){
		SystemPathUtils.addJavaLibraryPath("/usr/local/Cellar/opencv/2.4.5/share/OpenCV/java");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		TextExtraction t = new TextExtraction();
		t.detect("/Users/houbin/Desktop/QQ20130528-3.png");
		
		Tesseract instance = Tesseract.getInstance();
		instance.setLanguage("jpn");
		try {
			String abc = instance.doOCR(new File("/Users/houbin/Desktop/QQ20130527-1.png"));
			System.out.println("Text:"+abc);
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
