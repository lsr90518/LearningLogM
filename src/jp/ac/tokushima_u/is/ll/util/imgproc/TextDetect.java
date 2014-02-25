package jp.ac.tokushima_u.is.ll.util.imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rewrite from https://github.com/dreamdragon/text-detection/blob/master/jni/text_detect.cpp
 * @author houbin
 *
 */
public class TextDetect {
	
	private static Logger logger = LoggerFactory.getLogger(TextDetect.class); 
	
	private Mat originalImage;
	private Mat image;//gray scale to be processed
	private Mat detection;
	private double maxStrokeWidth;
	private double initialStrokeWidth;
	private Mat edgemap;
	private Mat theta;
	private boolean firstPass;// white: 1, black: 0
	private List<Point> edgepoints = new ArrayList<>();
	
	private Mat correlation; // read from arg[1]
	private List<String> wordList = new ArrayList<>();//read from arg[2]
	private int mode;
	
	private List<Rect> boxesBothSides = new ArrayList<>();
	private List<String> wordsBothSides = new ArrayList<>();
	private List<Double> boxesScores = new ArrayList<>();
	
	private List<Boolean> boxInbox = new ArrayList<>();
	
	private int fontColor;
	private int result;
	
	private List<Rect> componentsRoi = new ArrayList<>();
	private boolean[] isLetterComponects;
	private boolean[] isGrouped;
	private List<boolean[]> innerComponents = new ArrayList<>();
	
	private List<Pair> horizontalLetterGroups = new ArrayList<>();
	private List<Pair> verticalLetterGroups = new ArrayList<>();
	private List<List<Integer>> horizontalChains = new ArrayList<>();
	private List<List<Integer>> verticalChains = new ArrayList<>();
	
	private String filename;
	private String outputPrefix;
	private int textDisplayOffset;
	private double minLetterHeight;
	private double maxLetterHeight;
	private int nComponent;
	private double[] componentsMeanIntensity;
	private double[] componentsMedianStokeWidth;
	private List<Rect> boundingBoxes = new ArrayList<>();
	
	public static final int MODE_IMAGE = 1;
	public static final int MODE_STREAM = 2;
	public static final int FONTCOLOR_BRIGHT = 1;
	public static final int FONTCOLOR_DARK = 2;
	public static final int PURPOSE_UPDATE = 1;
	public static final int PURPOSE_REFINE = 2;
	public static final int RESULT_COARSE = 1;
	public static final int RESULT_FINE = 2;
	
	
	public TextDetect(){
		this.maxStrokeWidth = 0;
		this.initialStrokeWidth = 0;
		this.firstPass = true;
		this.result = RESULT_COARSE;
		this.nComponent = 0;
		this.maxLetterHeight = 0;
		this.minLetterHeight = 0;
		this.textDisplayOffset = 1;
	}
	
	public void detect(String filename){
		this.filename = filename; 
		this.originalImage = Highgui.imread(filename);
		if(this.originalImage.empty()){
			throw new RuntimeException("Cannot read image input");
		}
		this.mode = MODE_IMAGE;
		detect();
	}
	
	public void detect(){
		long start_time = System.currentTimeMillis();
		long cost_time = 0;
		
		Mat imGray = new Mat(this.originalImage.size(), CvType.CV_8UC1, new Scalar(0));
		Imgproc.cvtColor(this.originalImage, imGray, Imgproc.COLOR_RGB2GRAY);
		this.boundingBoxes = new ArrayList<>();
		this.boxesBothSides = new ArrayList<>();
		this.wordsBothSides = new ArrayList<>();
		this.boxesScores = new ArrayList<>();
		
		preprocess(imGray);
		this.firstPass = true;
		pipeline(1);
		this.firstPass = false;
		pipeline(-1);
		
		overlapBoundingBoxes(this.boundingBoxes);
		ocrRead(this.boundingBoxes);
		showBoundingBoxes(this.boxesBothSides);
		overlayText(this.boxesBothSides, this.wordsBothSides);
		
		Highgui.imwrite(this.outputPrefix+"_detection.jpg", this.detection);
		
		cost_time = System.currentTimeMillis() - start_time;
		logger.debug(cost_time+" total in process");
		this.textDisplayOffset = 1;
	}

	private void preprocess(Mat image) {
		logger.debug("preprocessing:"+this.filename);
		logger.debug("image size:"+image.cols()+"X"+image.rows());
		
		this.outputPrefix = new File(this.filename).getParent()+"/";
		
		this.image = image;
		
		this.maxStrokeWidth = Math.round(20*(double)(Math.max(image.cols(), image.rows())));
		this.initialStrokeWidth = this.maxStrokeWidth * 2;
		this.maxLetterHeight = 600;
		this.minLetterHeight = 10;
		
//		Mat img2 = this.originalImage.clone();// ?
//		Mat img1 = new Mat(img2.size(), img2.type(), new Scalar(255, 255, 255));
//		img2.copyTo(img1);
		detection = this.originalImage.clone();// ?
	}
	
	private void pipeline(int blackWhite) {
		if(blackWhite == 1){
			this.fontColor = FONTCOLOR_BRIGHT;
		}else if(blackWhite == -1){
			this.fontColor = FONTCOLOR_DARK;
		}else{
			logger.error("blackwhite should only be +/-1");
			assert(false);
		}
		
		long startTime = System.currentTimeMillis();
		
		Mat swtmap = new Mat (this.image.size(), CvType.CV_32FC1, new Scalar(this.initialStrokeWidth));
		strokeWidthTransform(this.image, swtmap, blackWhite);
		logger.debug((System.currentTimeMillis()-startTime)+" in strokeWidthTransform");
		
		startTime = System.currentTimeMillis();
		Mat ccmap = new Mat (this.image.size(), CvType.CV_32FC1, new Scalar(-1));
		componentsRoi = new ArrayList<>();
		this.nComponent = connectComponentAnalysis(swtmap, ccmap);
		logger.debug((System.currentTimeMillis()-startTime)+" in connectComponentAnalysis");
		
		startTime = System.currentTimeMillis();
		identifyLetters(swtmap, ccmap);
		logger.debug((System.currentTimeMillis()-startTime)+" in identifyLetters");
		
		startTime = System.currentTimeMillis();
		groupLetters(swtmap, ccmap);
		logger.debug((System.currentTimeMillis()-startTime)+" in groupLetters");
		
		startTime = System.currentTimeMillis();
		chainPairs(ccmap);
		logger.debug((System.currentTimeMillis()-startTime)+" in chainPairs");
		
		/*
		 * showEdgeMap();
		 * showSwtmap(swtmap);
		 * showCcmap(ccmap);
		 * showLetterGroup();
		 */
	}
	
	private void chainPairs(Mat ccmap) {
		mergePairs(this.horizontalLetterGroups, this.horizontalChains);
		List<Rect> initialHorizontalBoxes = new ArrayList<>();
		chainToBox(this.horizontalChains, initialHorizontalBoxes);
		filterBoundingBoxes(initialHorizontalBoxes, ccmap, 4);
		this.boundingBoxes.addAll(initialHorizontalBoxes);
	}

	private void filterBoundingBoxes(List<Rect> boundingBoxes,
			Mat ccmap, int rejectRatio) {
		List<Rect> qualifiedBoxes = new ArrayList<>();
		List<Integer> components = new ArrayList<>();
		
		for(int i=0;i<boundingBoxes.size();i++){
			int isLetterCount = 0;
			int letterArea = 0;
			int nonLetterArea = 0;
			Rect rect = boundingBoxes.get(i);
			
			double width = rect.height;
			double height = rect.height;
			
			if(width<20)continue;
			if(Math.max(width, height)/Math.min(width, height)>20)continue;
			
			for(int y=rect.y; y<rect.y+rect.height;y++){
				for(int x=rect.x;x<rect.x+rect.width;x++){
					int componetIndex = Double.valueOf(ccmap.get(y, x)[0]).intValue();//TODO ?
					if(componetIndex<0)continue;
					
					if(this.isLetterComponects.length>componetIndex-1 && this.isLetterComponects[componetIndex]){
						letterArea++;
					}else{
						nonLetterArea++;
					}
					if(!components.contains(componetIndex)){
						components.add(componetIndex);
						if(this.isLetterComponects.length>componetIndex-1 && isLetterComponects[componetIndex]){
							isLetterCount++;
						}
					}
				}
			}
			
			// accept patch with few noise inside
			if(letterArea>3*nonLetterArea || components.size()<rejectRatio*isLetterCount){
				qualifiedBoxes.add(rect);
			}
			components.clear();
		}
		boundingBoxes = qualifiedBoxes;
	}

	private void chainToBox(List<List<Integer>> chain,
			List<Rect> boundingBox) {
		for(int i=0;i<chain.size();i++){
			if(chain.get(i).size()<3){
				continue;
			}
			
			int minX = this.image.cols(), minY = this.image.rows(), maxX = 0, maxY = 0;
			int letterAreaSum = 0;
			int padding = 5;
			for(int j=0;j<chain.get(i).size();j++){
				Rect itr = componentsRoi.get(chain.get(i).get(j));
				letterAreaSum +=itr.width*itr.height;
				minX = Math.min(minX, itr.x);
				minY = Math.min(minY, itr.y);
				maxX = Math.max(maxX, itr.x+itr.width);
				maxY = Math.max(maxY, itr.y+itr.height);
			}
			
			// add padding around each box
			minX = Math.max(0, minX - padding);
			minY = Math.max(0, minY - padding);
			maxX = Math.min(image.cols(), maxX + padding);
			maxY = Math.min(image.rows(), maxY + padding);
			
			boundingBox.add(new Rect(minX, minY, maxX-minX, maxY-minY));
		}
	}

	private void mergePairs(final List<Pair> groups,
			List<List<Integer>> chains) {
		List<List<Integer>> initialChains = new ArrayList<>();
		for(Pair pair: groups){
			List<Integer> temp = new ArrayList<>();
			temp.add(pair.left);
			temp.add(pair.right);
			initialChains.add(temp);
		}
		
		while(mergePairsInt(initialChains, chains)){
			initialChains = chains;
			chains.clear();
		}
	}

	private boolean mergePairsInt(final List<List<Integer>> initialChains,
			List<List<Integer>> chains) {
		if(chains.size()!=0){
			chains.clear();
		}
		
		boolean merged = false;
		List<Integer> mergedToChainBitMap = new ArrayList<>();
		for(int i=0;i<initialChains.size();i++){
			mergedToChainBitMap.add(-1);
		}
		
		for(int i=0;i<initialChains.size();i++){
			if(-1!=mergedToChainBitMap.get(i)){
				continue;
			}
			
			for(int j=i+1;j<initialChains.size();j++){
				for(int ki=0;ki<initialChains.get(i).size();ki++){
					for(int kj=0;kj<initialChains.get(j).size();kj++){
						if(initialChains.get(i).get(ki).equals(initialChains.get(j).get(kj))){
							merged = true;
							if(-1!=mergedToChainBitMap.get(j)){
								chains.get(mergedToChainBitMap.get(j)).addAll(initialChains.get(i));
								mergedToChainBitMap.set(i, mergedToChainBitMap.get(j));
							}else{
								List<Integer> newChain = new ArrayList<>();
								newChain.addAll(initialChains.get(i));
								newChain.addAll(initialChains.get(j));
								chains.add(newChain);
								mergedToChainBitMap.set(i, chains.size()-1);
								mergedToChainBitMap.set(j, chains.size()-1);
							}
							break;
						}
					}
					if(-1!=mergedToChainBitMap.get(i) && -1!=mergedToChainBitMap.get(j)){
						break;
					}
					
				}
			}
			
			if(-1==mergedToChainBitMap.get(i)){
				chains.add(initialChains.get(i));
				mergedToChainBitMap.set(i, chains.size()-1);
			}
		}
		
		if(!merged){
			chains = initialChains;
		}
		mergedToChainBitMap = null;
		return merged;
	}

	private void groupLetters(Mat swtmap, Mat ccmap) {
		this.componentsMeanIntensity = new double[this.nComponent];
		this.componentsMedianStokeWidth = new double[this.nComponent];
		
		this.isGrouped = new boolean[this.nComponent];
		
		Arrays.fill(this.componentsMeanIntensity, 0);
		Arrays.fill(this.componentsMedianStokeWidth, 0);
		Arrays.fill(this.isGrouped, false);
		
		Mat debug = this.originalImage.clone();
		
		for(int i=0;i<this.nComponent;i++){
			if(!this.isLetterComponects[i])
				continue;
			
			Rect iRect = this.componentsRoi.get(i);
			double iMeanIntensity = this.getMeanIntensity(ccmap, iRect, i);
			double iMedianStrokeWidth = this.getMedianStrokeWidth(ccmap, swtmap, iRect, i);
			
			for(int j = i+1;j<this.nComponent;j++){
				if(!this.isLetterComponects[j]){
					continue;
				}
				Rect jRect = this.componentsRoi.get(j);
				
				// check if horizontal
				boolean horizontal = !(iRect.y > jRect.y + jRect.height
						|| jRect.y > iRect.y + iRect.height);
				
				// check if vertical
				boolean vertical = !(iRect.x > jRect.x + jRect.width
						|| jRect.x > iRect.x + iRect.width);
				if ((!horizontal) && (!vertical))
					continue;
				
				// if there is a tie between horizontal/vertical
				if (horizontal && vertical) {
					if (Math.abs(
							(iRect.x + iRect.width / 2)
									- (jRect.x + jRect.width / 2))
							>= Math.abs(
									(iRect.y + iRect.height / 2)
											- (jRect.y + jRect.height / 2))) {
						horizontal = true;
						vertical = false;
					} else {
						horizontal = false;
						vertical = true;
					}
				}
				
				// rule 3: distance between characters
				double distance = Math.sqrt(
						Math.pow(
								(double) (iRect.x + iRect.width / 2 - jRect.x
										- jRect.width / 2), 2)
								+ Math.pow(
										(double) (iRect.y + iRect.height / 2
												- jRect.y - jRect.height / 2), 2));
				int distanceRatio = 4;
				if (horizontal) {
					if (distance / Math.max(iRect.width, jRect.width) > distanceRatio)
						continue;
				} else {
					if (distance / Math.max(iRect.height, jRect.height) > distanceRatio)
						continue;
				}
				
				double jMeanIntensity = getMeanIntensity(ccmap, jRect,j);
				double jMedianStrokeWidth = getMedianStrokeWidth(ccmap, swtmap,jRect, j);

				boolean isGroup = true;
				
				// rule 1: median of stroke width ratio
				isGroup = isGroup
						&& (Math.max(iMedianStrokeWidth, jMedianStrokeWidth)
								/ Math.min(iMedianStrokeWidth, jMedianStrokeWidth)) < 2;

				// rule 2: height ratio
				isGroup = isGroup
						&& (Math.max(iRect.height, jRect.height)
								/ Math.min(iRect.height, jRect.height)) < 2;
				
				// rule 4: average color of letters
				isGroup = isGroup && Math.abs(iMeanIntensity - jMeanIntensity) < 10;

				if (isGroup) {
					this.isGrouped[i] = true;
					this.isGrouped[j] = true;

					if (horizontal) {
						this.horizontalLetterGroups.add(new Pair(i, j));
					}

					if (vertical) {
						this.verticalLetterGroups.add(new Pair(i, j));
					}
				}
			}// end for loop j
		}// end for loop i
	}

	private double getMeanIntensity(Mat ccmap, Rect rect, int element) {
		assert(element>0);
		if(this.componentsMeanIntensity[element] == 0){
			double sum = 0;
			double count = 0;
			for(int y=rect.y; y<rect.y+rect.height;y++){
				for(int x=rect.x; x<rect.x+rect.width;x++){
					if(ccmap.get(y, x)[0]==(double)element){
						sum+=image.get(y, x)[0];
						count++;
					}
				}
			}
			this.componentsMeanIntensity[element] = sum/count;
		}
		return this.componentsMeanIntensity[element];
	}
	
	private double getMedianStrokeWidth(Mat ccmap, Mat swtmap, Rect rect, int element) {
		assert(element >0);
		assert(this.isLetterComponects[element]);
		if(this.componentsMedianStokeWidth[element]==0){
			List<Double> swtValues = new ArrayList<>();
			for(int y=rect.y;y<rect.y+rect.height;y++){
				for(int x=rect.x;x<rect.x+rect.width;x++){
					if(ccmap.get(y, x)[0]==(double)element){
						swtValues.add(Double.valueOf(swtmap.get(y, x)[0]).doubleValue());
					}
				}
			}
			Collections.sort(swtValues);
			this.componentsMedianStokeWidth[element] = swtValues.get(swtValues.size()/2);
		}
		return this.componentsMedianStokeWidth[element];
	}

	private void identifyLetters(Mat swtmap, Mat ccmap) {
		assert(this.nComponent == this.componentsRoi.size());
		this.isLetterComponects = new boolean[this.nComponent];
		List<Double> iComponentStrokeWidth = new ArrayList<>();
		System.out.println( this.nComponent + " componets");
		boolean[] innerComponents = new boolean[this.nComponent];
		
		for (int i = 0; i < this.nComponent; i++) {
			double maxStrokeWidth = 0;
			double sumStrokeWidth = 0;
			double currentStrokeWidth;
			boolean isLetter = true;

			Rect itr = this.componentsRoi.get(i);
			if (itr.height > this.maxLetterHeight || itr.height < this.minLetterHeight) {
				this.isLetterComponects[i] = false;
				continue;
			}
			int maxY = itr.y + itr.height;
			int minY = itr.y;
			int maxX = itr.x + itr.width;
			int minX = itr.x;
			double increment = Math.abs(itr.width - itr.height) / 2;

			// reset the inner components
			Arrays.fill(innerComponents, false);

			if (itr.width > itr.height) {// increase box height
				maxY = Double.valueOf(Math.min(maxY + increment, (double)ccmap.rows())).intValue();
				minY = Double.valueOf(Math.max(minY - increment, 0f)).intValue();
			} else // increase box width
			{
				maxX = Double.valueOf(Math.min(maxX + increment, (double)ccmap.cols())).intValue();
				minX = Double.valueOf(Math.max(minX - increment, 0f)).intValue();
			}

			for (int y = minY; y < maxY; y++)
				for (int x = minX; x < maxX; x++) {
					int component = Double.valueOf(ccmap.get(y, x)[0]).intValue();
					if (component == i) {
						currentStrokeWidth = swtmap.get(y, x)[0];
						iComponentStrokeWidth.add(currentStrokeWidth);
						maxStrokeWidth = Math.max(maxStrokeWidth, currentStrokeWidth);
						sumStrokeWidth += currentStrokeWidth;
					} else {
						if (component >= 0) {
							innerComponents[component] = true;
						}
					}
				}

			double pixelCount = iComponentStrokeWidth.size();
			double mean = sumStrokeWidth / pixelCount;
			double variance = 0;
			for (int ii = 0; ii < pixelCount; ii++) {
				variance += Math.pow(iComponentStrokeWidth.get(ii) - mean, 2);
			}
			variance = variance / pixelCount;

			// rules & parameters goes here:

			isLetter = isLetter && (variance / mean < 1.5);

			isLetter = isLetter
					&& (Math.sqrt(
							(Math.pow((double) itr.width, 2)
									+ Math.pow((double) itr.height, 2)))
							/ maxStrokeWidth < 10);

			// additional rules:
			isLetter = isLetter && (pixelCount / maxStrokeWidth > 5);

			isLetter = isLetter && (itr.width < 2.5 * itr.height);

			if (countInnerLetterCandidates(innerComponents) - 1 > 5) {
				isLetter = false;
			}

			this.isLetterComponects[i] = isLetter;

			iComponentStrokeWidth.clear();
		}
		innerComponents = null;
	}

	private int countInnerLetterCandidates(boolean[] array) {
		int count = 0;
		for(int i=0;i<array.length;i++){
			if(array[i] && this.isLetterComponects[i]){
				count++;
			}
		}
		return count;
	}

	private int connectComponentAnalysis(Mat swtmap, Mat ccmap) {
		int ccmapInitialVal = Double.valueOf(ccmap.get(0, 0)[0]).intValue();
		int offsetY[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int offsetX[] = { -1, 0, 1, -1, 1, -1, 0, 1 };
		int nNeighbors = 8;
		int label = 0;

		int vectorSize = ccmap.rows() * ccmap.cols();

		int[] pStack = new int[vectorSize * 2];
		int stackPointer = 0;

		int[] pVector = new int[vectorSize * 2];
		int vectorPointer = 0;

		int currentPointX = 0;
		int currentPointY = 0;

		for (int y = 0; y < ccmap.rows(); y++) {
			for (int x = 0; x < ccmap.cols(); x++) {
				boolean connected = false;
				if (ccmap.get(y, x)[0] == ccmapInitialVal) {
					vectorPointer = 0;
					stackPointer = 0;
					pStack[stackPointer] = x;
					pStack[stackPointer + 1] = y;
					while (stackPointer >= 0) {
						currentPointX = pStack[stackPointer];
						currentPointY = pStack[stackPointer + 1];
						stackPointer -= 2;

						pVector[vectorPointer] = currentPointX;
						pVector[vectorPointer + 1] = currentPointY;
						vectorPointer += 2;

						for (int i = 0; i < nNeighbors; i++) {
							int ny = currentPointY + offsetY[i];
							int nx = currentPointX + offsetX[i];
							if (ny < 0 || nx < 0 || ny >= ccmap.rows()
									|| nx >= ccmap.cols())
								continue;

							if (swtmap.get(ny, nx)[0] == 0) {
								ccmap.put(ny, nx, -2);
								continue;
							}

							if (ccmap.get(ny, nx)[0] == ccmapInitialVal) {
								double sw1 = swtmap.get(ny, nx)[0];
								double sw2 = swtmap.get(y, x)[0];

								if (Math.max(sw1, sw2) / Math.min(sw1, sw2) <= 3) {
									ccmap.put(ny, nx, label);
									stackPointer += 2;
									if(stackPointer+1 > pStack.length-1) continue;
									try {
										pStack[stackPointer] = nx;
										pStack[stackPointer + 1] = ny;
									} catch (Exception e) {
										System.out.println("stackPointer:" +stackPointer);
										throw new RuntimeException(e);
									}
									connected = true;
								}
							}
						} // loop through neighbors

					}

					if (connected) {
						//	assert(vectorPointer <= vectorSize);
						//	assert(vectorPointer > 0);

						int minY = ccmap.rows(), minX = ccmap.cols(), maxY = 0,
								maxX = 0;
						int width, height;
						for (int i = 0; i < vectorPointer; i += 2) {
							// ROI for each component
							minY = Math.min(minY, pVector[i + 1]);
							minX = Math.min(minX, pVector[i]);
							maxY = Math.max(maxY, pVector[i + 1]);
							maxX = Math.max(maxX, pVector[i]);
						}
						width = maxX - minX + 1;
						height = maxY - minY + 1;
						Rect letterRoi = new Rect(minX, minY, width, height);
						this.componentsRoi.add(letterRoi);
						//assert(label == componentsRoi_.size()-1);

						label++;

					} else {
						ccmap.put(y, x, -2);
					}
				}
			} // loop through ccmap
		}

		pStack = null;
		pVector = null;

		return label;
	}

	private void strokeWidthTransform(Mat image, Mat swtmap, int searchDirection) {
		if(this.edgemap == null) this.edgemap = new Mat();
		if (this.firstPass) {
			// compute edge map
			Imgproc.Canny(this.image, this.edgemap, 50, 120);

			//compute gradient direction
			Mat dx = new Mat(), dy = new Mat();
			
			Imgproc.Sobel(this.image, dx, CvType.CV_32FC1, 1, 0/*, 3*/);
			Imgproc.Sobel(this.image, dy, CvType.CV_32FC1, 0, 1/*, 3*/);

			this.theta = new Mat(this.image.size(), CvType.CV_32FC1);

			if (this.edgepoints.size()!=0) {
				this.edgepoints.clear();
			}

			for (int y = 0; y < this.edgemap.rows(); y++) {
				for (int x = 0; x < this.edgemap.cols(); x++) {
					if (this.edgemap.get(y, x)[0] == 255) {
						this.theta.put(y, x, Math.atan2(dy.get(y, x)[0],
								dx.get(y, x)[0]));
						this.edgepoints.add(new Point(x, y));
					}
				}
			}
		}

		List <Point> strokePoints = new ArrayList<>();
		updateStrokeWidth(swtmap, this.edgepoints, strokePoints, searchDirection,
				PURPOSE_UPDATE);

		updateStrokeWidth(swtmap, strokePoints, strokePoints, searchDirection,
				PURPOSE_REFINE);
	}

	private void updateStrokeWidth(Mat swtmap, List<Point> startPoints,
			List<Point> strokePoints, int searchDirection, int purpose) {
		//loop through all edgepoints, compute stroke width
		List <Point> pointStack = new ArrayList<>();
		List<Double> SwtValues = new ArrayList<>();
		
		for (Point itr: startPoints) {
			pointStack.clear();
			SwtValues.clear();
			int step = 1;
			double iy = itr.y;
			double ix = itr.x;
			double currY = iy;
			double currX = ix;
			boolean isStroke = false;
			double iTheta = this.theta.get(Double.valueOf(itr.y).intValue(), Double.valueOf(itr.x).intValue())[0];
			pointStack.add(new Point(currX, currY));
			SwtValues.add(swtmap.get(Double.valueOf(currY).intValue(), Double.valueOf(currX).intValue())[0]);
			while (step < this.maxStrokeWidth) {
				int nextY = (int) Math.round(iy + Math.sin(iTheta) * searchDirection * step);
				int nextX = (int) Math.round(ix + Math.cos(iTheta) * searchDirection * step);

				if (nextY < 0 || nextX < 0 || nextY >= this.edgemap.rows()
						|| nextX >= this.edgemap.cols())
					break;

				step = step + 1;
				if (currY == nextY && currX == nextX)
					continue;

				currY = nextY;
				currX = nextX;

				pointStack.add(new Point(currX, currY));
				SwtValues.add(swtmap.get(Double.valueOf(currY).intValue(), Double.valueOf(currX).intValue())[0]);

				if (this.edgemap.get(Double.valueOf(currY).intValue(), Double.valueOf(currX).intValue())[0] == 255) {
					double jTheta = this.theta.get(Double.valueOf(currY).intValue(), Double.valueOf(currX).intValue())[0];
					if (Math.abs(Math.abs(iTheta - jTheta) - 3.14) < 3.14 / 2) {
						isStroke = true;
						if (purpose == PURPOSE_UPDATE) {
							strokePoints.add(new Point(ix, iy));
						}
					}
					break;
				}
			}

			if (isStroke) {
				double newSwtVal = 0;
				if (purpose == PURPOSE_UPDATE){ // update swt based on dist between edges
					newSwtVal = Math.sqrt(
							(currY - iy) * (currY - iy)
									+ (currX - ix) * (currX - ix));
				} else if (purpose == PURPOSE_REFINE) { // refine swt based on median
					Collections.sort(SwtValues);
					newSwtVal = SwtValues.get(SwtValues.size() / 2);
				}

				for (int i = 0; i < pointStack.size(); i++) {
					Point p = pointStack.get(i);
					swtmap.get(Double.valueOf(p.y).intValue(), Double.valueOf(p.x).intValue())[0] = Math.min(swtmap.get(Double.valueOf(p.y).intValue(), Double.valueOf(p.x).intValue())[0], newSwtVal);
				}
			}

		} // end loop through edge points

		// set initial upchanged value back to 0

		for (int y = 0; y < swtmap.rows(); y++) {
			for (int x = 0; x < swtmap.cols(); x++) {
				if (swtmap.get(y, x)[0] == this.initialStrokeWidth) {
					swtmap.put(y, x, 0);
				}
			}
		}		
	}

	private void overlapBoundingBoxes(List<Rect> boundingBoxes) {
		List < Rect > bigBoxes = new ArrayList<>();

		Mat tempMap = new Mat(this.image.size(), CvType.CV_32FC1, new Scalar(0));
		for (int i = 0; i < boundingBoxes.size(); i++) {
			Rect rect = boundingBoxes.get(i);
			for (int y = rect.y; y < rect.y + rect.height; y++)
				for (int x = rect.x; x < rect.x + rect.width; x++) {
					tempMap.put(y, x, 50);
				}
		}

		for (int i = 0; i < boundingBoxes.size(); i++) {
			if (tempMap.get(boundingBoxes.get(i).y + 1, boundingBoxes.get(i).x + 1)[0]
					!= 50)
				continue;

			Rect rect = boundingBoxes.get(i);
//			Imgproc.floodFill(tempMap, new Point(boundingBoxes.get(i).x, boundingBoxes.get(i).y),
//					i + 100, rect);
			Imgproc.floodFill(tempMap, new Mat(), new Point(boundingBoxes.get(i).x, boundingBoxes.get(i).y), new Scalar(i+100), rect, new Scalar(0), new Scalar(0), 4);

			int padding = 5;

			// add padding around each box
			int minX = Math.max(0, rect.x - padding);
			int minY = Math.max(0, rect.y - padding);
			int maxX = Math.min(this.image.cols(), rect.x + rect.width + padding);
			int maxY = Math.min(this.image.rows(), rect.y + rect.height + padding);

			bigBoxes.add(new Rect(minX, minY, maxX - minX, maxY - minY));
		}
		boundingBoxes = bigBoxes;
	}
	
	private void ocrRead(List<Rect> boundingBoxes) {
		Collections.sort(boundingBoxes, new Comparator<Rect>(){
			@Override
			public int compare(Rect a, Rect b) {
				return a.y-b.y;
			}
		});
		
		for (int i = 0; i < boundingBoxes.size(); i++) {
			String result = "";
			double score = ocrRead(this.originalImage.submat(boundingBoxes.get(i)), result);
			if (score > 0) {
				this.boxesBothSides.add(boundingBoxes.get(i));
				this.wordsBothSides.add(result);
				this.boxesScores.add(score);
			}
		}
	}
	
	private double ocrRead(Mat submat, String result2) {
		float score = 0;
//		Mat scaledImage;
//		if (imagePatch.rows < 30)
//		{
//			double scale = 1.5;
//			resize(imagePatch, scaledImage, Size(0,0),
//				scale, scale, INTER_LANCZOS4);
	//
//			//imwrite("patch.tiff", scaledImage);
//		}

//		tesseract::TessBaseAPI tess;
//		tess.Init(NULL, "eng");
//		tess.SetImage((uchar*)imagePatch.data, imagePatch.size().width, imagePatch.size().height, imagePatch.channels(), imagePatch.step1());
//		tess.Recognize(0);
//		char* out = tess.GetUTF8Text();
	//
//		string str(out);
//		string buf;
//		stringstream ss(str);
	//
//		vector<string> tokens;
	//
//		while (ss >> buf)
//			tokens.push_back(buf);
	//
//		for(std::vector<int>::size_type i = 0; i != tokens.size(); i++) {
//			string tempOutput;
//			score += spellCheck(tokens[i], tempOutput, 2);
//			output += tempOutput;
//		}

		return score;
	}

	private void showBoundingBoxes(List<Rect> boxesBoxes) {
		Scalar scalar = new Scalar(0, 0, 255);

		for (int i = 0; i < boundingBoxes.size(); i++) {
			Rect rect = boundingBoxes.get(i);
			Core.rectangle(this.detection, new Point(rect.x, rect.y),
					new Point(rect.x + rect.width, rect.y + rect.height), scalar,
					3);
		}
	}
	
	private void overlayText(List<Rect> box,
			List<String> text) {
		assert(box.size() == text.size());
		Scalar color = new Scalar(0, 255, 0);
		int lineWidth = 25;
		int indent = 50;
		int count = 1;
		for (int i = 0; i < box.size(); i++) {
			if (count > 9)
				indent = 70;
			String output = text.get(i);
			if (StringUtils.isBlank(output))
				continue;
			String s = "";
			StringBuffer out = new StringBuffer("");
			out.append(count);
			count++;
			String prefix = "[";
			prefix = prefix + out.toString() + "]";
			Core.putText(this.detection, prefix,
					new Point(box.get(i).x + box.get(i).width, box.get(i).y + box.get(i).height),
					Core.FONT_HERSHEY_DUPLEX, 1, color, 2);
			Core.putText(this.detection, prefix, new Point(this.image.cols(), this.textDisplayOffset * 35),
					Core.FONT_HERSHEY_DUPLEX, 1, color, 2);
			while (output.length() > lineWidth) {
				Core.putText(this.detection, output.substring(0, lineWidth),
						new Point(this.image.cols() + indent, this.textDisplayOffset * 35),
						Core.FONT_HERSHEY_DUPLEX, 1, color, 2);
				output = output.substring(lineWidth);
				this.textDisplayOffset++;
			}
			Core.putText(this.detection, output,
					new Point(this.image.cols() + indent, this.textDisplayOffset * 35),
					Core.FONT_HERSHEY_DUPLEX, 1, color, 2);
			this.textDisplayOffset += 2;
		}
	}


	public List<Rect> getBoundingBoxes(Mat image){
		this.filename = "streaming.jpg";
		this.originalImage = image;
		this.mode = MODE_STREAM;

		Mat imGray = new Mat(this.originalImage.size(), CvType.CV_8UC1, new Scalar(0));
		Imgproc.cvtColor(this.originalImage, imGray, Imgproc.COLOR_RGB2GRAY);

		this.boundingBoxes = new ArrayList<>();
		this.boxesBothSides = new ArrayList<>();
		this.wordsBothSides = new ArrayList<>();
		this.boxesScores = new ArrayList<>();

		preprocess(imGray);
		this.firstPass = true;
		pipeline(1);
		this.firstPass = false;
		pipeline(-1);

		overlapBoundingBoxes(this.boundingBoxes);

		return this.boundingBoxes;
	}
	
	/*--------------------------------------------------------*\
	 *	display functions
	 \*--------------------------------------------------------*/
	
	public void showEdgeMap() {
		if (this.firstPass)
			Highgui.imwrite("edgemap.png", edgemap);
	}
	
	public void showLetterDetection() {
		Mat output = this.originalImage.clone();
		Scalar scalar;
		if (this.firstPass)
			scalar = new Scalar(0, 255, 0);
		else
			scalar = new Scalar(0, 0, 255);

		for (int i = 0; i < nComponent; ++i) {
			if (this.isLetterComponects[i]) {
				Rect itr = componentsRoi.get(i);
				Core.rectangle(output, new Point(itr.x, itr.y),
						new Point(itr.x + itr.width, itr.y + itr.height), scalar,
						2);
				StringBuffer ss = new StringBuffer();
				String s = "";
				ss.append(i);
				s = ss.toString() + ".tiff";
				Highgui.imwrite("/Users/houbin/Desktop/"+s, originalImage.submat(itr));
			}
		}
		if (this.firstPass)
			Highgui.imwrite("/Users/houbin/Desktop/" + "_letters1.jpg", output);
		else
			Highgui.imwrite("/Users/houbin/Desktop/" + "_letters2.jpg", output);
	}
	
	public static void main(String[] args){
		Tesseract instance = Tesseract.getInstance();
		instance.setLanguage("chi_sim");
		try {
			String abc = instance.doOCR(new File("/Users/houbin/Desktop/QQ20130528-3.png"));
			System.out.println(abc);
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextDetect detect = new TextDetect();
		detect.detect("/Users/houbin/Desktop/321.jpg");
		//detect.showLetterDetection();
	}
	
	public class Pair{
		private int left;
		private int right;
		public Pair(int left, int right){
			this.left = left;
			this.right = right;
		}
		public int getLeft() {
			return left;
		}
		public void setLeft(int left) {
			this.left = left;
		}
		public int getRight() {
			return right;
		}
		public void setRight(int right) {
			this.right = right;
		}
	}
}
